package com.moji.v1.ui.mood

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
import com.moji.v1.databinding.FragmentMoodPickerBinding
import com.moji.v1.model.Mood

class MoodPickerFragment : Fragment() {

    private var _binding: FragmentMoodPickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val adapter = MoodAdapter(Mood.values().toList()) { mood ->
            val bundle = bundleOf("selectedMood" to mood.name)
            findNavController().navigate(R.id.journalFragment, bundle)
        }

        binding.rvMoodPicker.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMoodPicker.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}