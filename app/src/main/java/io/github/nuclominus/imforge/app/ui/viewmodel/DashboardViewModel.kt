package io.github.nuclominus.imforge.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.nuclominus.imagecompressor.ImageOptimizer
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity
import io.github.nuclominus.imforge.app.core.usecase.DeleteImageCacheUseCase
import io.github.nuclominus.imforge.app.core.usecase.GetImageListUseCase
import io.github.nuclominus.imforge.app.core.usecase.OptimizeImageUseCase
import io.github.nuclominus.imforge.app.ui.state.SideEffect
import io.github.nuclominus.imforge.app.ui.widget.Resolution
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    imageListUseCase: GetImageListUseCase,
    private val optimizeImageUseCase: OptimizeImageUseCase,
    private val deleteImageCacheUseCase: DeleteImageCacheUseCase,
) : ViewModel() {

    private val _sideEffect = MutableSharedFlow<SideEffect?>(
        replay = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sideEffect: SharedFlow<SideEffect?> = _sideEffect

    val details: Flow<List<ImageDetailsEntity>> = imageListUseCase()
        .distinctUntilChanged()
        .onEach {
            // scroll to top when new items are added
            _sideEffect.emit(SideEffect.ScrollTo(0))
        }

    private val _config = MutableStateFlow(ImageOptimizer.Configuration())
    val config: StateFlow<ImageOptimizer.Configuration> = _config.asStateFlow()

    fun setConfig(config: ImageOptimizer.Configuration) = viewModelScope.launch {
        _config.emit(config)
    }

    fun optimizeImage(uri: Uri?) = viewModelScope.launch {
        optimizeImageUseCase(uri, _config.value)
    }

    fun processData(intent: Intent?) = viewModelScope.launch {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        } ?: return@launch

        _sideEffect.emit(SideEffect.ShowResolutionPicker(uri))
    }

    fun deleteCache() {
        deleteImageCacheUseCase()
    }

    fun getImageResolution(context: Context?, uri: Uri) : Result<Resolution> = runCatching {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val inputStream = context?.contentResolver?.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream, null, options)
        return Result.success(options.outWidth to options.outHeight)
    }
}