package com.example.demo

import com.example.demo.dao.mappers.TaskMapper
import com.example.demo.domain.Task
import com.example.demo.exception.ApiRequestException
import com.example.demo.mvc.dto.`in`.CreateTaskDTO
import com.example.demo.mvc.dto.`in`.UpdateTaskDTO
import com.example.demo.service.impl.TaskServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import kotlin.random.Random

class TaskServiceTest {
    val userId = UUID.randomUUID().toString()

    val adminId = UUID.randomUUID().toString()

    val securityContext = mockk<SecurityContext>()

    val authentication = mockk<Authentication>()

    val mapper = mockk<TaskMapper>()

    val userTaskId = UUID.randomUUID().toString()

    val taskService = TaskServiceImpl(mapper)

    val userTask = Task(userTaskId, userId, "User task", "User task desc", true, true)

    @BeforeEach
    fun setMockkSecurity() {
        SecurityContextHolder.setContext(securityContext)
        every { securityContext.authentication } returns authentication
        every { authentication.name } returns userId
    }

    @Test
    fun `task is inserted correctly`() {
        val taskDTO = createTaskDto()

        every { mapper.insertTask(any()) } returns 1

        val task = taskService.insertTask(taskDTO)

        verify(exactly = 1) { mapper.insertTask(any()) }

        Assertions.assertTrue(compareTaskAndDTO(taskDTO, task))
    }

    @Test
    fun `successfully update existing task`() {
        every { mapper.updateTask(any()) } returns 1
        every { mapper.getTask(any()) } returns userTask

        val editedTaskDto = editTaskDto(true, true)
        val editedTask = taskService.updateTask(editedTaskDto, userTask.id)

        verify(exactly = 1) { mapper.updateTask(any()) }

        Assertions.assertTrue(compareTaskAndDTO(editedTaskDto, editedTask))
    }

    @Test
    fun `update existing task without permission raises exception FORBIDDEN`() {
        every { mapper.getTask(any()) } returns userTask

        val editedTaskDto = editTaskDto(false, true)

        every { authentication.name } returns adminId

        val thrown: ApiRequestException = Assertions.assertThrows(ApiRequestException::class.java) {
            taskService.updateTask(editedTaskDto, userTask.id)
        }

        verify(exactly = 0) { mapper.updateTask(any()) }

        Assertions.assertEquals(HttpStatus.FORBIDDEN, thrown.status)
    }

    @Test
    fun `update non existing task raises exception NOT_FOUND`() {
        val editedTaskDto = editTaskDto(true, false)

        every { mapper.getTask(any()) } returns null

        val thrown: ApiRequestException = Assertions.assertThrows(ApiRequestException::class.java) {
            taskService.updateTask(editedTaskDto, UUID.randomUUID().toString())
        }

        verify(exactly = 0) { mapper.updateTask(any()) }
        verify(exactly = 1) { mapper.getTask(any()) }

        Assertions.assertEquals(HttpStatus.NOT_FOUND, thrown.status)
    }

    @Test
    fun `successfully delete existing task`() {
        every { mapper.getTask(any()) } returns userTask
        every { mapper.deleteTask(any()) } returns 1

        Assertions.assertEquals(userTask.id, taskService.deleteTask(userTask.id))

        verify(exactly = 1) { mapper.getTask(any()) }
        verify(exactly = 1) { mapper.deleteTask(any()) }
    }

    @Test
    fun `delete existing task without permission raises exception FORBIDDEN`() {
        every { mapper.getTask(any()) } returns userTask
        every { authentication.name } returns adminId

        val thrown: ApiRequestException = Assertions.assertThrows(ApiRequestException::class.java) {
            taskService.deleteTask(userTask.id)
        }

        verify(exactly = 1) { mapper.getTask(any()) }
        verify(exactly = 0) { mapper.deleteTask(any()) }

        Assertions.assertEquals(HttpStatus.FORBIDDEN, thrown.status)
    }

    @Test
    fun `get all tasks returns all users tasks`() {
        val numberOfTasksUser = 5

        every { mapper.getAllTasks(userId) } returns randomTaskCollection(userId, numberOfTasksUser)
        val usersTasks = taskService.getAllTasks()

        verify(exactly = 1) { mapper.getAllTasks(any()) }

        Assertions.assertEquals(numberOfTasksUser, usersTasks.size)
    }

    @Test
    fun `get all tasks returns 0 for user without existing tasks`() {
        every { mapper.getAllTasks(userId) } returns emptyList()

        val usersTasks = taskService.getAllTasks()

        verify(exactly = 1) { mapper.getAllTasks(any()) }

        Assertions.assertEquals(0, usersTasks.size)
    }

    @Test
    fun `get task returns single user task with correct id correctly`() {
        every { mapper.getTask(userTask.id) } returns userTask

        val retrievedTask = taskService.getTask(userTask.id)

        verify(exactly = 1) { mapper.getTask(any()) }

        Assertions.assertEquals(userTask, retrievedTask)
    }

    @Test
    fun `get task throws NOT_FOUND exception for non-existing task id`() {
        every { mapper.getTask(any()) } returns null

        val thrown: ApiRequestException = Assertions.assertThrows(ApiRequestException::class.java) {
            taskService.getTask(UUID.randomUUID().toString())
        }

        verify(exactly = 1) { mapper.getTask(any()) }

        Assertions.assertEquals(HttpStatus.NOT_FOUND, thrown.status)
    }

    @Test
    fun `get task throws FORBIDDEN exception for user trying to access not his task`() {
        every { authentication.name } returns adminId
        every { mapper.getTask(any()) } returns userTask

        val thrown: ApiRequestException = Assertions.assertThrows(ApiRequestException::class.java) {
            taskService.getTask(userTask.id)
        }

        verify(exactly = 1) { mapper.getTask(any()) }

        Assertions.assertEquals(HttpStatus.FORBIDDEN, thrown.status)
    }

    @Test
    fun `filter important tasks returns important tasks only`() {
        val numberOfImportantTasks = 5

        every { mapper.filterImportantTasks(any()) } returns randomTaskCollection(userId, numberOfImportantTasks, important = true)

        val filteredTasks = taskService.getFilteredTasks(important = true, open = false)

        verify(exactly = 1) { mapper.filterImportantTasks(any()) }

        Assertions.assertEquals(filteredTasks.size, numberOfImportantTasks)
        for (task in filteredTasks) {
            Assertions.assertEquals(true, task.important)
        }
    }

    @Test
    fun `filter open tasks returns open tasks only`() {
        val numberOfOpenTasks = 5

        every { mapper.filterOpenTasks(any()) } returns randomTaskCollection(userId, numberOfOpenTasks, done = false)

        val filteredTasks = taskService.getFilteredTasks(important = false, open = true)

        verify(exactly = 1) { mapper.filterOpenTasks(any()) }

        Assertions.assertEquals(filteredTasks.size, numberOfOpenTasks)
        for (task in filteredTasks) {
            Assertions.assertEquals(false, task.done)
        }
    }

    @Test
    fun `filter open and important tasks returns only open and important tasks`() {
        val numberOfOpenAndImportantTasks = 5

        every { mapper.filterImportantAndOpenTasks(any()) } returns randomTaskCollection(userId, numberOfOpenAndImportantTasks, done = false, important = true)

        val filteredTasks = taskService.getFilteredTasks(important = true, open = true)

        verify(exactly = 1) { mapper.filterImportantAndOpenTasks(any()) }

        Assertions.assertEquals(filteredTasks.size, numberOfOpenAndImportantTasks)
        for (task in filteredTasks) {
            Assertions.assertEquals(false, task.done)
            Assertions.assertEquals(true, task.important)
        }
    }

    private fun createTaskDto(important: Boolean = Random.nextBoolean(), done: Boolean = Random.nextBoolean()): CreateTaskDTO {
        return CreateTaskDTO("Task title", "Task description", important, done)
    }
    private fun editTaskDto(important: Boolean = Random.nextBoolean(), done: Boolean = Random.nextBoolean()): UpdateTaskDTO {
        return UpdateTaskDTO("Edited ask title", "Task description", important, done)
    }
    private fun compareTaskAndDTO(taskIn: CreateTaskDTO, task: Task): Boolean {
        return (
            task.title == taskIn.title &&
                task.description == taskIn.description &&
                task.done == taskIn.done &&
                task.important == taskIn.important
            )
    }

    private fun compareTaskAndDTO(taskIn: UpdateTaskDTO, task: Task): Boolean {
        return (
            task.title == taskIn.title &&
                task.description == taskIn.description &&
                task.done == taskIn.done &&
                task.important == taskIn.important
            )
    }

    private fun randomTask(id: String, important: Boolean = Random.nextBoolean(), done: Boolean = Random.nextBoolean()): Task {
        val uuidTemp = UUID.randomUUID().toString()
        val description = "Description of task"
        val title = "Task title"
        return Task(uuidTemp, id, title, description, important, done)
    }

    private fun randomTaskCollection(uid: String, n: Int, important: Boolean = Random.nextBoolean(), done: Boolean = Random.nextBoolean()): Collection<Task> {
        return (0 until n).map { randomTask(uid, important, done) }
    }
}
