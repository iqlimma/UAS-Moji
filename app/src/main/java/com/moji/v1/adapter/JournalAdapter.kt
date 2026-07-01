package com.moji.v1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        val entry = entries[position]

        with(holder.binding) {
            tvItemMood.text = entry.mood.label
            tvItemContent.text = entry.content
            tvItemTime.text = entry.time
            imgItemChar.setImageResource(entry.mood.character)

            cardJournalItem.setCardBackgroundColor(
                ContextCompat.getColor(root.context, android.R.color.white)
            )
            cardJournalItem.strokeWidth = 4
            cardJournalItem.strokeColor = ContextCompat.getColor(root.context, entry.mood.backgroundColor)

            root.setOnClickListener { onClick(entry) }
        }
    }

    override fun getItemCount() = entries.size
}