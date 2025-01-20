package com.example.bikeputernew.ui.screens


import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import com.example.bikeputernew.R
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.datastore.datastore.StoreBikeputerData
import com.example.bikeputernew.ui.theme.BikeButerBetterTheme
import com.example.bikeputernew.values.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

val TAG = "SettingsScreen"

@Composable
fun SettingsScreen(){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreBikeputerData(context = context)

    BikeButerBetterTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        maxLines = 1,
                        color = MaterialTheme.colors.primary
                    )
                },
                backgroundColor = MaterialTheme.colors.background
            )
        },
            content = {
                Column(Modifier.padding(10.dp)) {
                    DeviceNameInput(
                        scope = scope,
                        dataStore = dataStore
                    )
                    DatabaseUserName(
                        scope = scope,
                        dataStore = dataStore
                    )
                }
            }
        )
    }
}

@Composable
fun SettingsNonEdit(field: String, label: String, edit: () -> Unit) {
    Row(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(
                text = "$label:",
                textDecoration = TextDecoration.Underline
            )
            Text(
                text = field
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = edit
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Edit"
            )
        }
    }
}

@Composable
fun SettingsEdit(input: String, label: String, setInput: (text: String) -> Unit) {
    var inputText by  remember {
        mutableStateOf(input)
    }
    Row(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text(text = label)}
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {
            setInput(inputText)
        }) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "ustaw"
            )
        }

    }
}

@Composable
@Preview
fun SettingsEditPreview() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        SettingsEdit("moje imie", "labelka") { text -> println(text) }
    }
}

@Composable
@Preview
fun SettingsNonEditPreview() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        SettingsNonEdit("Moje imie", "labelka") { }
    }
}

@Composable
fun SettingsField(field: String, label: String, edit: (text: String) -> Unit) {
    var editMode by remember {
        mutableStateOf(false)
    }
    if(editMode) {
        SettingsEdit(field, label) { text ->
            Log.i("AAA", "Saving $text")
            edit(text)
            editMode = false
        }
    } else {
        SettingsNonEdit(field, label) {
            editMode = true
        }
    }
}

@Composable
fun DeviceNameInput(scope: CoroutineScope, dataStore: StoreBikeputerData) {
    val savedDeviceName by dataStore.getDeviceName.collectAsState(initial = Constants.DEVICE_DEFAULT_NAME)
    SettingsField(savedDeviceName, stringResource(id = R.string.device_name)) { deviceName ->
        Log.i("AAA", "Saving $deviceName")
        scope.launch {
            dataStore.saveDeviceName(deviceName)
        }
    }
}


@Composable
fun DatabaseUserName(scope: CoroutineScope, dataStore: StoreBikeputerData) {
    val savedDbUser by dataStore.getDbUser.collectAsState(initial = Constants.DEFAULT_DB_USER)
    SettingsField(savedDbUser, stringResource(id = R.string.db_user)) { user ->
        Log.i("AAA", "Saving $user")
        scope.launch {
            dataStore.saveDbUser(
                user
            )
        }
    }
}