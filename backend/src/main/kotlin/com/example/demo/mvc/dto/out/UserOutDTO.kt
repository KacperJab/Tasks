package com.example.demo.mvc.dto.out

data class UserOutDTO(
    val name: String,

    val id: String,

    val openTasksCount: Int,

    val openImportantTaskCount: Int,

    val totalTaskCount: Int
)
