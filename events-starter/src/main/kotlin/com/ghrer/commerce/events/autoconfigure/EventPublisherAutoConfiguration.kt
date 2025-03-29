package com.ghrer.commerce.events.autoconfigure

import com.ghrer.commerce.events.CommerceEventPublisher
import com.ghrer.commerce.events.EventPublisherProxy
import com.ghrer.commerce.events.EventPublisherProxyImpl
import com.ghrer.commerce.events.inventory.InventoryEventPublisher
import com.ghrer.commerce.events.inventory.SnsInventoryEventPublisher
import com.ghrer.commerce.events.orders.OrdersEventPublisher
import com.ghrer.commerce.events.orders.SnsOrdersEventPublisher
import io.awspring.cloud.sns.core.SnsTemplate
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@ComponentScan(basePackages = ["com.ghrer.commerce.events"])
@AutoConfiguration
@EnableConfigurationProperties(EventPublisherProperties::class)
@ConditionalOnProperty(
    name = ["spring.event.publisher.ordersSns", "spring.event.publisher.inventorySns"]
)
class EventPublisherAutoConfiguration(
    private val eventPublisherProperties: EventPublisherProperties,
    private val snsTemplate: SnsTemplate,
) {

    @Bean
    @ConditionalOnMissingBean
    fun ordersEventPublisher(): OrdersEventPublisher = SnsOrdersEventPublisher(
        eventPublisherProperties.ordersSns,
        snsTemplate
    )

    @Bean
    @ConditionalOnMissingBean
    fun inventoryEventPublisher(): InventoryEventPublisher = SnsInventoryEventPublisher(
        eventPublisherProperties.inventorySns,
        snsTemplate
    )

    @Bean
    @ConditionalOnMissingBean
    fun eventPublisherProxy(publishers: List<CommerceEventPublisher>): EventPublisherProxy {
        return EventPublisherProxyImpl(publishers)
    }
}
