package io.github.nuclominus.imforge.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.nuclominus.imforge.app.core.usecase.GetImageDetailsUseCase
import io.github.nuclominus.imforge.app.ui.model.OptimizedImageModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailsViewModel.DetailsViewModelFactory::class)
class DetailsViewModel @AssistedInject constructor(
    @Assisted private val modelId: String,
    private val dataUseCase: GetImageDetailsUseCase,
) : ViewModel() {

    private val _model = MutableStateFlow<OptimizedImageModel?>(null)
    val model: StateFlow<OptimizedImageModel?> = _model

    init {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            val model = dataUseCase(modelId)
            _model.emit(model)
        }
    }

    @AssistedFactory
    interface DetailsViewModelFactory {
        fun create(modelId: String): DetailsViewModel
    }
}