package com.example.demo.service.impl

import com.example.demo.dao.mappers.TaskMapper
import com.example.demo.domain.Task
import com.example.demo.exception.ApiRequestException
import com.example.demo.mvc.dto.`in`.CreateTaskDTO
import com.example.demo.mvc.dto.`in`.UpdateTaskDTO
import com.example.demo.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskServiceImpl(val mapper: TaskMapper) : TaskService {

    private fun getUserId(): String = SecurityContextHolder.getContext().authentication.name

    override fun insertTask(taskDTO: CreateTaskDTO): Task {
        val userId = getUserId()
        val task = dtoToDomain(taskDTO, userId)
        val created = mapper.insertTask(task)
        if (created == 1) {
            return task
        } else {
            throw ApiRequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Task was not created")
        }
    }

    override fun updateTask(taskDTO: UpdateTaskDTO, taskId: String): Task {
        val userId = getUserId()
        val task = dtoToDomain(taskDTO, taskId, userId)
        val taskToUpdate = mapper.getTask(taskId)
        if (taskToUpdate != null) {
            if (taskToUpdate.userId != userId) {
                throw ApiRequestException(HttpStatus.FORBIDDEN, "User without permission to access this task")
            }
            val updated = mapper.updateTask(task)
            if (updated == 1) {
                return task.copy(createdOn = taskToUpdate.createdOn)
            } else {
                throw ApiRequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Task was not updated")
            }
        } else {
            throw ApiRequestException(HttpStatus.NOT_FOUND, "No task to update")
        }
    }

    override fun deleteTask(taskId: String): String {
        val userId = getUserId()
        val taskToDelete = mapper.getTask(taskId)
        if ((taskToDelete != null) && (taskToDelete.userId != userId)) {
            throw ApiRequestException(HttpStatus.FORBIDDEN, "User without permission to delete this task")
        }
        val deleted = mapper.deleteTask(taskId)
        if (deleted == 1) {
            return taskId
        } else {
            throw ApiRequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Task was not deleted")
        }
    }

    override fun getAllTasks(): Collection<Task> {
        val userId = getUserId()
        return mapper.getAllTasks(userId)
    }

    override fun getFilteredTasks(important: Boolean, open: Boolean): Collection<Task> {
        val userId = getUserId()
        if (important && open) {
            return mapper.filterImportantAndOpenTasks(userId)
        } else if (important) {
            return mapper.filterImportantTasks(userId)
        } else if (open) {
            return mapper.filterOpenTasks(userId)
        }
        return emptyList()
    }

    override fun getTask(taskId: String): Task {
        val userId = getUserId()
        val task = mapper.getTask(taskId)
        if (task == null) {
            throw ApiRequestException(HttpStatus.NOT_FOUND, "Task not found")
        } else {
            if (task.userId != userId) {
                throw ApiRequestException(HttpStatus.FORBIDDEN, "User without permission to get this task")
            }
            return task
        }
    }

    private fun dtoToDomain(taskDTO: CreateTaskDTO, userId: String): Task {
        val id = UUID.randomUUID().toString()
        return Task(
            id,
            userId,
            taskDTO.title,
            taskDTO.description,
            taskDTO.important,
            taskDTO.done
        )
    }

    private fun dtoToDomain(taskDTO: UpdateTaskDTO, taskId: String, userId: String): Task {
        return Task(taskId, userId, taskDTO.title, taskDTO.description, taskDTO.important, taskDTO.done)
    }
}
