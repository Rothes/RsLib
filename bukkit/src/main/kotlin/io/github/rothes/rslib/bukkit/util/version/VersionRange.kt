package io.github.rothes.rslib.bukkit.util.version

class VersionRange(
    val from: SemanticVersion,
    val to: SemanticVersion,
) {

    constructor(from: String, to: String) : this(SemanticVersion(from), SemanticVersion(to))

    fun matches(version: SemanticVersion) = from.compare(version) != SemanticVersion.Result.GREATER
            && to.compare(version) != SemanticVersion.Result.LESS

}