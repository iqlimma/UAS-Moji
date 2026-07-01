package com.moji.v1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.moji.v1.databinding.ItemDateHeaderBinding
import com.moji.v1.databinding.ItemJournalBinding
import com.moji.v1.model.HistoryListItem

class HistoryListAdapter(
    private val items: List<HistoryListItem>,
    private val onClick: (HistoryListItem.Entry) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ENTRY = 1
    }

    inner class HeaderViewHolder(val binding: ItemDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class EntryViewHolder(val binding: ItemJournalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryListItem.DateHeader -> TYPE_HEADER
            is HistoryListItem.Entry -> TYPE_ENTRY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val binding = ItemDateHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EntryViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HistoryListItem.DateHeader -> {
                val h = holder as HeaderViewHolder
                h.binding.tvHeaderDay.text = item.dayNumber
                h.binding.tvHeaderMonth.text = item.month
                h.binding.tvHeaderLabel.text = item.label
                h.binding.tvHeaderDayName.text = item.dayName
            }
            is HistoryListItem.Entry -> {
                val h = holder as EntryViewHolder
                val entry = item.journalEntry
                with(h.binding) {
                    tvItemMood.text = entry.mood.label
                    tvItemContent.text = entry.content
                    tvItemTime.text = entry.time
                    imgItemChar.setImageResource(entry.mood.character)
                    cardJournalItem.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, android.R.color.white)
                    )
                    cardJournalItem.strokeWidth = 4
                    cardJournalItem.strokeColor = ContextCompat.getColor(root.context, entry.mood.backgroundColor)
                    root.setOnClickListener { onClick(item) }
                }
            }
        }
    }

    override fun getItemCount() = items.size
}