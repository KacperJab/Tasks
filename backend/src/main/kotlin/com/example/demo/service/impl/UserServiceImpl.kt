package com.example.demo.service.impl

import com.example.demo.dao.mappers.TaskMapper
import com.example.demo.domain.Task
import com.example.demo.domain.User
import com.example.demo.exception.ApiRequestException
import com.example.demo.service.UserService
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class UserServiceImpl(val mapper: TaskMapper) : UserService {

    @Value("\${keycloak.auth-server-url}")
    private val authServerUrl: String = ""

    @Value("\${keycloak.realm}")
    private val realm: String = ""

    override fun getAllUsers(): Collection<User> {
        val url = "$authServerUrl/admin/realms/$realm"
        val auth = SecurityContextHolder.getContext().authentication
        val token = if (auth is KeycloakAuthenticationToken) auth.account.keycloakSecurityContext.tokenString else ""
        val client = WebClient.create(url)
        val userRepresentations = client.get()
            .uri("/users")
            .headers { h -> h.setBearerAuth(token) }
            .retrieve().bodyToMono<MutableList<UserRepresentation>>().block() ?: return emptyList()
        return userRepresentationCollectionToUserCollection(userRepresentations)
    }

    override fun getUser(userId: String): User {
        val url = "$authServerUrl/admin/realms/$realm"
        val auth = SecurityContextHolder.getContext().authentication
        val token = if (auth is KeycloakAuthenticationToken) auth.account.keycloakSecurityContext.tokenString else ""
        val client = WebClient.create(url)
        val userRepresentation = client.get()
            .uri("/users/$userId")
            .headers { h -> h.setBearerAuth(token) }
            .retrieve().bodyToMono<UserRepresentation>().block()
            ?: throw ApiRequestException(HttpStatus.NOT_FOUND, "User with id $userId not found")
        return userRepresentationToUser(userRepresentation)
    }

    override fun getUserTasks(userId: String): Collection<Task> {
        return mapper.getAllTasks(userId)
    }

    override fun getUserTask(userId: String, taskId: String): Task {
        val task = mapper.getUserTask(taskId, userId)
        if (task == null) {
            throw ApiRequestException(HttpStatus.NOT_FOUND, "Task not found")
        } else {
            return task
        }
    }

    override fun getUserFilteredTasks(userId: String, important: Boolean, open: Boolean): Collection<Task> {
        if (important && open) {
            return mapper.filterImportantAndOpenTasks(userId)
        } else if (important) {
            return mapper.filterImportantTasks(userId)
        } else if (open) {
            return mapper.filterOpenTasks(userId)
        }
        return emptyList()
    }

    private fun userRepresentationToUser(userRep: UserRepresentation): User {
        val userId = userRep.id
        val userName = userRep.firstName
        val userSurname = userRep.lastName
        val totalTaskCount = mapper.totalTasksCount(userId)
        val importantTaskCount = mapper.openImportantTasksCount(userId)
        val openTaskCount = mapper.openTasksCount(userId)
        return User(userId, userName, userSurname, openTaskCount, importantTaskCount, totalTaskCount)
    }

    private fun userRepresentationCollectionToUserCollection(usersRep: MutableList<UserRepresentation>) =
        usersRep.map { userRep -> userRepresentationToUser(userRep) }
}
