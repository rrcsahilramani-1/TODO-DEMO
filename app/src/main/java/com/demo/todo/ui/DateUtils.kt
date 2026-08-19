package com.demo.todo.ui

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Epoch millis at local midnight of today. */
fun todayStartMillis(): Long = startOfDay(System.currentTimeMillis())

fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = millis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private val dayFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

/** "Today" / "Tomorrow" / "Yesterday" where it helps, an absolute date otherwise. */
fun formatDueDate(millis: Long): String {
    val day = startOfDay(millis)
    val today = todayStartMillis()
    val dayMs = 24L * 60 * 60 * 1000
    return when (day) {
        today -> "Today"
        today + dayMs -> "Tomorrow"
        today - dayMs -> "Yesterday"
        else -> dayFormat.format(day)
    }
}

fun isOverdue(millis: Long): Boolean = startOfDay(millis) < todayStartMillis()

private val headerFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())

fun formatTodayHeader(): String = headerFormat.format(System.currentTimeMillis())
