package com.example.demo.domain

data class User(
    val id: String,

    val name: String,

    val surname: String,

    val openTasksCount: Int,

    val openImportantTaskCount: Int,

    val totalTaskCount: Int
)
