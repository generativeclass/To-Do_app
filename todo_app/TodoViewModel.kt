package com.example.todo_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_app.db.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class TodoViewModel : ViewModel() {

    private val todoDao = MainApplication.todoDatabase.getTodoDao()
    val todoList: LiveData<List<Todo>> = todoDao.getAllTodo()

    fun addTodo(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoDao.addTodo(Todo(title = title, createdAt = Date()))
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
}
