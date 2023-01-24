buildscript {
    repositories { mavenCentral() }

    dependencies { classpath("org.glavo.kala:kala-platform:0.10.0") }
}

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("org.glavo.load-maven-publish-properties") version "0.1.0"
    id("org.glavo.compile-module-info-plugin") version "2.0"
}

allprojects {

    apply {
        plugin("java-library")
        plugin("maven-publish")
        plugin("signing")
        plugin("org.glavo.compile-module-info-plugin")
    }

    group = "org.glavo"
    version = "0.2.0"// + "-SNAPSHOT"
    description = "Minimal library for creating PNG images"

    java {
        withSourcesJar()
    }

    val javadocJar = tasks.create<Jar>("javadocJar") {
        group = "build"
        archiveClassifier.set("javadoc")
    }

    tasks.compileJava {
        options.release.set(8)

        sourceCompatibility = "9"
    }

    tasks.compileTestJava {
        options.release.set(17)
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        if (project != rootProject) {
            api(rootProject)
        }
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                version = project.version.toString()
                artifactId = project.name

                from(components["java"])
                artifact(javadocJar)

                pom {
                    name.set(project.name)
                    description.set(project.description)
                    url.set("https://github.com/Glavo/SimplePNG")

                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("glavo")
                            name.set("Glavo")
                            email.set("zjx001202@gmail.com")
                        }
                    }

                    scm {
                        url.set("https://github.com/Glavo/SimplePNG")
                    }
                }
            }
        }
    }

    if (rootProject.ext.has("signing.key")) {
        signing {
            useInMemoryPgpKeys(
                rootProject.ext["signing.keyId"].toString(),
                rootProject.ext["signing.key"].toString(),
                rootProject.ext["signing.password"].toString(),
            )
            sign(publishing.publications["maven"])
        }
    }

}

dependencies {
    testImplementation(project(":simple-png-imageio"))
    testImplementation(project(":simple-png-javafx"))

    testImplementation("org.apache.commons:commons-imaging:1.0-alpha3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

// Setup JavaFX
run {
    var classifer = when (kala.platform.Platform.CURRENT_PLATFORM.operatingSystem) {
        kala.platform.OperatingSystem.LINUX -> "linux"
        kala.platform.OperatingSystem.WINDOWS -> "win"
        kala.platform.OperatingSystem.MACOS -> "mac"
        else -> return@run
    }

    when (kala.platform.Platform.CURRENT_PLATFORM.architecture) {
        kala.platform.Architecture.X86_64 -> {}
        kala.platform.Architecture.X86 -> classifer += "-x86"
        kala.platform.Architecture.AARCH64 -> classifer += "-aarch64"
        kala.platform.Architecture.ARM -> if (classifer == "linux") classifer = "linux-arm32-monocle" else return@run
        else -> return@run
    }

    val modules = listOf("base", "graphics")

    dependencies {
        for (module in modules) {
            testImplementation("org.openjfx:javafx-$module:17.0.2:$classifer")

        }
    }
}

// ./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(rootProject.ext["sonatypeStagingProfileId"].toString())
            username.set(rootProject.ext["sonatypeUsername"].toString())
            password.set(rootProject.ext["sonatypePassword"].toString())
        }
    }
}
