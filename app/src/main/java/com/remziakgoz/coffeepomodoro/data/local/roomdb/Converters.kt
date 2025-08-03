package com.remziakgoz.coffeepomodoro.data.local.roomdb

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromList(list: List<Int>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String?): List<Int>? {
        return data?.split(",")?.mapNotNull { it.toIntOrNull()}
    }
}