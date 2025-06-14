package com.example.quizapp.activities

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.R
import com.example.quizapp.adapters.ResultAdapter
import com.example.quizapp.databinding.ActivityQuizBinding
import com.example.quizapp.databinding.ToolbarLanBinding
import com.example.quizapp.models.Subject
import com.example.quizapp.models.SubjectsWrapper
import com.example.quizapp.viewmodel.QuizViewModel
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import androidx.core.content.edit

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var viewModel: QuizViewModel
    private lateinit var allSubjects: List<Subject>
    private var isUpdatingSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz)
        setSupportActionBar(binding.toolbar)
        val subject = intent.getStringExtra("subject") ?: ""

        val head = "$subject Quiz        "

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(Color.BLACK)
        binding.toolbar.overflowIcon?.setTint(Color.BLACK)

        val customTitleViewBinding: ToolbarLanBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.toolbar_lan, null, false)
        binding.toolbar.addView(
            customTitleViewBinding.root, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        customTitleViewBinding.customToolbarTitle.apply {
            text = head
            isSelected = true
        }


        viewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val inputStream = assets.open("questions.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<SubjectsWrapper>() {}.type
        val wrapper: SubjectsWrapper = Gson().fromJson(reader, type)
        allSubjects = wrapper.subjects
        reader.close()

        val prefs = getSharedPreferences("quiz_prefs", MODE_PRIVATE)
        val quizTaken = prefs.getBoolean("quiz_taken_$subject", false)

        if (quizTaken) {
            viewModel.loadLastQuizResults(subject, allSubjects)
        } else {
            viewModel.startQuiz(subject, allSubjects)
        }

        viewModel.currentQuestion.observe(this) { question ->
            binding.questionTextView.text = question.text
            binding.option1.text = question.options[0]
            binding.option2.text = question.options[1]
            binding.option3.text = question.options[2]
            binding.option4.text = question.options[3]
            binding.optionsRadioGroup.clearCheck() // Reset selection when question changes
        }

        viewModel.selectedOption.observe(this) { selected ->
            isUpdatingSelection = true
            listOf(binding.option1, binding.option2, binding.option3, binding.option4).forEach { radioButton ->
                radioButton.isChecked = radioButton.text.toString() == selected
            }
            isUpdatingSelection = false
        }

        binding.optionsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (!isUpdatingSelection) {
                val radioButton = group.findViewById<MaterialRadioButton>(checkedId)
                if (radioButton != null) {
                    viewModel.selectOption(radioButton.text.toString())
                }
            }
        }

        binding.previousButton.setOnClickListener {
            viewModel.previousQuestion()
        }

        binding.nextButton.setOnClickListener {
            val isLastQuestion = viewModel.currentQuestionIndex.value == (allSubjects.find { it.subject == subject }?.questions?.size?.minus(1) ?: 0)
            if (isLastQuestion && !viewModel.isViewingResults.value!!) {
                viewModel.submitQuiz()
                prefs.edit { putBoolean("quiz_taken_$subject", true) }
            } else {
                viewModel.nextQuestion()
            }
        }

        viewModel.currentQuestionIndex.observe(this) { index ->
            binding.previousButton.isEnabled = index > 0
            binding.nextButton.text = if (index == (allSubjects.find { it.subject == subject }?.questions?.size?.minus(1) ?: 0) && !viewModel.isViewingResults.value!!) "Submit" else "Next"
        }

        viewModel.isViewingResults.observe(this) { isViewing ->
            if (isViewing) {
                binding.scoreTextView.text = "Score: ${viewModel.score.value} / 10"
                binding.scoreTextView.visibility = View.VISIBLE
                binding.quizLayout.visibility = View.GONE
                binding.resultsRecyclerView.visibility = View.VISIBLE
                val results = viewModel.getResults()
                val adapter = ResultAdapter(results)
                binding.resultsRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.resultsRecyclerView.adapter = adapter
                binding.resultsRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            } else {
                binding.scoreTextView.visibility = View.GONE
                binding.quizLayout.visibility = View.VISIBLE
                binding.resultsRecyclerView.visibility = View.GONE
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}