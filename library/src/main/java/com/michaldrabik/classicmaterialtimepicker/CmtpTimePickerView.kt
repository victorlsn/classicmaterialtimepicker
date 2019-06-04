package com.michaldrabik.classicmaterialtimepicker

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.michaldrabik.classicmaterialtimepicker.model.CmtpTime
import com.michaldrabik.classicmaterialtimepicker.model.CmtpTime12
import com.michaldrabik.classicmaterialtimepicker.model.CmtpTime24
import com.michaldrabik.classicmaterialtimepicker.recycler.CmtpValuesAdapter
import kotlinx.android.synthetic.main.cmtp_timepicker_view.view.*

class CmtpTimePickerView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private val recyclerHoursAdapter by lazy { CmtpValuesAdapter() }
  private val recyclerMinutesAdapter by lazy { CmtpValuesAdapter() }
  private val recyclerPmAmAdapter by lazy { CmtpValuesAdapter() }

  private val recyclerHoursLayoutManager by lazy {
    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
  }
  private val recyclerMinutesLayoutManager by lazy {
    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
  }
  private val recyclerPmAmLayoutManager by lazy {
    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
  }

  private val recyclerHoursSnapHelper by lazy { LinearSnapHelper() }
  private val recyclerMinutesSnapHelper by lazy { LinearSnapHelper() }
  private val recyclerPmAmSnapHelper by lazy { LinearSnapHelper() }

  private var time: CmtpTime = CmtpTime24(12, 30)

  init {
    inflate(
      ContextThemeWrapper(context, R.style.CmtpViewTheme),
      R.layout.cmtp_timepicker_view,
      this
    )
    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    setupRecyclers()
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    val padding = measuredHeight / 2
    cmtpRecyclerHours.setPadding(0, padding, 0, padding)
    cmtpRecyclerMinutes.setPadding(0, padding, 0, padding)
    cmtpRecyclerPmAm.setPadding(0, padding, 0, padding)
  }

  private fun setupRecyclers() {
    cmtpRecyclerHours.apply {
      setHasFixedSize(true)
      adapter = recyclerHoursAdapter
      layoutManager = recyclerHoursLayoutManager
      recyclerHoursSnapHelper.attachToRecyclerView(this)
    }
    cmtpRecyclerMinutes.apply {
      setHasFixedSize(true)
      adapter = recyclerMinutesAdapter
      layoutManager = recyclerMinutesLayoutManager
      recyclerMinutesSnapHelper.attachToRecyclerView(this)
    }
    cmtpRecyclerPmAm.apply {
      setHasFixedSize(true)
      adapter = recyclerPmAmAdapter
      layoutManager = recyclerPmAmLayoutManager
      recyclerPmAmSnapHelper.attachToRecyclerView(this)
    }
    setupRecyclersData()
  }

  private fun setupRecyclersData() {
    val hours = when {
      time.is12Hour() -> CmtpTimeData.HOURS_12
      else -> CmtpTimeData.HOURS_24
    }
    cmtpRecyclerHours.apply {
      recyclerHoursAdapter.setItems(hours.map { String.format("%02d", it) })
      recyclerHoursLayoutManager.scrollToPosition(hours.indexOf(time.hour))
      smoothScrollBy(0, 1)
    }
    cmtpRecyclerMinutes.apply {
      val minutes = CmtpTimeData.MINUTES
      recyclerMinutesAdapter.setItems(minutes.map { String.format("%02d", it) })
      recyclerMinutesLayoutManager.scrollToPosition(minutes.indexOf(time.minute))
      smoothScrollBy(0, 1)
    }
    cmtpRecyclerPmAm.apply {
      recyclerPmAmAdapter.setItems(CmtpTimeData.PM_AM)
      visibility = when {
        time.is12Hour() -> {
          recyclerPmAmLayoutManager.scrollToPosition((time as CmtpTime12).pmAm.ordinal)
          smoothScrollBy(0, 1)
          VISIBLE
        }
        else -> GONE
      }
    }
  }

  /**
   * Set initial time with 24-Hour or 12-Hour format.
   */
  fun setInitialTime(initialTime: CmtpTime) {
    time = initialTime
    setupRecyclersData()
  }

  fun getTime24(): CmtpTime24 {
    check(!time.is12Hour()) { "Can't retrieve time in 24-Hour format. TimePicker view was initialised with 12-Hour format." }

    val hoursView = recyclerHoursSnapHelper.findSnapView(recyclerHoursLayoutManager)
    val minutesView = recyclerMinutesSnapHelper.findSnapView(recyclerMinutesLayoutManager)

    if (hoursView == null || minutesView == null)
      throw IllegalStateException("TimePicker view has not been initialized yet.")

    val hoursIndex = recyclerHoursLayoutManager.getPosition(hoursView)
    val minutesIndex = recyclerMinutesLayoutManager.getPosition(minutesView)

    return CmtpTime24(
      recyclerHoursAdapter.getItems()[hoursIndex].toInt(),
      recyclerMinutesAdapter.getItems()[minutesIndex].toInt()
    )
  }

  fun getTime12(): CmtpTime12 {
    check(time.is12Hour()) { "Can't retrieve time in 12-Hour format. TimePicker view was initialised with 24-Hour format." }

    val hoursView = recyclerHoursSnapHelper.findSnapView(recyclerHoursLayoutManager)
    val minutesView = recyclerMinutesSnapHelper.findSnapView(recyclerMinutesLayoutManager)
    val pmAmView = recyclerPmAmSnapHelper.findSnapView(recyclerPmAmLayoutManager)

    if (hoursView == null || minutesView == null || pmAmView == null)
      throw IllegalStateException("TimePicker view has not been initialized yet.")

    val hoursIndex = recyclerHoursLayoutManager.getPosition(hoursView)
    val minutesIndex = recyclerMinutesLayoutManager.getPosition(minutesView)
    val pmAmIndex = recyclerPmAmLayoutManager.getPosition(pmAmView)

    return CmtpTime12(
      recyclerHoursAdapter.getItems()[hoursIndex].toInt(),
      recyclerMinutesAdapter.getItems()[minutesIndex].toInt(),
      CmtpTime12.PmAm.values()[pmAmIndex]
    )
  }

  fun is12Hour() = time.is12Hour()
}