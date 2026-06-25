package com.moji.v1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moji.v1.databinding.ItemMoodCardBinding
import com.moji.v1.model.Mood

class MoodAdapter(
    private val moods: List<Mood>,
    private val onClick: (Mood) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(val binding: ItemMoodCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moods[position]

        with(holder.binding) {
            tvMoodLabel.text = mood.label
            tvMoodDesc.text = mood.description
            imgMoodChar.setImageResource(mood.character)
            cardMood.setBackgroundResource(mood.cardBackground)
            cardMood.setOnClickListener { onClick(mood) }
        }
    }

    override fun getItemCount() = moods.size
}