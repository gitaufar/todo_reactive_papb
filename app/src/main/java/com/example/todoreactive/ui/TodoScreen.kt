package com.example.todoreactive.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoreactive.viewmodel.TodoViewModel

@Composable
fun TodoScreen(vm: TodoViewModel = viewModel()) {
    val todos by vm.filteredTodos.collectAsState()
    val activeCount by vm.activeCount.collectAsState()
    val doneCount by vm.doneCount.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    val searchQuery by vm.searchQuery.collectAsState()

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tambah tugas...") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    vm.addTask(text.trim())
                    text = ""
                }
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text("Tambah")
        }
        Divider(Modifier.padding(vertical = 8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Aktif: $activeCount")
            Text("Selesai: $doneCount")
            Text("Total: ${activeCount + doneCount}")
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { vm.updateSearchQuery(it) },
            label = { Text("Cari tugas...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { vm.filterTask("semua") }) { Text("Semua") }
            Button(onClick = { vm.filterTask("aktif") }) { Text("Aktif") }
            Button(onClick = { vm.filterTask("selesai") }) { Text("Selesai") }
        }

        Divider(Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { vm.toggleTask(todo.id) },
                    onDelete = { vm.deleteTask(todo.id) }
                )
            }
        }
    }
}

