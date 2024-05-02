package io.github.nuclominus.example.ui.widget

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import io.github.nuclominus.example.MainActivity
import io.github.nuclominus.example.viewmodel.ListViewModel
import java.lang.ref.WeakReference

@Composable
fun BoxScope.ImageIdleWidget() {

    val viewModel: ListViewModel = hiltViewModel()
    val contextRef = WeakReference(LocalContext.current)
    val galleryResult =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            contextRef.get()?.let {
                viewModel.optimizeImage(it, uri)
            }
        }


    Surface(
        modifier = Modifier
            .size(200.dp)
            .align(Alignment.Center),
        onClick = {
            galleryResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap to select image",
            )
        }
    }
}