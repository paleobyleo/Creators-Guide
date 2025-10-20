package com.leo.creators_guide.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class for managing app version checking and updates
 */
class VersionManager(private val context: Context) {
    
    companion object {
        private const val TAG = "VersionManager"
    }
    
    /**
     * Gets the current app version name
     */
    fun getCurrentVersionName(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Error getting current version", e)
            "1.0"
        }
    }
    
    /**
     * Gets the current app version code
     */
    fun getCurrentVersionCode(): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Error getting current version code", e)
            1
        }
    }
    
    /**
     * Checks for updates by comparing current version with latest GitHub release
     * Returns UpdateInfo if update is available, null if current version is latest
     */
    suspend fun checkForUpdates(): UpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                // Check if update checks are enabled
                if (!GitHubConfig.ENABLE_UPDATE_CHECKS) {
                    return@withContext null
                }
                
                val gitHubService = GitHubApiService()
                val latestRelease = gitHubService.getLatestRelease(GitHubConfig.OWNER, GitHubConfig.REPO)
                
                if (latestRelease != null) {
                    val latestVersion = latestRelease.version
                    val currentVersion = getCurrentVersionName()
                    
                    // Compare versions
                    if (compareVersions(latestVersion, currentVersion) > 0) {
                        // Find APK asset
                        val apkAsset = latestRelease.assets.find { 
                            it.name.endsWith(".apk", ignoreCase = true) && 
                            it.contentType == "application/vnd.android.package-archive" 
                        }
                        
                        return@withContext UpdateInfo(
                            latestVersionName = latestVersion,
                            latestVersionCode = getCurrentVersionCode() + 1, // Approximate
                            releaseNotes = latestRelease.body,
                            downloadUrl = latestRelease.htmlUrl,
                            apkUrl = apkAsset?.downloadUrl ?: latestRelease.htmlUrl
                        )
                    }
                }
                
                null
            } catch (e: Exception) {
                Log.e(TAG, "Error checking for updates", e)
                null
            }
        }
    }
    
    /**
     * Data class for update information
     */
    data class UpdateInfo(
        val latestVersionName: String,
        val latestVersionCode: Int,
        val releaseNotes: String,
        val downloadUrl: String,
        val apkUrl: String
    )
    
    /**
     * Compares two version strings
     * Returns positive if version1 > version2, negative if version1 < version2, 0 if equal
     */
    private fun compareVersions(version1: String, version2: String): Int {
        val v1Parts = version1.split(".").map { it.toIntOrNull() ?: 0 }
        val v2Parts = version2.split(".").map { it.toIntOrNull() ?: 0 }
        
        for (i in 0 until maxOf(v1Parts.size, v2Parts.size)) {
            val part1 = if (i < v1Parts.size) v1Parts[i] else 0
            val part2 = if (i < v2Parts.size) v2Parts[i] else 0
            
            val comparison = part1.compareTo(part2)
            if (comparison != 0) {
                return comparison
            }
        }
        
        return 0
    }
}