package com.example.quizapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizapp.models.Question
import com.example.quizapp.models.Subject
import androidx.core.content.edit

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
    private lateinit var subject: String
    private lateinit var questions: List<Question>

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> get() = _currentQuestionIndex

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> get() = _currentQuestion

    private val _selectedOption = MutableLiveData<String?>()
    val selectedOption: LiveData<String?> get() = _selectedOption

    private val _isViewingResults = MutableLiveData(false)
    val isViewingResults: LiveData<Boolean> get() = _isViewingResults

    private val _score = MutableLiveData<Int?>()
    val score: LiveData<Int?> get() = _score

    fun startQuiz(subject: String, allSubjects: List<Subject>) {
        this.subject = subject
        questions = allSubjects.find { it.subject == subject }?.questions ?: emptyList()
        questions.forEach { question ->
            sharedPreferences.edit { remove("${subject}_q${question.id}_answer") }
        }
        _currentQuestionIndex.value = 0
        _isViewingResults.value = false
        updateCurrentQuestion()
        _selectedOption.value = null
    }

    fun loadLastQuizResults(subject: String, allSubjects: List<Subject>) {
        this.subject = subject
        questions = allSubjects.find { it.subject == subject }?.questions ?: emptyList()
        _currentQuestionIndex.value = 0
        _isViewingResults.value = true
        _score.value = sharedPreferences.getInt("${subject}_score", 0)
        updateCurrentQuestion()
    }

    fun nextQuestion() {
        val index = _currentQuestionIndex.value ?: 0
        if (index < questions.size - 1) {
            _currentQuestionIndex.value = index + 1
            _selectedOption.value = null // Clear selection for next question
            updateCurrentQuestion()
        }
    }

    fun previousQuestion() {
        val index = _currentQuestionIndex.value ?: 0
        if (index > 0) {
            _currentQuestionIndex.value = index - 1
            updateCurrentQuestion()
            loadSelectedOption() // Load saved answer after updating question
        }
    }

    fun selectOption(option: String) {
        _selectedOption.value = option
        val questionId = questions[_currentQuestionIndex.value ?: 0].id
        sharedPreferences.edit { putString("${subject}_q${questionId}_answer", option) }
    }

    fun submitQuiz() {
        var correct = 0
        questions.forEach { question ->
            val userAnswer = sharedPreferences.getString("${subject}_q${question.id}_answer", null)
            if (userAnswer == question.correctAnswer) correct++
        }
        sharedPreferences.edit { putInt("${subject}_score", correct) }
        _score.value = correct
        _isViewingResults.value = true
    }

    fun getResults(): List<Pair<Question, String?>> {
        return questions.map { question ->
            val userAnswer = sharedPreferences.getString("${subject}_q${question.id}_answer", null)
            Pair(question, userAnswer)
        }
    }

    private fun updateCurrentQuestion() {
        val index = _currentQuestionIndex.value ?: 0
        if (questions.isNotEmpty() && index in questions.indices) {
            _currentQuestion.value = questions[index]
        }
    }

    private fun loadSelectedOption() {
        val questionId = questions[_currentQuestionIndex.value ?: 0].id
        val savedAnswer = sharedPreferences.getString("${subject}_q${questionId}_answer", null)
        _selectedOption.value = savedAnswer
    }

}