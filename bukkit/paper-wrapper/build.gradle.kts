repositories {
    maven {
        name = "papermc"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencies {
    api("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.shadowJar {
    archiveFileName.set("paper-wrapper.jar")
}