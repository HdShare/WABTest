// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.devtools.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

fun getGitInfo(): Pair<Int?, String?> {
    return try {
        val rootDir = rootProject.projectDir
        val gitDir = File(rootDir, ".git")
        if (!gitDir.exists()) return Pair(null, null)

        val countProcess = ProcessBuilder("git", "rev-list", "--count", "HEAD")
            .directory(rootDir)
            .start()
        val count = countProcess.inputStream.bufferedReader().readText().trim().toIntOrNull()
        countProcess.waitFor()

        val hashProcess = ProcessBuilder("git", "rev-parse", "--short=7", "HEAD")
            .directory(rootDir)
            .start()
        val hash = hashProcess.inputStream.bufferedReader().readText().trim().takeIf { it.isNotEmpty() }
        hashProcess.waitFor()

        if (count != null && hash != null) Pair(count, hash) else Pair(null, null)
    } catch (e: Exception) {
        logger.warn("GetGitInfo Failed: ${e.message}")
        Pair(null, null)
    }
}

fun getBuildVersionCode(fallback: Int = 1): Int {
    return getGitInfo().first ?: fallback
}

fun getGitHeadRefsSuffix(fallback: String = "standalone"): String {
    val (count, hash) = getGitInfo()
    return if (count != null && hash != null) "r$count.${hash.take(7)}" else fallback
}

extra["gitVersionCode"] = getBuildVersionCode()
extra["gitVersionSuffix"] = getGitHeadRefsSuffix()
