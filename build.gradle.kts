import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea

    id("org.springframework.boot") version "2.2.7.RELEASE"

    // A Gradle plugin that provides Maven-like dependency management and exclusions
    id("io.spring.dependency-management") version "1.0.9.RELEASE"

    kotlin("jvm") version "1.3.72"

    // instead of "kotlin-allopen". Needed to make Kotlin classes open
    kotlin("plugin.spring") version "1.3.72"

    // Annotation Processing with Kotlin
    kotlin("kapt") version "1.3.72"

    // For Gradle Release
    id("net.researchgate.release") version "2.8.1"

    // Jacoco for test reports
    id("jacoco")

    // OpenApi 3 plugins
    id("com.github.johnrengelman.processes") version "0.5.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.2.0"

    // Jib tool to containarize project
    id("com.google.cloud.tools.jib") version "2.3.0"

}

java.sourceCompatibility = JavaVersion.VERSION_11

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

// Defining integration testing task
sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}
val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

// Build info added to Spring actuator http://<url>:<port>/actuator/info
springBoot {
    buildInfo()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

    val mapStructVersion = "1.3.1.Final"
    implementation("org.mapstruct:mapstruct:$mapStructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapStructVersion")



    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    intTestImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    intTestImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

    val springDocOpenApi = "1.3.9"
    runtimeOnly("org.springdoc:springdoc-openapi-ui:$springDocOpenApi")
    runtimeOnly("org.springdoc:springdoc-openapi-kotlin:$springDocOpenApi")
    // Possibly other springdoc related dependencies like Spring Security
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

// Add extra task for Integration testing so that it runs separately from
// normal unit testing
val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["intTest"].output.classesDirs// + sourceSets["test"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath// + sourceSets["test"].runtimeClasspath
    shouldRunAfter("test")
}
tasks.check { dependsOn(integrationTest) }

//export test coverage
tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = false
    }
}

// JIB configuration
 jib.to.image = "andrefonsecavieira/vocab-enhancer"




