package com.ghrer.commerce.events.util

// class LocalStackInitializer : BeforeAllCallback {
//
//    private val localStack = LocalStackContainer(DockerImageName.parse("localstack/localstack:3.8.1"))
//
//    override fun beforeAll(context: ExtensionContext?) {
//        if (!IS_INITIALIZED.getAndSet(true)) {
//            localStack.start()
//            setLocalstackProperties()
//        }
//    }
//
//    private fun setLocalstackProperties() {
//        System.setProperty("spring.cloud.aws.region.static", localStack.region)
//        System.setProperty("spring.cloud.aws.credentials.access-key", localStack.accessKey)
//        System.setProperty("spring.cloud.aws.credentials.secret-key", localStack.secretKey)
//        System.setProperty(
//            "spring.cloud.aws.sns.endpoint",
//            localStack.getEndpointOverride(LocalStackContainer.Service.SNS).toString()
//        )
//    }
//
//    companion object {
//        val IS_INITIALIZED = AtomicBoolean(false)
//    }
// }
