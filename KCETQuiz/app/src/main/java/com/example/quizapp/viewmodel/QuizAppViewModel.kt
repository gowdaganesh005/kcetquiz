package com.example.quizapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizapp.database.AppDatabase
import com.example.quizapp.database.Option
import com.example.quizapp.database.Question
import com.example.quizapp.database.Score
import com.example.quizapp.database.UserAnswer
import com.example.quizapp.database.QuizResultItem
import kotlinx.coroutines.launch

class QuizAppViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val questionDao = database.questionDao()
    private val optionDao = database.optionDao()
    private val userAnswerDao = database.userAnswerDao()
    private val scoreDao = database.scoreDao()

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> get() = _questions

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> get() = _currentQuestionIndex

    private val currentQuestion = MediatorLiveData<Question>()

    private val _currentOptions = MutableLiveData<List<Option>>()
    val currentOptions: LiveData<List<Option>> get() = _currentOptions

    private val _selectedOptionId = MutableLiveData<Int?>()
    val selectedOptionId: LiveData<Int?> get() = _selectedOptionId

    private val _isViewingResults = MutableLiveData(false)
    val isViewingResults: LiveData<Boolean> get() = _isViewingResults

    private val _score = MutableLiveData<Score?>()
    val score: LiveData<Score?> get() = _score

    private val _quizResults = MutableLiveData<List<QuizResultItem>>()
    val quizResults: LiveData<List<QuizResultItem>> get() = _quizResults

    private var subject: String = ""

    init {
        currentQuestion.addSource(_questions) { qs ->
            val index = _currentQuestionIndex.value ?: 0
            if (qs.isNotEmpty()) currentQuestion.value = qs[index]
        }
        currentQuestion.addSource(_currentQuestionIndex) { index ->
            val qs = _questions.value
            if (!qs.isNullOrEmpty()) currentQuestion.value = qs[index]
        }
    }

    fun startQuiz(subject: String) {
        this.subject = subject
        viewModelScope.launch {
            userAnswerDao.clearUserAnswers()
            val questionsForSubject = questionDao.getQuestionsForSubject(subject)
            _questions.value = questionsForSubject
            _currentQuestionIndex.value = 0
            loadOptionsForCurrentQuestion()
            _isViewingResults.value = false
            Log.d("QuizViewModel", "Quiz started for subject: $subject with ${questionsForSubject.size} questions")
        }
    }

    fun loadLastQuizResults(subject: String) {
        this.subject = subject
        viewModelScope.launch {
            val questionsForSubject = questionDao.getQuestionsForSubject(subject)
            _questions.value = questionsForSubject
            _currentQuestionIndex.value = 0
            loadOptionsForCurrentQuestion()
            val latestScore = scoreDao.getLatestScoreForSubject(subject)
            Log.d("QuizViewModel", "Fetched latest score: $latestScore")
            _score.value = latestScore
            _isViewingResults.value = true
            val results = questionsForSubject.map { question ->
                val options = optionDao.getOptionsForQuestion(question.id)
                val userAnswer = userAnswerDao.getUserAnswerForQuestion(question.id)
                val correctOption = options.find { it.isCorrect }!!
                QuizResultItem(
                    questionText = question.text,
                    options = options,
                    selectedOptionId = userAnswer?.selectedOptionId,
                    correctOptionId = correctOption.id
                )
            }
            _quizResults.value = results
            Log.d("QuizViewModel", "Loaded ${results.size} quiz results for subject: $subject")
        }
    }

    private fun loadOptionsForCurrentQuestion() {
        viewModelScope.launch {
            val question = currentQuestion.value ?: return@launch
            val options = optionDao.getOptionsForQuestion(question.id)
            _currentOptions.value = options
            if (_isViewingResults.value == true) {
                val userAnswer = userAnswerDao.getUserAnswerForQuestion(question.id)
                _selectedOptionId.value = userAnswer?.selectedOptionId
            } else {
                _selectedOptionId.value = null
            }
            Log.d("QuizViewModel", "Loaded ${options.size} options for question ID: ${question.id}")
        }
    }

    fun selectOption(optionId: Int) {
        val question = currentQuestion.value ?: return
        viewModelScope.launch {
            val userAnswer = UserAnswer(question.id, optionId)
            userAnswerDao.insert(userAnswer)
            _selectedOptionId.value = optionId
            Log.d("QuizViewModel", "Selected option ID: $optionId for question ID: ${question.id}")
        }
    }

    fun nextQuestion() {
        val index = _currentQuestionIndex.value ?: 0
        if (index < (_questions.value?.size ?: 0) - 1) {
            _currentQuestionIndex.value = index + 1
            loadOptionsForCurrentQuestion()
            _selectedOptionId.value = null
            Log.d("QuizViewModel", "Moved to next question, new index: ${index + 1}")
        }
    }

    fun previousQuestion() {
        val index = _currentQuestionIndex.value ?: 0
        if (index > 0) {
            _currentQuestionIndex.value = index - 1
            loadOptionsForCurrentQuestion()
            viewModelScope.launch {
                val question = currentQuestion.value ?: return@launch
                val userAnswer = userAnswerDao.getUserAnswerForQuestion(question.id)
                _selectedOptionId.value = userAnswer?.selectedOptionId
            }
            Log.d("QuizViewModel", "Moved to previous question, new index: ${index - 1}")
        }
    }

    fun submitQuiz() {
        viewModelScope.launch {
            val (correct, total) = calculateResults()
            val score = Score(0, subject, correct, total, System.currentTimeMillis())
            scoreDao.insert(score)
            Log.d("QuizViewModel", "Score saved: $score")
            _score.value = score
            _isViewingResults.value = true
            loadLastQuizResults(subject)
        }
    }

    private suspend fun calculateResults(): Pair<Int, Int> {
        val questions = _questions.value ?: return Pair(0, 0)
        var correct = 0
        for (question in questions) {
            val userAnswer = userAnswerDao.getUserAnswerForQuestion(question.id)
            if (userAnswer != null) {
                val selectedOption = optionDao.getOptionsForQuestion(question.id)
                    .find { it.id == userAnswer.selectedOptionId }
                if (selectedOption?.isCorrect == true) correct++
            }
        }
        Log.d("QuizViewModel", "Calculated results: $correct correct out of ${questions.size}")
        return Pair(correct, questions.size)
    }
}