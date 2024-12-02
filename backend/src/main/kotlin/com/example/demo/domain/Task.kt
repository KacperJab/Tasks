package com.example.demo.domain

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

data class Task(
    val id: String,

    val userId: String,

    val title: String,

    val description: String? = null,

    val important: Boolean = false,

    val done: Boolean = false,

    val createdOn: Timestamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MICROS))
)
