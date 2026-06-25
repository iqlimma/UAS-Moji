package com.moji.v1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moji.v1.databinding.ItemJournalBinding
import com.moji.v1.model.JournalEntry

class JournalAdapter(
    private val entries: List<JournalEntry>,
    private val onClick: (JournalEntry) -> Unit
) : RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {

    inner class JournalViewHolder(val binding: ItemJournalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val binding = ItemJournalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return JournalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val (id, mood, content, date, dateKey) = entries[position]

        with(holder.binding) {
            tvItemMood.text = mood.label
            tvItemContent.text = content
            tvItemDate.text = date
            imgItemChar.setImageResource(mood.character)
            cardJournalItem.setBackgroundResource(mood.cardBackground)
            root.setOnClickListener { onClick(entries[position]) }
        }
    }

    override fun getItemCount() = entries.size
}