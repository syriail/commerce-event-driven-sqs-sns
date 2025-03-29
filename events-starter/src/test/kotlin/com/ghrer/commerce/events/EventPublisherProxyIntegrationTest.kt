package com.ghrer.commerce.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ghrer.commerce.events.inventory.SnsInventoryEventPublisher
import com.ghrer.commerce.events.inventory.model.InventoryEventType
import com.ghrer.commerce.events.inventory.model.ItemAddedEvent
import com.ghrer.commerce.events.orders.SnsOrdersEventPublisher
import com.ghrer.commerce.events.orders.model.OrderCreatedEvent
import com.ghrer.commerce.events.orders.model.OrderEventType
import com.ghrer.commerce.events.util.FileUtil
import io.awspring.cloud.sns.core.SnsTemplate
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.CreateTopicRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName

@Testcontainers
class EventPublisherProxyIntegrationTest {

    private val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())

    @AfterEach
    fun purgeQueues() {
        sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(orderSqsUrl).build())
        sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(inventorySqsUrl).build())
    }

    @Test
    fun `should send orderEvent to the correct channel`() {
        val eventPayload = FileUtil.readJsonFileAsString(
            "/events/order-created-event.json"
        )
        val orderCreatedEvent = objectMapper.readValue(eventPayload, OrderCreatedEvent::class.java)
        eventPublisherProxy.publish(orderCreatedEvent)

        await().untilAsserted {
            val orderSqsResponse = sqsClient.receiveMessage {
                it.queueUrl(orderSqsUrl)
            }

            val inventorySqsResponse = sqsClient.receiveMessage {
                it.queueUrl(inventorySqsUrl)
            }

            assertThat(orderSqsResponse.messages()).hasSize(1)
            assertThat(inventorySqsResponse.messages()).hasSize(0)
        }
    }

    @Test
    fun `should send InventoryEvent to the correct channel`() {

        val itemAddedEvent = ItemAddedEvent(
            itemId = "item-1",
            quantity = 10
        )
        eventPublisherProxy.publish(itemAddedEvent)

        await().untilAsserted {
            val orderSqsResponse = sqsClient.receiveMessage {
                it.queueUrl(orderSqsUrl)
            }

            val inventorySqsResponse = sqsClient.receiveMessage {
                it.queueUrl(inventorySqsUrl)
            }

            assertThat(orderSqsResponse.messages()).hasSize(0)
            assertThat(inventorySqsResponse.messages()).hasSize(1)
        }
    }

    @Test
    fun `should send the event only once`() {
        val eventPayload = FileUtil.readJsonFileAsString(
            "/events/order-created-event.json"
        )
        val orderCreatedEvent = objectMapper.readValue(eventPayload, OrderCreatedEvent::class.java)
        eventPublisherProxy.publish(orderCreatedEvent)
        eventPublisherProxy.publish(orderCreatedEvent)

        await().untilAsserted {
            val orderSqsResponse = sqsClient.receiveMessage {
                it.queueUrl(orderSqsUrl)
            }
            assertThat(orderSqsResponse.messages()).hasSize(1)
        }
    }

    companion object {
        private const val ORDERS_SNS_TOPIC = "orders-sns.fifo"
        private const val INVENTORY_SNS_TOPIC = "inventory-sns.fifo"

        private lateinit var orderSqsUrl: String
        private lateinit var inventorySqsUrl: String
        private lateinit var snsTemplate: SnsTemplate
        private lateinit var snsClient: SnsClient
        private lateinit var sqsClient: SqsClient
        private lateinit var eventPublisherProxy: EventPublisherProxy

        @Container
        private val localStack = LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.8.1")
        )
        @JvmStatic
        @BeforeAll
        fun setUp() {
            snsClient = setupSnsClient()
            sqsClient = setupSqsClient()
            snsTemplate = SnsTemplate(snsClient)
            orderSqsUrl = setupSqs("orders-sqs.fifo")
            inventorySqsUrl = setupSqs("inventory-sqs.fifo")
            setupSns(ORDERS_SNS_TOPIC, orderSqsUrl, OrderEventType.ORDER_CREATED.name)
            setupSns(INVENTORY_SNS_TOPIC, inventorySqsUrl, InventoryEventType.ITEM_ADDED.name)
            eventPublisherProxy = setupEventPublisherProxy()
        }
        private fun setupSnsClient() = SnsClient.builder()
            .endpointOverride(localStack.endpoint)
            .region(Region.of(localStack.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(localStack.accessKey, localStack.secretKey)
                )
            )
            .build()

        private fun setupSqsClient() = SqsClient.builder()
            .endpointOverride(localStack.endpoint)
            .region(Region.of(localStack.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(localStack.accessKey, localStack.secretKey)
                )
            )
            .build()

        private fun setupSqs(queueName: String) = sqsClient
            .createQueue {
                it.queueName(queueName)
                    .attributes(mapOf(QueueAttributeName.FIFO_QUEUE to "true"))
            }.queueUrl()

        private fun setupSns(topic: String, queueUrl: String, eventType: String) {
            val topicArn = snsClient
                .createTopic(
                    CreateTopicRequest.builder()
                        .name(topic)
                        .attributes(mapOf("FifoTopic" to "true"))
                        .build()
                )
                .topicArn()
            val queueArn = sqsClient
                .getQueueAttributes {
                    it.queueUrl(queueUrl).attributeNames(QueueAttributeName.QUEUE_ARN)
                }
                .attributes()[QueueAttributeName.QUEUE_ARN]
            snsClient.subscribe {
                it.topicArn(topicArn)
                    .protocol("sqs")
                    .endpoint(queueArn)
                    .attributes(
                        mapOf(
                            "RawMessageDelivery" to "true",
                            "FilterPolicy" to "{ \"eventType\": [\"$eventType\"] }"
                        )
                    )
            }
        }

        private fun setupEventPublisherProxy() = EventPublisherProxyImpl(
            listOf(
                SnsOrdersEventPublisher(ORDERS_SNS_TOPIC, snsTemplate),
                SnsInventoryEventPublisher(INVENTORY_SNS_TOPIC, snsTemplate)
            )
        )
    }
}
