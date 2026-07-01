package com.moji.v1.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.moji.v1.R
import com.moji.v1.adapter.JournalAdapter
import com.moji.v1.database.DatabaseHelper
import com.moji.v1.database.SessionManager
import com.moji.v1.databinding.FragmentHistoryBinding
import com.moji.v1.model.JournalEntry
import com.moji.v1.model.Mood
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private var moodMap: Map<String, Mood> = emptyMap()
    private var allEntries: List<JournalEntry> = emptyList()
    private var selectedDateKey: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())
        loadData()
        setupCalendar()
        setupMenu()
    }

    private fun loadData() {
        val userId = sessionManager.getUserId()
        allEntries = dbHelper.getJournalsByUser(userId)
        moodMap = allEntries.associate { it.dateKey to it.mood }
        showEntries(selectedDateKey)
    }

    private fun setupMenu() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_clear_all -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Clear All Journals")
                        .setMessage("Are you sure you want to delete all journal entries?")
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton("Delete") { _, _ ->
                            val userId = sessionManager.getUserId()
                            dbHelper.deleteAllJournalsByUser(userId)
                            loadData()
                            binding.calendarView.notifyCalendarChanged()
                            Snackbar.make(binding.root, "All journals deleted", Snackbar.LENGTH_SHORT).show()
                        }
                        .show()
                    true
                }
                R.id.action_filter -> {
                    showFilterBottomSheet()
                    true
                }
                else -> false
            }
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheet = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
        bottomSheet.setContentView(sheetView)

        fun filterBy(mood: Mood?) {
            val filtered = if (mood == null) allEntries
            else allEntries.filter { it.mood == mood }
            showFilteredEntries(filtered)
            bottomSheet.dismiss()
        }

        sheetView.findViewById<android.view.View>(R.id.filterAll)?.setOnClickListener { filterBy(null) }
        sheetView.findViewById<android.view.View>(R.id.filterHappy)?.setOnClickListener { filterBy(Mood.HAPPY) }
        sheetView.findViewById<android.view.View>(R.id.filterNeutral)?.setOnClickListener { filterBy(Mood.NEUTRAL) }
        sheetView.findViewById<android.view.View>(R.id.filterSad)?.setOnClickListener { filterBy(Mood.SAD) }
        sheetView.findViewById<android.view.View>(R.id.filterTired)?.setOnClickListener { filterBy(Mood.TIRED) }
        sheetView.findViewById<android.view.View>(R.id.filterAngry)?.setOnClickListener { filterBy(Mood.ANGRY) }
        sheetView.findViewById<android.view.View>(R.id.filterEnvy)?.setOnClickListener { filterBy(Mood.ENVY) }

        bottomSheet.show()
    }

    private fun showFilteredEntries(entries: List<JournalEntry>) {
        if (entries.isEmpty()) {
            binding.rvHistory.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvHistory.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvHistory.adapter = com.moji.v1.adapter.HistoryListAdapter(buildListItems(entries)) {}
        }
    }

    private fun setupCalendar() {
        val daysOfWeek = daysOfWeek()
        binding.legendLayout.removeAllViews()
        daysOfWeek.forEach { day ->
            val tv = TextView(requireContext())
            tv.text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            val params = android.widget.LinearLayout.LayoutParams(
                0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            tv.layoutParams = params
            binding.legendLayout.addView(tv)
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val tvMonth = view.findViewById<TextView>(R.id.tvMonthHeader)
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            val tvDay = view.findViewById<TextView>(R.id.tvCalendarDay)
            val imgDayMood = view.findViewById<android.widget.ImageView>(R.id.imgDayMood)
            val dayContainer = view.findViewById<android.widget.FrameLayout>(R.id.dayContainer)
        }

        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                container.tvMonth.text =
                    data.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) +
                            " " + data.yearMonth.year
            }
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.tvDay.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    val key = data.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val mood = moodMap[key]

                    if (mood != null) {
                        container.dayContainer.setBackgroundResource(R.drawable.bg_calendar_day)
                        val color = ContextCompat.getColor(requireContext(), mood.backgroundColor)
                        (container.dayContainer.background as android.graphics.drawable.GradientDrawable).setColor(color)
                        container.imgDayMood.setImageResource(mood.character)
                        container.imgDayMood.visibility = View.VISIBLE
                        container.imgDayMood.scaleX = 1.4f
                        container.imgDayMood.scaleY = 1.4f
                        container.imgDayMood.translationY = 20f
                        container.tvDay.setTextColor(android.graphics.Color.parseColor("#1A1A1A"))
                    } else {
                        container.dayContainer.background = null
                        container.imgDayMood.visibility = View.GONE
                        container.imgDayMood.scaleX = 1f
                        container.imgDayMood.scaleY = 1f
                        container.tvDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    }

                    container.tvDay.setOnClickListener {
                        selectedDateKey = if (selectedDateKey == key) null else key
                        showEntries(selectedDateKey)
                    }
                } else {
                    container.dayContainer.background = null
                    container.imgDayMood.visibility = View.GONE
                    container.tvDay.setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
                }
            }
        }

        val currentMonth = YearMonth.now()
        binding.calendarView.setup(
            currentMonth.minusMonths(6),
            currentMonth.plusMonths(6),
            daysOfWeek.first()
        )
        binding.calendarView.scrollToMonth(currentMonth)
    }

    private fun showEntries(dateKey: String?) {
        val entries = if (dateKey == null) allEntries
        else allEntries.filter { it.dateKey == dateKey }

        if (entries.isEmpty()) {
            binding.rvHistory.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvHistory.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvHistory.adapter = com.moji.v1.adapter.HistoryListAdapter(buildListItems(entries)) {}
        }
    }

    private fun buildListItems(entries: List<JournalEntry>): List<com.moji.v1.model.HistoryListItem> {
        val grouped = entries.groupBy { it.dateKey }
        val result = mutableListOf<com.moji.v1.model.HistoryListItem>()

        val todayKey = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
        val yesterdayKey = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(java.util.Date(System.currentTimeMillis() - 86400000))

        grouped.forEach { (key, entryList) ->
            val sampleDate = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(key)
            val dayNumber = java.text.SimpleDateFormat("d", Locale.getDefault()).format(sampleDate!!)
            val month = java.text.SimpleDateFormat("MMM", Locale.getDefault()).format(sampleDate)
            val dayName = java.text.SimpleDateFormat("EEEE", Locale.getDefault()).format(sampleDate)

            val label = when (key) {
                todayKey -> "Today"
                yesterdayKey -> "Yesterday"
                else -> java.text.SimpleDateFormat("d MMM", Locale.getDefault()).format(sampleDate)
            }

            result.add(com.moji.v1.model.HistoryListItem.DateHeader(dayNumber, month, label, dayName))
            entryList.forEach { result.add(com.moji.v1.model.HistoryListItem.Entry(it)) }
        }

        return result
    }
}