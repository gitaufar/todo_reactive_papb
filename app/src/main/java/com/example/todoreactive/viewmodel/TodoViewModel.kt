package com.example.todoreactive.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todoreactive.model.Todo
import kotlinx.coroutines.flow.*

class TodoViewModel : ViewModel() {

    private val _allTodos = MutableStateFlow<List<Todo>>(emptyList())
    val allTodos: StateFlow<List<Todo>> = _allTodos

    private val _filter = MutableStateFlow("semua")
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredTodos: StateFlow<List<Todo>> = combine(
        _allTodos, _filter, _searchQuery
    ) { todos, filter, query ->
        var filtered = when (filter) {
            "aktif" -> todos.filterNot { it.isDone }
            "selesai" -> todos.filter { it.isDone }
            else -> todos
        }
        if (query.isNotBlank()) {
            filtered = filtered.filter { it.title.contains(query, ignoreCase = true) }
        }
        filtered
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val activeCount: StateFlow<Int> = _allTodos.map { todos ->
        todos.count { !it.isDone }
    }.stateIn(
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        SharingStarted.WhileSubscribed(5000),
        0
    )
    val doneCount: StateFlow<Int> = _allTodos.map { todos ->
        todos.count { it.isDone }
    }.stateIn(
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        SharingStarted.WhileSubscribed(5000),
        0
    )
    fun addTask(title: String) {
        val nextId = (_allTodos.value.maxOfOrNull { it.id } ?: 0) + 1
        _allTodos.value = _allTodos.value + Todo(id = nextId, title = title)
    }

    fun toggleTask(id: Int) {
        _allTodos.value = _allTodos.value.map {
            if (it.id == id) it.copy(isDone = !it.isDone) else it
        }
    }

    fun deleteTask(id: Int) {
        _allTodos.value = _allTodos.value.filterNot { it.id == id }
    }

    fun filterTask(filter: String) {
        _filter.value = filter
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
