package com.ghrer.commerce.orders.persistence

import com.ghrer.commerce.orders.exception.ApplicationException
import com.ghrer.commerce.orders.fixture.OrderFixture
import com.ghrer.commerce.orders.persistence.repository.OrderItemRepository
import com.ghrer.commerce.orders.persistence.repository.OrderRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.transaction.PlatformTransactionManager
import reactor.test.StepVerifier
import java.time.LocalDateTime

class TransactionalOrderPersistenceAdaptorTest {

    private val orderRepository: OrderRepository = mock()

    private val orderItemRepository: OrderItemRepository = mock()

    private val transactionManager: PlatformTransactionManager = mock()

    private val orderMapper = OrderMapper()

    private val orderServiceAdaptor = TransactionalOrderPersistenceAdaptor(
        orderRepository,
        orderItemRepository,
        orderMapper,
        transactionManager
    )

    @Test
    fun `should fail creating order and throw exception when save order items fails`() {
        val request = OrderFixture.getSampleCreateOrderRequest()
        val createdOrder = orderMapper.mapToOrder(request).copy(
            createDate = LocalDateTime.now()
        )
        `when`(orderRepository.save(any())).thenReturn(createdOrder)
        val orderItemsToSave = orderMapper.mapToOrderItems(request, createdOrder.id)
        `when`(orderItemRepository.saveAll(orderItemsToSave)).thenThrow(ApplicationException(false))

        StepVerifier.create(orderServiceAdaptor.createOrder(request))
            .expectErrorMatches {
                it is ApplicationException
            }.verify()
    }
}
