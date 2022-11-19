package edu.utap.softmax

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val softmaxClient = SoftmaxClient.create()

    fun fetchModels() = viewModelScope.launch(
        context = viewModelScope.coroutineContext + Dispatchers.IO
    ) {
        val models = softmaxClient.getModels()
    }
}