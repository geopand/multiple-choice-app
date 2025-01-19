package com.panco.multichoice.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.panco.multichoice.models.Questionnaire

class GameViewModel: ViewModel() {
    val sharedData = MutableLiveData<Questionnaire>()
}