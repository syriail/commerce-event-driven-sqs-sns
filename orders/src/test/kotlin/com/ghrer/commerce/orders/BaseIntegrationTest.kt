package com.ghrer.commerce.orders

import com.ghrer.commerce.orders.util.DatabaseInitializer
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@ExtendWith(DatabaseInitializer::class)
class BaseIntegrationTest
