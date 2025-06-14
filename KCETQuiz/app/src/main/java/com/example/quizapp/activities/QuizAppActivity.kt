package com.example.quizapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.quizapp.R
import com.example.quizapp.databinding.ActivityQuizappBinding
import com.example.quizapp.viewmodel.QuizAppViewModel
import com.google.android.material.radiobutton.MaterialRadioButton

class QuizAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizappBinding
    private lateinit var viewModel: QuizAppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quizapp)
        viewModel = ViewModelProvider(this)[QuizAppViewModel::class.java]
        binding.viewModel = this.viewModel
        binding.lifecycleOwner = this

        val subject = intent.getStringExtra("subject") ?: ""
        val prefs = getSharedPreferences("quiz_prefs", MODE_PRIVATE)
        val quizTaken = prefs.getBoolean("quiz_taken_$subject", false)

        if (quizTaken) {
            viewModel.loadLastQuizResults(subject)
        } else {
            viewModel.startQuiz(subject)
        }

        viewModel.currentOptions.observe(this) { options ->
            if (options.size == 4) {
                binding.option1.text = options[0].text
                binding.option1.tag = options[0].id
                binding.option2.text = options[1].text
                binding.option2.tag = options[1].id
                binding.option3.text = options[2].text
                binding.option3.tag = options[2].id
                binding.option4.text = options[3].text
                binding.option4.tag = options[3].id
            }
        }

        viewModel.selectedOptionId.observe(this) { optionId ->
            if (optionId != null) {
                listOf(binding.option1, binding.option2, binding.option3, binding.option4).forEach {
                    it.isChecked = it.tag == optionId
                }
            } else {
                binding.optionsRadioGroup.clearCheck()
            }
        }

        binding.optionsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<MaterialRadioButton>(checkedId)
            if (radioButton != null) {
                val optionId = radioButton.tag as Int
                viewModel.selectOption(optionId)
            }
        }

        binding.previousButton.setOnClickListener {
            viewModel.previousQuestion()
        }

        binding.nextButton.setOnClickListener {
            val isLastQuestion = viewModel.currentQuestionIndex.value == (viewModel.questions.value?.size ?: 0) - 1
            if (isLastQuestion && viewModel.isViewingResults.value != true) {
                viewModel.submitQuiz()
                prefs.edit { putBoolean("quiz_taken_$subject", true) }
            } else {
                viewModel.nextQuestion()
            }
        }

        viewModel.currentQuestionIndex.observe(this) { index ->
            binding.optionsRadioGroup.clearCheck()
            val totalQuestions = viewModel.questions.value?.size ?: 0
            binding.previousButton.isEnabled = index > 0
            binding.nextButton.text = if (index == totalQuestions - 1 && !viewModel.isViewingResults.value!!) "Submit" else "Next"
            Log.d("QuizActivity", "Question index changed to: $index")
        }

         viewModel.isViewingResults.observe(this) { isViewing ->
            if (isViewing) {
                val score = viewModel.score.value
                binding.scoreTextView.text = if (score != null) {
                    "Score: ${score.correctCount} / ${score.totalQuestions}"
                } else {
                    Log.w("QuizActivity", "Score is null when viewing results")
                    "No score available"
                }
                binding.scoreTextView.visibility = View.VISIBLE
                binding.quizLayout.visibility = View.GONE
                binding.resultsRecyclerView.visibility = View.VISIBLE

            } else {
                binding.scoreTextView.visibility = View.GONE
                binding.quizLayout.visibility = View.VISIBLE
                binding.resultsRecyclerView.visibility = View.GONE
            }
        }
    }
}