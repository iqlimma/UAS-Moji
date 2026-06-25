package com.moji.v1.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.moji.v1.R
import com.moji.v1.adapter.MoodAdapter
import com.moji.v1.databinding.FragmentHomeBinding
import com.moji.v1.model.Mood

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMoodCards()
        updateDashboardVisibility()
        setupFab()
    }

    private fun setupFab() {
        val fab = (activity as? com.moji.v1.ui.main.MainActivity)?.findViewById<View>(R.id.fab)
        fab?.setOnClickListener {
            showMoodSelection()
        }
    }

    private fun showMoodSelection() {
        binding.layoutDashboard.visibility = View.GONE
        binding.layoutMoodSelection.visibility = View.VISIBLE
    }

    private fun updateDashboardVisibility() {
        val entries = com.moji.v1.data.DummyData.entries
        val hasEntries = entries.isNotEmpty()
        
        binding.layoutMoodSelection.visibility = View.GONE
        binding.layoutDashboard.visibility = View.VISIBLE

        if (hasEntries) {
            binding.layoutDataPresent.visibility = View.VISIBLE
            binding.tvSeeMore.visibility = View.VISIBLE
            binding.layoutDataEmpty.visibility = View.GONE
            binding.tvJournalCount.text = entries.size.toString()
            setupMoodBars(entries)
        } else {
            binding.layoutDataPresent.visibility = View.GONE
            binding.tvSeeMore.visibility = View.GONE
            binding.layoutDataEmpty.visibility = View.VISIBLE
            binding.tvJournalCount.text = "0"
        }
    }

    private fun setupMoodBars(entries: List<com.moji.v1.model.JournalEntry>) {
        val recentEntries = entries.take(4)
        val container = binding.layoutDataPresent

        // Heights to mimic the "dynamic" look in the user's design image
        val heights = listOf(180, 140, 110, 80) 

        for (i in 0 until 4) {
            val barView = container.getChildAt(i) ?: continue
            if (i < recentEntries.size) {
                barView.visibility = View.VISIBLE
                val mood = recentEntries[i].mood
                
                val fillView = barView.findViewById<View>(R.id.viewMoodFill)
                val outlineView = barView.findViewById<View>(R.id.viewOutline)
                val charImg = barView.findViewById<android.widget.ImageView>(R.id.imgMoodChar)

                // Set dynamic height
                val params = fillView?.layoutParams
                params?.height = (heights[i % heights.size] * resources.displayMetrics.density).toInt()
                fillView?.layoutParams = params

                // Set colors and character
                fillView?.setBackgroundResource(mood.barFill)
                charImg?.setImageResource(mood.character)
                
                // Color the outline to match the mood
                outlineView?.background?.setTint(androidx.core.content.ContextCompat.getColor(requireContext(), mood.backgroundColor))
                outlineView?.background?.setTintMode(android.graphics.PorterDuff.Mode.SRC_ATOP)

            } else {
                barView.visibility = View.GONE
            }
        }
    }

    private fun setupMoodCards() {
        val adapter = MoodAdapter(Mood.values().toList()) { mood ->
            val bundle = bundleOf("selectedMood" to mood.name)
            findNavController().navigate(R.id.journalFragment, bundle)
        }

        binding.rvMoodCards.layoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val totalItems = Mood.values().size
                    return if (totalItems % 2 != 0 && position == totalItems - 1) 2 else 1
                }
            }
        }
        binding.rvMoodCards.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}