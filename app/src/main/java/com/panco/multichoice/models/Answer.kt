package com.panco.multichoice.models

data class Answer (var id: Int, var questionId: Int, var text: String, var isCorrect: Boolean) {}