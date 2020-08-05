package org.chrisolsen.mathops.views.gameoptions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.chrisolsen.mathops.views.game.Operations

class GameOptionsViewModel : ViewModel() {
    val questionCount = MutableLiveData<Int>(25)
    val operation = MutableLiveData<Operations>(Operations.addition)
}