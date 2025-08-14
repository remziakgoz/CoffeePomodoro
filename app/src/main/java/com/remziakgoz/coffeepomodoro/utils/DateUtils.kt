package com.remziakgoz.coffeepomodoro.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getCurrentDateString(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.now().format(formatter)
    } else {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatter.format(Date())
    }
}

fun getMondayOfCurrentWeek(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.now().with(DayOfWeek.MONDAY).format(formatter)
    } else {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatter.format(calendar.time)
    }
}

fun getCurrentMonthString(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        LocalDate.now().format(formatter)
    } else {
        val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        formatter.format(Date())
    }
}

fun getCurrentDayIndex(): Int {
    val cal = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY }
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }
}

