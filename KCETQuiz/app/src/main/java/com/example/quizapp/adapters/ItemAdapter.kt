package com.example.quizapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.quizapp.databinding.ItemViewBinding
import com.example.quizapp.models.Item

class ItemAdapter(private val items: List<Item>, private val itemClickListener: (Item)-> Unit) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemViewBinding) : ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.imageview.setImageResource(item.img)
            
            binding.descriptionTextView.text = item.description
            binding.root.setOnClickListener{
                itemClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemViewBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}