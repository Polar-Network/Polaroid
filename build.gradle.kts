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
    api("com.github.Minestom:Minestom:-SNAPSHOT")
    api("net.kyori:adventure-text-minimessage:4.12.0")
}

tasks {
    java  {
        withJavadocJar()
        withSourcesJar()
    }

    compileJava {
        options.encoding = "UTF-8"  
        options.release.set(17)
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