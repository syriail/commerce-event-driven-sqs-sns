package com.ghrer.commerce.events.autoconfigure

import com.ghrer.commerce.events.EventPublisherProxy
import com.ghrer.commerce.events.inventory.InventoryEventPublisher
import com.ghrer.commerce.events.orders.OrdersEventPublisher
import io.awspring.cloud.sns.core.SnsTemplate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class EventPublisherAutoConfigurationTest {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                EventPublisherAutoConfiguration::class.java
            )
        )
        .withBean(SnsTemplate::class.java, { mock(SnsTemplate::class.java) })

    @Test
    fun `should not instantiate the beans when properties are absent`() {
        contextRunner.run { context ->
            Assertions.assertThat(context).doesNotHaveBean(OrdersEventPublisher::class.java)
            Assertions.assertThat(context).doesNotHaveBean(InventoryEventPublisher::class.java)
            Assertions.assertThat(context).doesNotHaveBean(EventPublisherProxy::class.java)
        }
    }

    @Test
    fun `should instantiate the configured beans`() {
        contextRunner
            .withPropertyValues("spring.event.publisher.ordersSns:orders-sns.fifo")
            .withPropertyValues("spring.event.publisher.inventorySns:inventory-sns.fifo")
            .run { context ->
                Assertions.assertThat(context).hasSingleBean(OrdersEventPublisher::class.java)
                Assertions.assertThat(context).hasSingleBean(InventoryEventPublisher::class.java)
                Assertions.assertThat(context).hasSingleBean(EventPublisherProxy::class.java)
            }
    }

    @Test
    fun `should not instantiate OrdersEventPublisher when one is already instantiated`() {
        contextRunner
            .withPropertyValues("spring.event.publisher.ordersSns:orders-sns.fifo")
            .withPropertyValues("spring.event.publisher.inventorySns:inventory-sns.fifo")
            .withBean(OrdersEventPublisher::class.java, { mock(OrdersEventPublisher::class.java) })
            .run { context ->
                Assertions.assertThat(context).hasSingleBean(OrdersEventPublisher::class.java)
                Assertions.assertThat(context).hasSingleBean(InventoryEventPublisher::class.java)
                Assertions.assertThat(context).hasSingleBean(EventPublisherProxy::class.java)
            }
    }

    @Test
    fun `should not instantiate InventoryEventPublisher when one is already instantiated`() {
        contextRunner
            .withPropertyValues("spring.event.publisher.ordersSns:orders-sns.fifo")
            .withPropertyValues("spring.event.publisher.inventorySns:inventory-sns.fifo")
            .withBean(InventoryEventPublisher::class.java, { mock(InventoryEventPublisher::class.java) })
            .run { context ->
                Assertions.assertThat(context).hasSingleBean(OrdersEventPublisher::class.java)
                Assertions.assertThat(context).hasSingleBean(InventoryEventPublisher::class.java)
                Assertions.assertThat(context).hasSingleBean(EventPublisherProxy::class.java)
            }
    }

    @Test
    fun `should not instantiate EventPublisherProxy when one is already instantiated`() {
        contextRunner
            .withPropertyValues("spring.event.publisher.ordersSns:orders-sns.fifo")
            .withPropertyValues("spring.event.publisher.inventorySns:inventory-sns.fifo")
            .withBean(EventPublisherProxy::class.java, { mock(EventPublisherProxy::class.java) })
            .run { context ->
                Assertions.assertThat(context).hasSingleBean(OrdersEventPublisher::class.java)
                Assertions.assertThat(context).hasSingleBean(InventoryEventPublisher::class.java)
                Assertions.assertThat(context).hasSingleBean(EventPublisherProxy::class.java)
            }
    }
}
