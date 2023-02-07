plugins {
    id("java")
    id("maven-publish")
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "net.polar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    api("com.github.Minestom:Minestom:-SNAPSHOT")
    api("net.kyori:adventure-text-minimessage:4.12.0")
}