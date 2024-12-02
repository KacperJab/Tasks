package com.example.demo.mvc.controller

import com.example.demo.domain.Task
import com.example.demo.mvc.dto.`in`.CreateTaskDTO
import com.example.demo.mvc.dto.`in`.UpdateTaskDTO
import com.example.demo.mvc.dto.out.TaskOutDTO
import com.example.demo.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.* // ktlint-disable no-wildcard-imports
import javax.validation.Valid

@CrossOrigin()
@RestController
@RequestMapping("/api")
class TaskController(val taskService: TaskService) {

    @GetMapping("/tasks/{taskId}")
    fun getTask(@PathVariable taskId: String): TaskOutDTO {
        val taskDomain = taskService.getTask(taskId)
        return domainToDTO(taskDomain)
    }

    @GetMapping("/tasks")
    fun getAllTasks(
        @RequestParam("important", required = false) important: Boolean,
        @RequestParam("open", required = false) open: Boolean
    ): Collection<TaskOutDTO> {
        val tasks: Collection<Task> = if (important || open) {
            taskService.getFilteredTasks(important, open)
        } else {
            taskService.getAllTasks()
        }
        return domainToDTO(tasks)
    }

    @PostMapping("/tasks")
    fun insertTask(
        @Valid @RequestBody
        task: CreateTaskDTO
    ): TaskOutDTO {
        val taskDomain = taskService.insertTask(task)
        return domainToDTO(taskDomain)
    }

    @PutMapping("/tasks/{taskId}")
    fun updateTask(
        @Valid @RequestBody
        task: UpdateTaskDTO,
        @PathVariable taskId: String
    ): TaskOutDTO {
        val updatedTask = taskService.updateTask(task, taskId)
        return domainToDTO(updatedTask)
    }

    @DeleteMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable taskId: String) {
        taskService.deleteTask(taskId)
    }
    private fun domainToDTO(task: Task): TaskOutDTO {
        val date = task.createdOn.toLocalDateTime()
        return TaskOutDTO(task.id, task.title, task.description, task.important, task.done, date)
    }

    private fun domainToDTO(tasks: Collection<Task>) = tasks.map { task -> domainToDTO(task) }
}
