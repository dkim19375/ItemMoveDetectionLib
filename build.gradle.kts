plugins {
    java
    signing
    `maven-publish`
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "me.dkim19375"
version = "1.1.8"

val javaVersion = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    withSourcesJar()
    withJavadocJar()
}

license {
    header.set(resources.text.fromFile(rootProject.file("LICENSE")))
    include("**/*.java")
}

tasks.shadowJar {
    finalizedBy("deleteFiles")
    finalizedBy("copyFileToServer_8")
    finalizedBy("copyFileToServer_18")
}

tasks.wrapper {
    finalizedBy("licenseFormat")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        exclude(module = "bungeecord-api")
    }
}

tasks.processResources {
    from("${project.rootDir}/src/main/resources/plugin.yml") {
        expand("pluginVersion" to project.version)
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register<Copy>("copyFileToServer_8") {
    File("../.TestServers/1.8/plugins/" + project.name + "-" + project.version + "-all.jar").delete()
    from("build/libs/" + project.name + "-" + project.version + "-all.jar")
    into("../.TestServers/1.8/plugins")
    include("*.jar")
}

tasks.register<Copy>("copyFileToServer_18") {
    File("../.TestServers/1.18/plugins/" + project.name + "-" + project.version + "-all.jar").delete()
    from("build/libs/" + project.name + "-" + project.version + "-all.jar")
    into("../.TestServers/1.18/plugins")
    include("*.jar")
}

tasks.register<Copy>("deleteFiles") {
    File("../.TestServers/1.8/plugins/" + project.name + "-" + project.version + "-all.jar").delete()
    File("../.TestServers/1.18/plugins/" + project.name + "-" + project.version + "-all.jar").delete()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "io.github.dkim19375"
            artifactId = "item-move-detection-lib"
            version = project.version as String
            from(components["java"])
            pom {
                name.set("ItemMoveDetectionLib")
                description.set("A spigot library used to detect items moving through player inventories")
                url.set("https://github.com/dkim19375/ItemMoveDetectionLib")

                packaging = "jar"

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("dkim19375")
                    }
                }

                scm {
                    connection.set("scm:git:git://https://github.com/dkim19375/ItemMoveDetectionLib.git")
                    developerConnection.set("scm:git:ssh://https://github.com/dkim19375/ItemMoveDetectionLib.git")
                    url.set("https://github.com/dkim19375/ItemMoveDetectionLib")
                }
            }
        }
    }
}

nexusPublishing {
    packageGroup.set("io.github.dkim19375")
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.findProperty("mavenUsername") as? String ?: return@sonatype)
            password.set(project.findProperty("mavenPassword") as? String ?: return@sonatype)
        }
    }
}

signing.sign(publishing.publications["mavenJava"])