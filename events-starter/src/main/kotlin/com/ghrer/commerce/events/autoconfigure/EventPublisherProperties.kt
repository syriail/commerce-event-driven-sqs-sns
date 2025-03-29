package com.ghrer.commerce.events.autoconfigure

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.event.publisher")
class EventPublisherProperties(
    val inventorySns: String,
    val ordersSns: String,
) {

    private val logger = KotlinLogging.logger { }

    @PostConstruct
    fun init() {
        logger.debug {
            "EventPublisherProperties initialized with $inventorySns and $ordersSns"
        }
    }
}
