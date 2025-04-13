## Description

This project is to showcase how to implement event driven architecture using AWS SNS FIFO and SQS FIFO.

It is the practical part of the article
[Can we implement Event-Driven Architecture using AWS Kinesis or SNS?](https://medium.com/@hussin.ghrer/can-we-implement-event-driven-architecture-using-aws-kinesis-or-sns-04bcd70f7ab6)

The project represents an eCommerce platform which handles placing orders, payments,
shipment and emailing.

## Architecture

The following diagram shows a punch of services which are parts of the eCommerce platform.

* Order service, Payments service and Shipping service all emit events related to orders.
* The events are published to the AWS SNS FIFO topic: `orders-topic.fifo`.
* Each service interested in consuming orders related events, subscribe to `orders-topic.fifo` via dedicated AWS SQS FIFI.
* Each SQS subscribes to `orders-topic.fifo` filters out the events which the related service is not interested in. Therefor, the service only receive the events it will actually process.

![Architecture using SNS FIFO and SQS FIFO](./assests/ecomm-arch-sns-sqs.svg)

## Implementation

The project consists of:
* `checkout` service: Acts as a gateway to check out operations.
* `orders` service: Is responsible for creating and updating orders.
* `inventory` service: Is responsible to manage items in the inventory
* `payments` service: Mocks payments processing
* `events-starter`: Is a Spring Boot starter which provides the common events model and handles common concern functionality.
It exposes the bean `EventPublisherProxy` which knows where to publish each event to depending on its topic.
So that the services are offloaded from the burden to know where and how the events will be published.
* Some services shown in the architecture are not implemented. They should be implemented the same way as the others.

## Run Tests

Each service, including `events-start` can be tested individually.

Since I use Test Containers, all you need is Docker to be installed and running on your machine

However, `orders`, `inventory` and `payments` services depend on `events-starter` which needs to be published to your local maven repository.
So you need to run `./gradlew publish` in `events-starter` directory to publish the library to your `~/.m2` folder.
One published, other services can pull it and use it.

## Run The Services

To run the services as one platform, all you need is to run `chmod +x docker-up.sh` and then `docker-up.sh`

If you are interested in knowing more details, I will explain in the following subsections how it works.

### Infrastructure
The platform requires some infrastructure to be spun out using `docker-compose.yml`

***Database***

`orders` and `inventory` services require database server. So that a Postgresql container is defined and named `commerce-db`.
The entrypoint of the container is defined in `/scripts/init-db` whose responsibility is to create the required databases: `orders` and `inventory`.

I also defined `test-data-setup` to run a script which:
* Waits for all services to be started
* Cleans up existing data if any
* Inserts test data

***AWS Resources***

I use [LocalStack](https://www.localstack.cloud/) as a localized AWS environment.
I also defined `aws-resources-setup` to create the required AWS resources:
* SNS FIFO topic `orders-sns.fifo` to publish orders related events to
* Three SQS FIFO which subscribe to the topic on behalf of the interested services
* Each SQS subscribes to `orders-sns.fifo` and define a filter to receive only the events which the underlying service needs

### Run script
Since I haven't published `events-starter` to a public maven repository, so I have to solve two problems:
* Automatically publishing the starter to your local maven repository
* Docker build doesn't have access to any folder outside its context

***Publish `events-starter`***

It is as simple as running the command `./events-starter/gradlew --project-dir=events-starter --full-stacktrace clean assemble publish`

***Make the starter available in docker build context***

After having the starter published under `~/.m2/repository/com/ghrer/commerce`, I copy the same folder to each project's folder under that name `local-repo`,
so that it could be used by each service's Dockerfile.

In Dockerfile I create the folder `/root/.m2/` to act as a maven local repository for each context,
and then copy the content of `local-repo/` to it.

This way, each build context will have the starter in its local maven repository and the starter could be pulled from it.

***Build and run***

It is as simple as running `docker compose up $@`

***Clean up***

To remove all created `local-repo` folders.

### Postman Collection
Simple import the collection available [here](./assests/Commerce.postman_collection.json)

To place an order with successful payment, use ***Add items to cart*** then ***Checkout-Place order-Payment success***.

To place an order with declined payment, use ***Add items to cart*** then ***Checkout-Place order-Payment declined***.




 


