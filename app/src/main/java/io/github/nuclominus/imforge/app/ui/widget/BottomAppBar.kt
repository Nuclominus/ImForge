package io.github.nuclominus.imforge.app.ui.widget

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomAppBar(
    countFiles: Int,
    galleryResult: ActivityResultLauncher<PickVisualMediaRequest>,
    onOpenSettings: () -> Unit,
    onDeleteCache: () -> Unit,
) {
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    androidx.compose.material3.BottomAppBar {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmallFloatingActionButton(
                onClick = onOpenSettings
            ) {
                Icon(imageVector = Icons.Outlined.Settings, contentDescription = "settings")
            }

            FloatingActionButton(
                onClick = {
                    galleryResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
            ) {
                Icon(Icons.Filled.Add, null)
            }

            SmallFloatingActionButton(
                onClick = {
                    showDeleteDialog = true
                }
            ) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "delete content")
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                dialogTitle = "Remove files",
                dialogText = "Do you want remove all stored files?",
                onDismissRequest = {
                    showDeleteDialog = false
                }
            ) {
                onDeleteCache.invoke()
                showDeleteDialog = false
            }
        }
    }
}