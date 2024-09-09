package com.example.todo_app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListPage(viewModel: TodoViewModel) {
    val todoList by viewModel.todoList.observeAsState()
    var inputText by remember { mutableStateOf("") }
    var selectedTodo by remember { mutableStateOf<Todo?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Text(
            text = "My Notes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Enter new Note", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.primary,
                )
            )

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.addTodo(inputText)
                        inputText = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        // Todo List
        todoList?.let { list ->
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(list) { _, item ->
                    TodoItem(
                        item = item,
                        onDelete = { viewModel.deleteTodo(item.id) },
                        onSetReminder = { todo ->
                            selectedTodo = todo
                            showDialog = true
                        }
                    )
                }
            }
        } ?: Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "No items yet",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

    // Reminder Dialog
    if (showDialog && selectedTodo != null) {
        SetReminderDialog(
            todo = selectedTodo!!,
            onDismiss = { showDialog = false },
            onReminderSet = { todo, reminderDate ->
                viewModel.setReminder(todo, reminderDate)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItem(item: Todo, onDelete: () -> Unit, onSetReminder: (Todo) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(14.dp)
            .shadow(3.dp, RoundedCornerShape(6.dp))
            .combinedClickable(
                onLongClick = { onSetReminder(item) },
                onClick = { /* Handle click if needed */ }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.ENGLISH).format(item.createdAt),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
            )
            Text(
                text = item.title,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Medium
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
fun SetReminderDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onReminderSet: (Todo, Date) -> Unit
) {
    var reminderDate by remember { mutableStateOf(Date()) }
    var dateString by remember { mutableStateOf("Select date and time") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                reminderDate = calendar.time
                dateString = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(reminderDate)
                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            LocalContext.current,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                reminderDate = calendar.time
                dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(reminderDate)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Reminder") },
        text = {
            Column {
                Text("Choose date and time for the reminder")
                Spacer(modifier = Modifier.size(16.dp))
                Text("Selected: $dateString", fontSize = 16.sp)
                Spacer(modifier = Modifier.size(16.dp))
                Row {
                    Button(onClick = { showDatePicker = true }) {
                        Text("Pick Date")
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(onClick = { showTimePicker = true }) {
                        Text("Pick Time")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onReminderSet(todo, reminderDate)
                }
            ) {
                Text("Set Reminder")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
