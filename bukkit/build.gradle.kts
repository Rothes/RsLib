repositories {
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "papermc"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "jitpack.io"
        url = uri("https://jitpack.io/")
    }
}

dependencies {
//    compileOnlyApi("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
//    compileOnlyApi("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnlyApi(project(":bukkit:paper-wrapper"))

    api("org.bstats:bstats-bukkit:3.0.0")
    api("me.carleslc.Simple-YAML:Simple-Yaml:1.8.3")

    api("net.kyori:adventure-api:4.12.0")
    api("net.kyori:adventure-platform-bukkit:4.2.0")
    api("net.kyori:adventure-text-minimessage:4.12.0")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveFileName.set("RsLib-Bukkit-${project.version}.jar")

    relocate("net.kyori", "io.github.rothes.rslib.lib.net.kyori")
    relocate("org.bstats", "io.github.rothes.rslib.lib.org.bstats")
    relocate("org.simpleyaml", "io.github.rothes.rslib.lib.org.simpleyaml")
    relocate("org.yaml", "io.github.rothes.rslib.lib.org.yaml")
    relocate("kotlin", "io.github.rothes.rslib.lib.kotlin")

    dependencies {
        exclude("com.google.code.gson:gson")
    }
}