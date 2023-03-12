
rootProject.name = "RsLib"
include("bukkit")
include("bukkit:paper-wrapper")
findProject(":bukkit:paper-wrapper")?.name = "paper-wrapper"
