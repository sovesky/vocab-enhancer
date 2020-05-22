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

    // id("net.ltgt.apt") version "0.21"
    // id("net.ltgt.apt-idea") version "0.21"
}
// apply(plugin="net.ltgt.apt-idea")
allprojects {
    ext {
        set("mapStructVersion", "1.3.1.Final")
    }
}

group = "com.sovesky"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val mapStructVersion = ext.get("mapStructVersion") as String
val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

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

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.mapstruct:mapstruct:$mapStructVersion")
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    kapt("org.mapstruct:mapstruct-processor:$mapStructVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    intTestImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    intTestImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
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

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter("test")
}
tasks.check { dependsOn(integrationTest) }

