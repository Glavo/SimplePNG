plugins {
    id("java")
    id("org.glavo.compile-module-info-plugin") version "2.0"
}

group = "org.glavo"
version = "0.1.0-SNAPSHOT"

tasks.compileJava {
    options.release.set(8)
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.openjfx:javafx-graphics:17:linux") // For javafx.scene.image

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}