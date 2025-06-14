package com.example.quizapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.databinding.ItemResultBinding
import com.example.quizapp.models.Question

class ResultAdapter(private val results: List<Pair<Question, String?>>) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val (question, userAnswer) = results[position]
        holder.bind(question, userAnswer)
    }

    override fun getItemCount(): Int = results.size

    class ResultViewHolder(private val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(question: Question, userAnswer: String?) {
            binding.questionTextView.text = question.text
            binding.optionsLayout.removeAllViews()
            question.options.forEach { option ->
                val textView = TextView(binding.root.context).apply {
                    text = option
                    typeface = ResourcesCompat.getFont(binding.root.context, R.font.abcd)
                    textSize = 17f
                    setTextColor(
                        when {
                            option == question.correctAnswer -> Color.GREEN
                            option == userAnswer && option != question.correctAnswer -> Color.RED
                            else -> ContextCompat.getColor(context, R.color.textviewLan)
                        }
                    )
                }
                binding.optionsLayout.addView(textView)
            }
            binding.feedbackTextView.text = when (userAnswer) {
                null -> "Selected Option: Unattended"
                question.correctAnswer -> "Selected Option: Correct"
                else -> "Selected Option: Incorrect"
            }
        }
    }
}