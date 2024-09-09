package com.example.todo_app

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.todo_app.db.Converters
import java.util.Date

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String,
    var createdAt: Date,
    var reminderDate: Date? = null // Optional field for reminders
)
