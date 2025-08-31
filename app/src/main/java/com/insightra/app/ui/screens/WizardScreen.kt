package com.insightra.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.insightra.app.viewmodel.WizardViewModel

@Composable
fun WizardScreen(
    onComparisonReady: (Long) -> Unit,
    vm: WizardViewModel = hiltViewModel()
) {
    val state = vm.state

    Column {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { vm.takePhoto() }) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                Text("Camera")
            }
            Button(onClick = { vm.pickFromGallery() }) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                Text("Gallery")
            }
        }

        Spacer(Modifier.height(16.dp))

        state.items.forEachIndexed { index, item ->
            Row {
                Image(
                    painter = rememberAsyncImagePainter(model = item.imageUri),
                    contentDescription = null
                )
                Column {
                    OutlinedTextField(value = item.brand, onValueChange = { vm.updateBrand(index, it) }, label = { Text("Brand") })
                    OutlinedTextField(value = item.name, onValueChange = { vm.updateName(index, it) }, label = { Text("Name") })
                    OutlinedTextField(value = item.size, onValueChange = { vm.updateSize(index, it) }, label = { Text("Size") })
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Row {
            Button(onClick = { vm.saveCurrentItem() }, enabled = state.canSaveCurrent) {
                Text("Save Item")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.compare(onComparisonReady) }, enabled = state.items.size >= 2) {
                Text("Compare")
            }
        }
    }
}
