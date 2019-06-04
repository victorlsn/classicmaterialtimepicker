package com.michaldrabik.classicmaterialtimepicker.model

import com.michaldrabik.classicmaterialtimepicker.CmtpTimeData

data class CmtpTime12(
  override val hour: Int,
  override val minute: Int,
  val pmAm: PmAm
) : CmtpTime {

  init {
    check(hour in CmtpTimeData.HOURS_12) { "Invalid hour. Must be between 1 and 12" }
    check(minute in CmtpTimeData.MINUTES) { "Invalid minute. Must be between 0 and 59" }
  }

  override fun is12Hour() = true

  override fun toString() = String.format("%02d:%02d:%s", hour, minute, pmAm.name)

  enum class PmAm { PM, AM }
}