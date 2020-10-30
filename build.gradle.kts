plugins {
    // Apply the java-library plugin to add support for Java Library
    `java-library`

    // Apply the application plugin to add support for building a CLI application.
    application

    // Nebula semantic release plugins.
    id("nebula.release") version "15.2.0"
    id("nebula.javadoc-jar") version "17.3.2"
    id("nebula.source-jar") version "17.3.2"
}

apply <nebula.plugin.publishing.maven.MavenNebulaPublishPlugin>()

group = "com.elemency"
description = "- Midi4J - (Rt)Midi for Java -\n" +
        "Small Java Midi library (w.i.p.) bridged, via JNA binding, " +
        "to a slightly revisited RtMidi cross platform realtime C++ Midi library."

var javaVersion = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public")
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api("junit:junit:4.13.1")

    // These dependencies are used internally, and not exposed to consumers on their own compile classpath.
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("net.java.dev.jna:jna:5.5.0")
    implementation("junit:junit:4.13.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")

    // Use JUnit test framework
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-params:5.3.1")
}

application {
    // Define the main class for the application.
    mainClassName = "com.elemency.Midi4J.Examples.App"
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    jar {
        manifest {
            attributes(
                    mapOf("Implementation-Title" to project.name,
                    "Implementation-Version" to project.version)
            )
        }
    }

    test {
            useJUnitPlatform()
    }
}
