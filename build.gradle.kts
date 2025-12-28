plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "tech.ccat.controller"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("tech.ccat.controller.ServerController")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "tech.ccat.controller.ServerController",
            "Implementation-Title" to "ServerController",
            "Implementation-Version" to project.version
        )
    }
}

tasks.shadowJar {
    archiveBaseName.set("ServerController")
    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes(
            "Main-Class" to "tech.ccat.controller.ServerController",
            "Implementation-Title" to "ServerController",
            "Implementation-Version" to project.version
        )
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.0")
}

tasks.test {
    useJUnitPlatform()
}