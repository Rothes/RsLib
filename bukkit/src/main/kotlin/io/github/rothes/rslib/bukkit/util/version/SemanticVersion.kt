package io.github.rothes.rslib.bukkit.util.version

class SemanticVersion(
    vararg val nums: Int
) {

    constructor(string: String) : this(*string.split('.').map(String::toInt).toIntArray())

    override fun toString(): String {
        return nums.joinToString(".")
    }

    fun compare(version: SemanticVersion): Result {
        val maxIndex = version.nums.size

        nums.forEachIndexed { index, number ->
            if (index == maxIndex) {
                return Result.GREATER
            }
            val other = version.nums[index]
            if (number != other) {
                return if (number > other) Result.GREATER else Result.LESS
            }
        }
        return if (maxIndex == nums.size) Result.EQUAL else Result.LESS
    }

    enum class Result {
        GREATER,
        EQUAL,
        LESS,
    }

}