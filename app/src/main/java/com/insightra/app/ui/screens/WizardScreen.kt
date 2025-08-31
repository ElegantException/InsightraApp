package com.insightra.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) vm.addImage(uri)
    }

    Column {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                // Camera flow can be added; for now, use gallery to unblock
                galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }, enabled = state.items.size < 3) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                Text("Camera")
            }
            Button(onClick = {
                galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }, enabled = state.items.size < 3) {
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
                IconButton(onClick = { vm.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Row {
            Button(onClick = { vm.compare(onComparisonReady) }, enabled = state.canCompare) {
                Text("Compare")
            }
        }
    }
}
