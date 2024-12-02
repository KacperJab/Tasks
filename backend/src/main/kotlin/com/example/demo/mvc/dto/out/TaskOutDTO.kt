package com.example.demo.mvc.dto.out

import java.time.LocalDateTime

data class TaskOutDTO(
    val id: String,

    val title: String,

    val description: String?,

    val important: Boolean,

    val done: Boolean,

    val createdOn: LocalDateTime
)
