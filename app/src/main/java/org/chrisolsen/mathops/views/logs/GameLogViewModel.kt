package org.chrisolsen.mathops.views.logs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.chrisolsen.mathops.models.Game
import org.chrisolsen.mathops.models.MathOpsDatabase

class GameLogViewModel(application: Application) : AndroidViewModel(application) {

    val logs = MutableLiveData<List<Game>>()

    fun fetchAll() {
        viewModelScope.launch {
            logs.value = MathOpsDatabase(getApplication()).gameDao().getAll()
        }
    }

    fun fetchByOperation(operation: String): MutableLiveData<List<Game>> {
        viewModelScope.launch {
            logs.value = MathOpsDatabase(getApplication()).gameDao().getByOperation(operation)
        }
        return logs
    }

}