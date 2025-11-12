package com.example.todoreactive.model

data class Todo(
    val id: Int,
    val title: String,
    val isDone: Boolean = false
)
