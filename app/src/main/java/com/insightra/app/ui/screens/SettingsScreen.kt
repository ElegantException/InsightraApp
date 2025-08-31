package com.insightra.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.insightra.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    padding: PaddingValues,
    vm: SettingsViewModel = hiltViewModel()
) {
    val saved = vm.savedInfo
    val editing = remember { mutableStateOf(saved == null) }
    val apiKey = remember { mutableStateOf("") }

    LaunchedEffect(saved) {
        if (saved != null) {
            apiKey.value = ""
            editing.value = false
        }
    }

    Column(
        verticalArrangement = Arrangement.Top
    ) {
        if (!editing.value && saved != null) {
            Column {
                Text("Saved API Key — ${saved.date}")
                IconButton(onClick = { editing.value = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        } else {
            Column {
                OutlinedTextField(
                    value = apiKey.value,
                    onValueChange = { apiKey.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("OpenAI API Key") }
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = {
                    vm.verifyAndSave(apiKey.value, onError = {}, onSuccess = {
                        editing.value = false
                    })
                }) {
                    Text("Verify & Save")
                }
            }
        }
    }
}
