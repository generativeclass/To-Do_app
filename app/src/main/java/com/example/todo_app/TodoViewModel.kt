package com.example.todo_app

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todo_app.db.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val todoDao = MainApplication.todoDatabase.getTodoDao()
    val todoList: LiveData<List<Todo>> = todoDao.getAllTodo()

    fun addTodo(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val todo = Todo(title = title, createdAt = Date())
                todoDao.addTodo(todo)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteTodo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoDao.deleteTodo(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun setReminder(todo: Todo, reminderDate: Date) {
        scheduleReminder(getApplication(), todo, reminderDate)
    }
}

// Function to schedule reminders using WorkManager
fun scheduleReminder(context: Context, todo: Todo, reminderDate: Date) {
    val workManager = WorkManager.getInstance(context)

    // Calculate the delay until the reminder date
    val currentTime = System.currentTimeMillis()
    val delay = reminderDate.time - currentTime

    // Prepare input data
    val inputData = Data.Builder()
        .putString("todoTitle", todo.title)
        .putLong("reminderDate", reminderDate.time)
        .build()

    // Create and enqueue the worker request
    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()

    workManager.enqueue(workRequest)
}
