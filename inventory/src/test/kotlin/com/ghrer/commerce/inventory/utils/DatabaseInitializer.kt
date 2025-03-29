package com.ghrer.commerce.inventory.utils

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.atomic.AtomicBoolean

class DatabaseInitializer : BeforeAllCallback {

    private val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:15"))
        .withDatabaseName("inventory")

    override fun beforeAll(context: ExtensionContext?) {
        if (!INITIALIZED.getAndSet(true)) {
            postgresContainer.start()
            addPostgresProperties()
            runDatabaseMigration()
        }
    }

    private fun addPostgresProperties() {
        System.setProperty("spring.datasource.url", postgresContainer.jdbcUrl)
        System.setProperty("spring.datasource.username", postgresContainer.username)
        System.setProperty("spring.datasource.password", postgresContainer.password)
    }

    private fun runDatabaseMigration() {
        Flyway
            .configure()
            .dataSource(postgresContainer.jdbcUrl, postgresContainer.username, postgresContainer.password)
            .load()
            .migrate()
    }

    companion object {
        val INITIALIZED = AtomicBoolean(false)
    }
}
