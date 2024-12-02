package com.example.demo.mvc.controller

import com.example.demo.domain.Task
import com.example.demo.domain.User
import com.example.demo.mvc.dto.out.TaskOutDTO
import com.example.demo.mvc.dto.out.UserOutDTO
import com.example.demo.service.UserService
import org.springframework.web.bind.annotation.*

@CrossOrigin()
@RestController
@RequestMapping("/api")
class UserController(val service: UserService) {

    @GetMapping("/users")
    fun getAllUsers(): Collection<UserOutDTO> {
        val users = service.getAllUsers()
        return userToDTO(users)
    }

    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable userId: String): UserOutDTO {
        val tasks = service.getUser(userId)
        return userToDTO(tasks)
    }

    @GetMapping("/users/{userId}/tasks")
    fun getUserTasks(
        @PathVariable userId: String,
        @RequestParam("important", required = false) important: Boolean,
        @RequestParam("open", required = false) open: Boolean
    ): Collection<TaskOutDTO> {
        val tasks: Collection<Task> = if (important || open) {
            service.getUserFilteredTasks(userId, important, open)
        } else {
            service.getUserTasks(userId)
        }
        return tasksToDTO(tasks)
    }

    @GetMapping("/users/{userId}/tasks/{taskId}")
    fun getUserTask(@PathVariable userId: String, @PathVariable taskId: String): TaskOutDTO {
        val task = service.getUserTask(userId, taskId)
        return taskToDTO(task)
    }

    private fun userToDTO(user: User): UserOutDTO {
        return UserOutDTO(user.name, user.id, user.openTasksCount, user.openImportantTaskCount, user.totalTaskCount)
    }

    private fun userToDTO(users: Collection<User>) = users.map { user -> userToDTO(user) }

    private fun taskToDTO(task: Task): TaskOutDTO {
        val date = task.createdOn.toLocalDateTime()
        return TaskOutDTO(task.id, task.title, task.description, task.important, task.done, date)
    }

    private fun tasksToDTO(tasks: Collection<Task>) = tasks.map { task -> taskToDTO(task) }
}
