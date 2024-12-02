package com.example.demo.dao.mappers

import com.example.demo.domain.Task
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface TaskMapper {
    fun insertTask(@Param("task") task: Task): Int
    fun getAllTasks(@Param("userId") userId: String): Collection<Task>
    fun getTask(@Param("taskId") taskId: String): Task?
    fun updateTask(@Param("task") task: Task): Int
    fun deleteTask(@Param("taskId") taskId: String): Int
    fun filterImportantAndOpenTasks(userId: String): Collection<Task>
    fun filterImportantTasks(userId: String): Collection<Task>
    fun filterOpenTasks(userId: String): Collection<Task>
    fun getUserTask(@Param("taskId") taskId: String, @Param("userId") userId: String): Task?
    fun openTasksCount(userId: String): Int
    fun openImportantTasksCount(userId: String): Int
    fun totalTasksCount(userId: String): Int
}
