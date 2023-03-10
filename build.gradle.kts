plugins {
    id("java")
    id("maven-publish")
    id("java-library")
}

group = "net.polar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    api("com.github.Minestom:Minestom:a9e319f961")
    api("net.kyori:adventure-text-minimessage:4.12.0")
    api("com.github.Polar-Network:MiniMessenger:0265bf9331")
    api("com.google.guava:guava:30.1.1-jre")
    api("org.mongodb:mongodb-driver-sync:4.9.0")
}

tasks {

    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_17.majorVersion
        targetCompatibility = JavaVersion.VERSION_17.majorVersion
    }

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            this.groupId = "net.polar"
            this.artifactId = "Polaroid"
            this.version = "1.0-SNAPSHOT"
            from(components["java"])
        }
    }
}