plugins {
    kotlin("jvm") version "1.8.0"
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin-platform-jvm")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")

    group = "io.github.rothes.rslib"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.shadowJar {
        archiveBaseName.set("RsLib")
    }

}

subprojects {
    publishing {
        repositories {
            mavenLocal()
        }
        publications {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])

//                    artifact(project.tasks["kotlinSourcesJar"])
//                    artifact(project.tasks["shadowJar"])

                    artifactId = project.name
                    groupId = project.group as String?
                    version = project.version as String?

//                    pom.withXml {
//                        // Add all compileOnly to provided scope
//                        print(asNode()["dependencies"]::class.java.canonicalName)
//                        project.configurations.compileOnly.get().allDependencies.forEach { dependency ->
//                            ((asNode().get("dependencies") as NodeList)[0] as Node).appendNode("dependency").also {
//                                it.appendNode("groupId", dependency.group)
//                                it.appendNode("artifactId", dependency.name)
//                                it.appendNode("version", dependency.version)
//                                it.appendNode("scope", "provided")
//                            }
//                        }
//
//                        // Edit all implementation scope to compile
//                        (((asNode().get("dependencies") as NodeList)[0] as Node).get("dependency") as NodeList).forEach {
//                            val node = it as Node
//                            project.configurations.implementation.get().allDependencies.find { dependency ->
//                                return@find ((node["groupId"] as NodeList)[0] as Node).value() == dependency.group
//                                        && ((node["artifactId"] as NodeList)[0] as Node).value() == dependency.name
//                                        && ((node["version"] as NodeList)[0] as Node).value() == dependency.version
//                                        && (((node["scope"] as NodeList)[0] as Node).value() as NodeList)[0] == "runtime"
//                            }?.run {
//                                (((node["scope"] as NodeList)[0] as Node).value() as NodeList)[0] = "compile"
//                            }
//                        }
//
//                    }

                }
            }
        }
    }
}