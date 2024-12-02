package com.example.demo

import com.example.demo.dao.mappers.TaskMapper
import com.example.demo.domain.Task
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*
import kotlin.random.Random

@Transactional
@Testcontainers
@SpringBootTest
class TaskMapperTest @Autowired constructor(@Autowired val mapper: TaskMapper) {

    private final val uuid = UUID.randomUUID().toString()

    private final val userId = "1234"

    private final val userIdNonExisting = "999"

    private final val title = "Test title"

    private final val description = "test description"

    private final val titleModified = "Test title modified"

    val task = Task(uuid, userId, title, description, true, true)

    @Test
    fun `task is being inserted correctly`() {
        mapper.insertTask(task)
        verifyTaskInsertion(task)
        val retrievedTask = mapper.getTask(uuid)
        Assertions.assertTrue(task == retrievedTask)
    }

    @Test
    fun `task with same id is not being inserted`() {
        mapper.insertTask(task)
        verifyTaskInsertion(task)
        Assertions.assertThrows(DuplicateKeyException::class.java) {
            mapper.insertTask(task)
        }
    }

    @Test
    fun `task title is modified correctly`() {
        val taskModified = task.copy(title = titleModified)
        mapper.insertTask(task)
        verifyTaskInsertion(task)
        mapper.updateTask(taskModified)
        val retrievedTask = mapper.getTask(uuid)
        Assertions.assertTrue(retrievedTask?.title == taskModified.title)
    }

    @Test
    fun `existing task can be modified`() {
        val taskModified = task.copy(title = titleModified)
        mapper.insertTask(task)
        verifyTaskInsertion(task)
        Assertions.assertTrue(mapper.updateTask(taskModified) == 1)
    }

    @Test
    fun `not existing task is not modified`() {
        val taskModified = task.copy(id = UUID.randomUUID().toString())
        mapper.insertTask(task)
        verifyTaskInsertion(task)
        Assertions.assertTrue(mapper.updateTask(taskModified) == 0)
    }

    @Test
    fun `non existing task is not being deleted`() {
        Assertions.assertTrue(mapper.deleteTask(uuid) == 0)
        Assertions.assertTrue(mapper.getTask(uuid) == null)
    }

    @Test
    fun `existing task is being deleted`() {
        mapper.insertTask(task)
        verifyTaskInsertion(task)
        Assertions.assertTrue(mapper.deleteTask(uuid) == 1)
        Assertions.assertTrue(mapper.getTask(uuid) == null)
    }

    @Test
    fun `getAllTasks returns all users tasks`() {
        val userTasksCount = 10
        val insertedTasks = insertMultipleTasks(userTasksCount)
        verifyTasksInsertion(insertedTasks)
        Assertions.assertTrue(mapper.getAllTasks(userId).size == userTasksCount)
    }

    @Test
    fun `getAllTasks returns 0 tasks for non existing user`() {
        val existingUserTasksCount = 10
        val insertedTasks = insertMultipleTasks(existingUserTasksCount)
        verifyTasksInsertion(insertedTasks)
        Assertions.assertTrue(mapper.getAllTasks(userIdNonExisting).isEmpty())
    }

    @Test
    fun `getTask with valid id returns single user task`() {
        mapper.insertTask(task)
        verifyTaskInsertion(task)
        Assertions.assertTrue(mapper.getTask(uuid) == task)
    }

    @Test
    fun `getTask with invalid id does not return any task`() {
        mapper.insertTask(task)
        verifyTaskInsertion(task)
        val randomUUID = UUID.randomUUID().toString()
        Assertions.assertTrue(mapper.getTask(randomUUID) == null)
    }

    @Test
    fun `filter important tasks returns only important tasks`() {
        val importantTaskCount = 5
        val notImportantTaskCount = 10
        val insertedImportantTasks = insertImportantTasks(importantTaskCount)
        val insertedNotImportantTasks = insertNotImportantTasks(notImportantTaskCount)
        verifyTasksInsertion(insertedImportantTasks)
        verifyTasksInsertion(insertedNotImportantTasks)

        val importantTasks = mapper.filterImportantTasks(userId)
        Assertions.assertTrue(importantTasks.size == importantTaskCount)
        for (importantTask in importantTasks) {
            Assertions.assertEquals(true, importantTask.important)
        }
    }

    @Test
    fun `filter open tasks returns only open tasks`() {
        val doneTaskCount = 5
        val notDoneTaskCount = 10
        val insertedDoneTasks = insertDoneTasks(doneTaskCount)
        val insertedNotDoneTasks = insertNotDoneTasks(notDoneTaskCount)
        verifyTasksInsertion(insertedDoneTasks)
        verifyTasksInsertion(insertedNotDoneTasks)

        val openTasks = mapper.filterOpenTasks(userId)
        Assertions.assertTrue(openTasks.size == notDoneTaskCount)
        for (doneTask in openTasks) {
            Assertions.assertEquals(false, doneTask.done)
        }
    }

    @Test
    fun `filter open and important tasks returns only open and important tasks`() {
        val openAndImportantTaskCount = 5
        val doneOrNotImportantTaskCount = 10
        val insertedNotImportantTasks = insertNotImportantTasks(doneOrNotImportantTaskCount)
        val insertedDoneTasks = insertDoneTasks(doneOrNotImportantTaskCount)
        val insertedOpenAndImportantTasks = insertImportantAndOpenTasks(openAndImportantTaskCount)

        verifyTasksInsertion(insertedNotImportantTasks)
        verifyTasksInsertion(insertedDoneTasks)
        verifyTasksInsertion(insertedOpenAndImportantTasks)

        val openAndImportantTasks = mapper.filterImportantAndOpenTasks(userId)
        Assertions.assertTrue(openAndImportantTasks.size == openAndImportantTaskCount)
        for (task in openAndImportantTasks) {
            Assertions.assertEquals(false, task.done)
            Assertions.assertEquals(true, task.important)
        }
    }

    @Test
    fun `correct count of open tasks is retrieved`() {
        val openTaskCount = 5
        val doneTaskCount = 2
        val insertedNotDoneTasks = insertNotDoneTasks(openTaskCount)
        val insertedDoneTasks = insertDoneTasks(doneTaskCount)
        verifyTasksInsertion(insertedNotDoneTasks)
        verifyTasksInsertion(insertedDoneTasks)

        Assertions.assertTrue(mapper.openTasksCount(userId) == openTaskCount)
    }

    @Test
    fun `correct count of open and important tasks is retrieved`() {
        val openAndImportantTaskCount = 5
        val doneTaskCount = 2
        val insertedImportantAndOpenTasks = insertImportantAndOpenTasks(openAndImportantTaskCount)
        val insertedDoneTasks = insertDoneTasks(doneTaskCount)
        verifyTasksInsertion(insertedImportantAndOpenTasks)
        verifyTasksInsertion(insertedDoneTasks)

        Assertions.assertTrue(mapper.openImportantTasksCount(userId) == openAndImportantTaskCount)
    }

    @Test
    fun `correct count of all tasks is retrieved for user`() {
        val totalTaskCount = 10
        val insertedTasks = insertMultipleTasks(totalTaskCount)
        verifyTasksInsertion(insertedTasks)

        Assertions.assertTrue(mapper.totalTasksCount(userId) == totalTaskCount)
    }

    @Test
    fun `getUserTask with valid task id and valid user id returns single user task`() {
        val uid = UUID.randomUUID().toString()
        val randomTask = randomTask(i = 1, uid = uid)
        mapper.insertTask(randomTask)
        verifyTaskInsertion(randomTask)

        Assertions.assertTrue(mapper.getUserTask(randomTask.id, uid) == randomTask)
    }

    @Test
    fun `getUserTask with valid task id and invalid user id returns null`() {
        val uid = UUID.randomUUID().toString()
        val randomTask = randomTask(i = 1, uid = uid)
        mapper.insertTask(randomTask)
        verifyTaskInsertion(randomTask)

        val wrongUid = UUID.randomUUID().toString()
        Assertions.assertTrue(mapper.getUserTask(randomTask.id, wrongUid) == null)
    }

    private fun verifyTasksInsertion(tasks: Collection<Task>) {
        for (task in tasks) {
            verifyTaskInsertion(task)
        }
    }

    private fun verifyTaskInsertion(task: Task) {
        val retrievedTask = mapper.getTask(task.id)
        Assertions.assertNotEquals(null, retrievedTask, "Task was not inserted correctly")
        Assertions.assertEquals(task, retrievedTask, "Task was not retrieved correctly")
    }

    private fun insertImportantTasks(importantTaskCount: Int): Collection<Task> {
        val tasks = mutableListOf<Task>()
        for (i: Int in 1..importantTaskCount) {
            val task = randomTask(i, important = true)
            mapper.insertTask(task)
            tasks.add(task)
        }
        return tasks
    }

    private fun insertNotImportantTasks(notImportantTaskCount: Int): Collection<Task> {
        val tasks = mutableListOf<Task>()
        for (i: Int in 1..notImportantTaskCount) {
            val task = randomTask(i, important = false)
            mapper.insertTask(task)
            tasks.add(task)
        }
        return tasks
    }

    private fun insertDoneTasks(doneTaskCount: Int): Collection<Task> {
        val tasks = mutableListOf<Task>()
        for (i: Int in 1..doneTaskCount) {
            val task = randomTask(i, done = true)
            mapper.insertTask(task)
            tasks.add(task)
        }
        return tasks
    }

    private fun insertNotDoneTasks(notDoneTaskCount: Int): Collection<Task> {
        val tasks = mutableListOf<Task>()
        for (i: Int in 1..notDoneTaskCount) {
            val task = randomTask(i, done = false)
            mapper.insertTask(task)
            tasks.add(task)
        }
        return tasks
    }

    private fun insertImportantAndDoneTasks(importantAndDoneTaskCount: Int): Collection<Task> {
        val tasks = mutableListOf<Task>()
        for (i: Int in 1..importantAndDoneTaskCount) {
            val task = randomTask(i, important = true, done = true)
            mapper.insertTask(task)
            tasks.add(task)
        }
        return tasks
    }

    private fun insertImportantAndOpenTasks(importantAndDoneTaskCount: Int): Collection<Task> {
        val tasks = mutableListOf<Task>()
        for (i: Int in 1..importantAndDoneTaskCount) {
            val task = randomTask(i, important = true, done = false)
            mapper.insertTask(task)
            tasks.add(task)
        }
        return tasks
    }

    private fun insertMultipleTasks(randomTaskCount: Int): Collection<Task> {
        val tasks = mutableListOf<Task>()
        for (i: Int in 1..randomTaskCount) {
            val task = randomTask(i)
            mapper.insertTask(task)
            tasks.add(task)
        }
        return tasks
    }

    private fun randomTask(i: Int, uid: String = userId, important: Boolean = Random.nextBoolean(), done: Boolean = Random.nextBoolean()): Task {
        val uuidTemp = UUID.randomUUID().toString()
        val description = "Description of task $i"
        val title = "Task $i"
        return Task(uuidTemp, uid, title, description, important, done)
    }
}
