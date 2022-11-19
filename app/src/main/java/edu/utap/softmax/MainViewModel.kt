package edu.utap.softmax

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val softmaxClient = SoftmaxClient.create()

    private val models = MutableLiveData<List<SoftmaxClient.Model>>()

    fun fetchModels() = viewModelScope.launch(
        context = viewModelScope.coroutineContext + Dispatchers.IO
    ) {
        models.postValue(softmaxClient.getModels())
    }

    fun fetchModel(modelId: String) = viewModelScope.launch(
        context = viewModelScope.coroutineContext + Dispatchers.IO
    ) {
        val model = softmaxClient.get(modelId)
    }

    fun observeModels(): LiveData<List<SoftmaxClient.Model>> = models
}