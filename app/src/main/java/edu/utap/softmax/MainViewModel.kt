package edu.utap.softmax

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val softmaxClient = SoftmaxClient.create()

    private val models = MutableLiveData<List<SoftmaxClient.Model>>()
    private val model = MutableLiveData<SoftmaxClient.Model>()
    private val run = MediatorLiveData<SoftmaxClient.Run>().apply {
        addSource(model) {
            updateRun()
        }
    }
    private var runNum = 0

    fun fetchModels() = viewModelScope.launch(
        context = viewModelScope.coroutineContext + Dispatchers.IO
    ) {
        models.postValue(softmaxClient.getModels())
    }

    fun fetchModel(modelId: String) = viewModelScope.launch(
        context = viewModelScope.coroutineContext + Dispatchers.IO
    ) {
        model.postValue(softmaxClient.get(modelId))
    }

    fun nextRun() {
        val totalRuns = model.value?.runs?.size ?: 0
        runNum = (runNum + 1).mod(totalRuns)
        updateRun()
    }

    fun prevRun() {
        val totalRuns = model.value?.runs?.size ?: 0
        runNum = (runNum - 1).mod(totalRuns)
        updateRun()
    }

    private fun updateRun() {
        val runs = model.value?.runs
        runs?.let {
            if (runNum > runs.size)
                runNum = runs.size - 1
            run.value = runs[runNum]
        }
    }

    fun observeModels(): LiveData<List<SoftmaxClient.Model>> = models

    fun observeModel(): LiveData<SoftmaxClient.Model> = model

    fun observeRun(): LiveData<SoftmaxClient.Run> = run
}