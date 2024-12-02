package com.example.demo.service

import com.example.demo.domain.Task
import com.example.demo.domain.User

interface UserService {

    fun getAllUsers(): Collection<User>

    fun getUser(userId: String): User

    fun getUserTasks(userId: String): Collection<Task>

    fun getUserTask(userId: String, taskId: String): Task

    fun getUserFilteredTasks(userId: String, important: Boolean, open: Boolean): Collection<Task>
}
