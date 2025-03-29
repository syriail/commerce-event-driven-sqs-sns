#!/bin/sh

echo "waiting for localstack to be ready"

until aws --endpoint-url=http://localstack:4566 sqs list-queues; do
    >&2 echo "SQS is not ready.. sleeping"
      sleep 1
done

echo "Localstack is ready"

echo "Creating SNS topics"

aws sns create-topic --endpoint-url=http://localstack:4566 --name orders-sns.fifo --attributes "FifoTopic=true,ContentBasedDeduplication=true"

# arn:aws:sns:eu-central-1:000000000000:orders-sns.fifo

echo "Creating SQS"

aws sqs create-queue --endpoint-url=http://localstack:4566 --queue-name orders_topic_orders_service.fifo --attributes "FifoQueue=true,ContentBasedDeduplication=true"
aws sqs create-queue --endpoint-url=http://localstack:4566 --queue-name orders_topic_payment_service.fifo --attributes "FifoQueue=true,ContentBasedDeduplication=true"
aws sqs create-queue --endpoint-url=http://localstack:4566 --queue-name orders_topic_inventory_service.fifo --attributes "FifoQueue=true,ContentBasedDeduplication=true"

echo "SQS has been created"

echo "Subscribe Topics"


aws --endpoint-url=http://localstack:4566 sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:orders-sns.fifo \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:orders_topic_orders_service.fifo \
    --attribute RawMessageDelivery=true \
    --attributes '{"RawMessageDelivery":"true","FilterPolicy":"{\"eventType\":[\"ORDER_PAYMENT_SUCCESSFUL\",\"ORDER_PAYMENT_FAILED\"]}"}'


aws --endpoint-url=http://localstack:4566 sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:orders-sns.fifo \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:orders_topic_payment_service.fifo \
    --attributes '{"RawMessageDelivery":"true","FilterPolicy":"{\"eventType\":[\"ORDER_CREATED\"]}"}'

aws --endpoint-url=http://localstack:4566 sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:orders-sns.fifo \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:orders_topic_inventory_service.fifo \
    --attributes '{"RawMessageDelivery":"true","FilterPolicy":"{\"eventType\":[\"ORDER_CREATED\"]}"}'
    

