import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin


plugins {
    val kotlinVersion = "1.9.24"

    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion

    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

group = "com.ghrer.commerce"

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/syriail/commerce-event-driven-sqs-sns")
    }
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}
dependencies {
    kapt(platform(SpringBootPlugin.BOM_COORDINATES))
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.3.0"))
    implementation(kotlin("reflect"))

//    Spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")

//  Our own starter
    implementation("com.ghrer.commerce:events-starter:1.0.0")

//    Logging
    implementation("io.github.microutils:kotlin-logging:2.1.23")

//  Typing
    implementation("io.hypersistence:hypersistence-utils-hibernate-62:3.8.0")

//    AWS
    implementation("io.awspring.cloud:spring-cloud-aws-starter")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")

//    Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testcontainers:postgresql")

//    DB
    implementation("org.flywaydb:flyway-core:10.0.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.0.0")
    implementation("org.postgresql:postgresql:42.7.3")
}
