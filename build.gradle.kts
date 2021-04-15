import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("java")
    id("maven")
}

group = "me.dkim19375"
version = "1.0.1"

val javaVersion = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    named<ShadowJar>("shadowJar") {
        finalizedBy("deleteFiles")
        finalizedBy("copyFileToServer_8")
        finalizedBy("copyFileToServer_16")
    }
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:+")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
}

tasks.register<Copy>("copyFileToServer_8") {
    File("../.TestServers/1.8/plugins/" + project.name + "-" + project.version + "-all.jar").delete()
    from("build/libs/" + project.name + "-" + project.version + "-all.jar")
    into("../.TestServers/1.8/plugins")
    include("*.jar")
}

tasks.register<Copy>("copyFileToServer_16") {
    File("../.TestServers/1.16/plugins/" + project.name + "-" + project.version + "-all.jar").delete()
    from("build/libs/" + project.name + "-" + project.version + "-all.jar")
    into("../.TestServers/1.16/plugins")
    include("*.jar")
}

tasks.register<Copy>("deleteFiles") {
    File("../.TestServers/1.8/plugins/" + project.name + "-" + project.version + "-all.jar").delete()
    File("../.TestServers/1.16/plugins/" + project.name + "-" + project.version + "-all.jar").delete()
}
