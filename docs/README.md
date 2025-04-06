## Description

This project is to showcase how to implement event driven architecture using AWS SNS FIFO and SQS FIFO.

It is the practical part of the article
[Can we implement Event-Driven Architecture with AWS Kinesis or SNS?](https://medium.com/@hussin.ghrer/)

The project represents an eCommerce platform which handles placing orders, payments,
shipment and emailing.

## Architecture

We are going to replace the abstracted event channel with real components.

Since we are handling only events related to orders, so we need only one SNS FIFO Topic to
publish events to. Let's name it `orders-sns.fifo`.

For each service which consumes events, a SQS FIFO is to be created for it. Therefore,
`orders_topic_payment_service.fifo` is the SQS which subscribes to `orders-topic.fifo` and filters all the events
which should be handled by payments service.

![Architecture using SNS FIFO and SQS FIFO](./assests/ecomm-arch-sns-sqs.svg)

## Run The Services

`chmod +x docker-up.sh`
