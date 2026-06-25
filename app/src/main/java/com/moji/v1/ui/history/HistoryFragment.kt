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
import com.moji.v1.R
import com.moji.v1.adapter.JournalAdapter
import com.moji.v1.data.DummyData
import com.moji.v1.databinding.FragmentHistoryBinding
import com.moji.v1.model.Mood
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private var moodMap: Map<String, Mood> = emptyMap()
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
        setupCalendar()
        showEntries(null)
    }

    private fun setupCalendar() {
        moodMap = DummyData.entries.associate { it.dateKey to it.mood }

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
                    container.tvDay.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    )

                    val key = data.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val mood = moodMap[key]

                    if (mood != null) {
                        val color = ContextCompat.getColor(requireContext(), mood.backgroundColor)
                        container.tvDay.setBackgroundResource(R.drawable.bg_calendar_day)
                        (container.tvDay.background as android.graphics.drawable.GradientDrawable).setColor(color)
                    } else {
                        container.tvDay.background = null
                    }

                    container.tvDay.setOnClickListener {
                        if (selectedDateKey == key) {
                            showEntries(null)
                        } else {
                            showEntries(key)
                        }
                    }

                } else {
                    container.tvDay.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    )
                    container.tvDay.background = null
                }
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(6)
        val endMonth = currentMonth.plusMonths(6)

        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToMonth(currentMonth)
    }

    private fun showEntries(dateKey: String?) {
        selectedDateKey = dateKey
        val entries = if (dateKey == null) {
            DummyData.entries
        } else {
            DummyData.entries.filter { it.dateKey == dateKey }
        }

        if (entries.isEmpty()) {
            binding.rvHistory.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvHistory.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvHistory.adapter = JournalAdapter(entries) {}
        }
    }

    override fun onResume() {
        super.onResume()
        moodMap = DummyData.entries.associate { it.dateKey to it.mood }
        binding.calendarView.notifyCalendarChanged()
        showEntries(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}