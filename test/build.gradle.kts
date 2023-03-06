plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "net.polar"
version = "1.0-SNAPSHOT"

val testFolder = File(project.projectDir, "server").apply { mkdirs() }

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(rootProject)
}


tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes(
                "Main-Class" to "net.polar.test.TestServer"
            )
        }
        val file = archiveFile.get().asFile
        doLast {
            copy {
                from(file)
                into(testFolder)
            }
        }
    }
}