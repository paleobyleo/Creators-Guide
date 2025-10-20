package com.leo.creators_guide.utils

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson

/**
 * Service for interacting with GitHub API to check for releases
 */
class GitHubApiService {
    
    companion object {
        private const val TAG = "GitHubApiService"
        private const val GITHUB_API_BASE_URL = "https://api.github.com"
    }
    
    /**
     * Checks for the latest release of a GitHub repository
     */
    suspend fun getLatestRelease(owner: String, repo: String): GitHubRelease? {
        return withContext(Dispatchers.IO) {
            val url = "$GITHUB_API_BASE_URL/repos/$owner/$repo/releases/latest"
            
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val gson = Gson()
                    gson.fromJson(response, GitHubRelease::class.java)
                } else {
                    null
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error fetching latest release", e)
                null
            }
        }
    }
    
    /**
     * Data class representing a GitHub release
     */
    data class GitHubRelease(
        @SerializedName("tag_name")
        val tagName: String,
        
        @SerializedName("name")
        val name: String,
        
        @SerializedName("body")
        val body: String,
        
        @SerializedName("html_url")
        val htmlUrl: String,
        
        @SerializedName("assets")
        val assets: List<Asset> = emptyList()
    ) {
        val version: String
            get() = tagName.removePrefix("v")
    }
    
    /**
     * Data class representing a release asset
     */
    data class Asset(
        @SerializedName("name")
        val name: String,
        
        @SerializedName("browser_download_url")
        val downloadUrl: String,
        
        @SerializedName("content_type")
        val contentType: String,
        
        @SerializedName("size")
        val size: Long
    )
}