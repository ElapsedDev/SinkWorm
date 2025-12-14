plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("maven-publish")
}

group = "dev.elapsed.sinkworm"
version = "1.0-0"

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

    implementation("com.sparkjava:spark-core:2.9.4")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks {


    jar {
        manifest {
            attributes["Main-Class"] = "dev.elapsed.sinkworm.SinkWorm"
        }
    }

    shadowJar {
        archiveFileName.set("SinkWorm.jar")
        destinationDirectory.set(file("out"))
    }

}

//publishing {
//    publications.create<MavenPublication>("maven") {
//        from(components["java"])
//    }
//}
