package edu.utap.softmax

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private var softmaxServerAddress = MutableLiveData("http://jerryr.us")
    private var softmaxServerPort = MutableLiveData(23800)
    private var softmaxUpdateSeconds = MutableLiveData(2)
    private var softmaxClient = SoftmaxClient.create()

    private val models = MutableLiveData<List<SoftmaxClient.Model>>()
    private val model = MutableLiveData<SoftmaxClient.Model>()
    private val run = MediatorLiveData<SoftmaxClient.Run>().apply {
        addSource(model) {
            updateRun()
        }
    }
    private var runNum = 0
    var enabledRuns: MutableMap<String, Boolean> = mutableMapOf()

    var lossEnabled = true
    set(value) {
        field = value
        model.value = model.value
    }

    var accuracyEnabled = true
    set(value) {
        field = value
        model.value = model.value
    }

    fun getRunNum() = runNum

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

    fun setRunEnabled(run: SoftmaxClient.Run, enabled: Boolean) {
        enabledRuns[run.runId] = enabled
        model.value = model.value
    }

    fun isRunEnabled(run: SoftmaxClient.Run): Boolean {
        return enabledRuns.getOrDefault(run.runId, true)
    }

    fun disableAllRuns() {
        model?.value?.runs?.forEach { run ->
            enabledRuns[run.runId] = false
        }
    }

    fun observeModels(): LiveData<List<SoftmaxClient.Model>> = models

    fun observeModel(): LiveData<SoftmaxClient.Model> = model

    fun observeRun(): LiveData<SoftmaxClient.Run> = run

    fun observeServerAddress(): LiveData<String> = softmaxServerAddress

    fun observeServerPort(): LiveData<Int> = softmaxServerPort

    fun observeUpdateSeconds(): LiveData<Int> = softmaxUpdateSeconds

    fun setServerAddress(serverAddress: String) {
        softmaxServerAddress.value = serverAddress
        println("new address $serverAddress")
    }

    fun setServerPort(serverPort: Int) {
        softmaxServerPort.value = serverPort
        println("new port $serverPort")
    }

    fun setUpdateSeconds(updateSeconds: Int) {
        softmaxUpdateSeconds.value = updateSeconds
        println("new secods $updateSeconds")
    }
}