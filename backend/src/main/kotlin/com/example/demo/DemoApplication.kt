package com.example.demo

import org.flywaydb.core.Flyway
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("com.example.demo.dao.mappers")
class DemoApplication(val config: YamlConfiguration) {

    @EventListener(ApplicationReadyEvent::class)
    fun initDatabase() {
        val flyway = Flyway.configure().dataSource(config.url, config.username, config.password).load()
        flyway.clean()
        flyway.migrate()
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@Configuration
class YamlConfiguration {
    @Value("\${spring.datasource.url}")
    val url: String = ""

    @Value("\${spring.datasource.username}")
    val username: String = ""

    @Value("\${spring.datasource.password}")
    val password: String = ""
}
