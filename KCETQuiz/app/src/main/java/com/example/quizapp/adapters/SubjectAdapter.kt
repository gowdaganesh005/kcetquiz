package com.example.quizapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.databinding.ItemSubjectBinding

class SubjectAdapter(
    private val subjects: List<String>,
    private val onSubjectClick: (String) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    override fun getItemCount(): Int = subjects.size

    inner class SubjectViewHolder(private val binding: ItemSubjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subject: String) {
            binding.subjectName.text = subject
            binding.root.setOnClickListener { onSubjectClick(subject) }
        }
    }
}