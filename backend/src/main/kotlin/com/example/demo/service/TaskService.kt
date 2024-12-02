package com.example.demo.service

import com.example.demo.domain.Task
import com.example.demo.mvc.dto.`in`.CreateTaskDTO
import com.example.demo.mvc.dto.`in`.UpdateTaskDTO
import org.springframework.stereotype.Service

@Service
interface TaskService {

    fun insertTask(taskDTO: CreateTaskDTO): Task

    fun updateTask(taskDTO: UpdateTaskDTO, taskId: String): Task

    fun deleteTask(taskId: String): String

    fun getAllTasks(): Collection<Task>

    fun getFilteredTasks(important: Boolean, open: Boolean): Collection<Task>

    fun getTask(taskId: String): Task
}
