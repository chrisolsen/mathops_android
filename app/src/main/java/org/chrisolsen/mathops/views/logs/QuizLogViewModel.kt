package org.chrisolsen.mathops.views.logs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.chrisolsen.mathops.models.MathOpsDatabase
import org.chrisolsen.mathops.models.Quiz

class QuizLogViewModel(application: Application) : AndroidViewModel(application) {

    val logs = MutableLiveData<List<Quiz>>()

    fun fetchAll() {
        viewModelScope.launch {
            logs.value = MathOpsDatabase(getApplication()).quizDao().getAll()
        }
    }

    fun fetchByOperation(operation: String): MutableLiveData<List<Quiz>> {
        viewModelScope.launch {
            logs.value = MathOpsDatabase(getApplication()).quizDao().getByOperation(operation)
        }
        return logs
    }

}