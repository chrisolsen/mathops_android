package org.chrisolsen.mathops.views.game

import androidx.lifecycle.ViewModel

enum class Operations {
    addition,
    subtraction,
    multiplication,
    division,
}

class GameViewModel : ViewModel() {
    val TAG = "GameViewModel"
}