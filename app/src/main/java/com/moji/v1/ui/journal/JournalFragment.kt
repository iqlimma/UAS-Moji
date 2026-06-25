package com.moji.v1.ui.journal

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.moji.v1.R
import com.moji.v1.data.DummyData
import com.moji.v1.databinding.FragmentJournalBinding
import com.moji.v1.model.JournalEntry
import com.moji.v1.model.Mood
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentMood: Mood
    private var isScribbleMode = false

    // Photo picker
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imgPhoto.setImageURI(it)
            binding.imgPhoto.visibility = View.VISIBLE
        }
    }

    private fun showStickerBottomSheet() {
        val bottomSheet = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_sticker, null)
        bottomSheet.setContentView(sheetView)

        val stickerMap = mapOf(
            R.id.stickerThink to R.drawable.sticker_think,
            R.id.stickerFlower to R.drawable.sticker_flower,
            R.id.stickerCloud to R.drawable.sticker_cloud,
            R.id.stickerStar to R.drawable.sticker_star
        )

        stickerMap.forEach { (viewId, drawableId) ->
            sheetView.findViewById<android.widget.ImageView>(viewId).setOnClickListener {
                addStickerToJournal(drawableId)
                bottomSheet.dismiss()
            }
        }

        bottomSheet.show()
    }

    private fun addStickerToJournal(drawableId: Int) {
        val sticker = android.widget.ImageView(requireContext())
        sticker.setImageResource(drawableId)

        val size = 150
        val params = android.widget.FrameLayout.LayoutParams(size, size)
        params.leftMargin = 100
        params.topMargin = 100
        sticker.layoutParams = params

        // Make draggable
        sticker.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_MOVE -> {
                    v.x = event.rawX - v.width / 2
                    v.y = event.rawY - binding.cardJournal.top - v.height / 2
                }
            }
            true
        }

        // Tambahkan ke FrameLayout dalam cardJournal
        val frameLayout = binding.cardJournal.getChildAt(0) as android.widget.FrameLayout
        frameLayout.addView(sticker)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moodName = arguments?.getString("selectedMood") ?: Mood.NEUTRAL.name
        currentMood = Mood.valueOf(moodName)

        // Set background card sesuai mood
        val journalBg = when (currentMood) {
            Mood.HAPPY -> R.drawable.bg_journal_happy
            Mood.NEUTRAL -> R.drawable.bg_journal_neutral
            Mood.SAD -> R.drawable.bg_journal_sad
            Mood.TIRED -> R.drawable.bg_journal_tired
            Mood.ANGRY -> R.drawable.bg_journal_angry
            Mood.ENVY -> R.drawable.bg_journal_envy
        }
        binding.cardJournal.setBackgroundResource(journalBg)

        // Set karakter mood
        binding.imgMoodBg.setImageResource(currentMood.character)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            saveJournal()
        }

        // Add Photo
        binding.btnAddPhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAddSticker.setOnClickListener {
            showStickerBottomSheet()
        }

        // Add Scribble
        binding.btnAddScribble.setOnClickListener {
            isScribbleMode = !isScribbleMode
            if (isScribbleMode) {
                binding.drawingView.visibility = View.VISIBLE
                binding.toolbarScribble.visibility = View.VISIBLE
                binding.etJournal.isEnabled = false
                binding.btnAddScribble.text = "Done Scribble"
            } else {
                binding.drawingView.visibility = View.GONE
                binding.toolbarScribble.visibility = View.GONE
                binding.etJournal.isEnabled = true
                binding.btnAddScribble.text = "Add Scribble"
            }
        }

        binding.btnUndo.setOnClickListener { binding.drawingView.undo() }
        binding.btnClearScribble.setOnClickListener { binding.drawingView.clear() }

        // Color picker
        binding.colorRed.setOnClickListener { binding.drawingView.setColor(Color.parseColor("#EF5350")) }
        binding.colorYellow.setOnClickListener { binding.drawingView.setColor(Color.parseColor("#FFD527")) }
        binding.colorGreen.setOnClickListener { binding.drawingView.setColor(Color.parseColor("#66BB6A")) }
        binding.colorBlue.setOnClickListener { binding.drawingView.setColor(Color.parseColor("#5C85F5")) }
        binding.colorPink.setOnClickListener { binding.drawingView.setColor(Color.parseColor("#F48FB1")) }

        // Brush size
        binding.brushSmall.setOnClickListener { binding.drawingView.setBrushSize(4f) }
        binding.brushMedium.setOnClickListener { binding.drawingView.setBrushSize(8f) }
        binding.brushLarge.setOnClickListener { binding.drawingView.setBrushSize(16f) }
    }

    private fun saveJournal() {
        val content = binding.etJournal.text.toString().trim()

        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Tulis dulu yuk!", Toast.LENGTH_SHORT).show()
            return
        }

        val now = Date()
        val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val newEntry = JournalEntry(
            id = DummyData.entries.size + 1,
            mood = currentMood,
            content = content,
            date = displayFormat.format(now),
            dateKey = keyFormat.format(now)
        )

        DummyData.entries.add(0, newEntry)
        Toast.makeText(requireContext(), "Jurnal tersimpan! 🎉", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}