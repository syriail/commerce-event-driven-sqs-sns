import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin


plugins {
    val kotlinVersion = "1.9.24"

    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

group = "com.ghrer.commerce"

repositories {
    mavenCentral()
    mavenLocal()
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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")

//  Our own starter
    implementation("com.ghrer.commerce:events-starter:1.0.0")

//    Logging
    implementation("io.github.microutils:kotlin-logging:2.1.23")

//    AWS
    implementation("io.awspring.cloud:spring-cloud-aws-starter")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")

//    Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
