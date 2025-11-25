package com.kotla.anifloat.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.kotla.anifloat.BuildConfig
import com.kotla.anifloat.data.api.GitHubApi
import com.kotla.anifloat.data.model.GitHubRelease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val latestVersion: String,
    val currentVersion: String,
    val releaseNotes: String?,
    val downloadUrl: String?,
    val releasePageUrl: String
)

class UpdateRepository(
    private val githubApi: GitHubApi,
    private val context: Context
) {
    companion object {
        private const val GITHUB_OWNER = "kotlakiran"
        private const val GITHUB_REPO = "AniFloat"
    }

    suspend fun checkForUpdate(): UpdateInfo? {
        return try {
            val release = githubApi.getLatestRelease(GITHUB_OWNER, GITHUB_REPO)
            
            // Skip pre-releases and drafts
            if (release.prerelease || release.draft) {
                return null
            }
            
            val latestVersion = release.tagName.removePrefix("v")
            val currentVersion = BuildConfig.VERSION_NAME
            
            val isUpdateAvailable = isNewerVersion(latestVersion, currentVersion)
            
            // Find APK asset
            val apkAsset = release.assets.firstOrNull { 
                it.name.endsWith(".apk") 
            }
            
            UpdateInfo(
                isUpdateAvailable = isUpdateAvailable,
                latestVersion = latestVersion,
                currentVersion = currentVersion,
                releaseNotes = release.body,
                downloadUrl = apkAsset?.browserDownloadUrl,
                releasePageUrl = release.htmlUrl
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
        
        val maxLength = maxOf(latestParts.size, currentParts.size)
        
        for (i in 0 until maxLength) {
            val latestPart = latestParts.getOrElse(i) { 0 }
            val currentPart = currentParts.getOrElse(i) { 0 }
            
            when {
                latestPart > currentPart -> return true
                latestPart < currentPart -> return false
            }
        }
        
        return false
    }

    suspend fun downloadAndInstallApk(downloadUrl: String, onProgress: (Int) -> Unit): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(downloadUrl).build()
                val response = client.newCall(request).execute()
                
                if (!response.isSuccessful) return@withContext false
                
                val body = response.body ?: return@withContext false
                val contentLength = body.contentLength()
                
                val downloadDir = context.getExternalFilesDir(null) ?: context.filesDir
                val apkFile = File(downloadDir, "AniFloat-update.apk")
                
                // Delete old file if exists
                if (apkFile.exists()) {
                    apkFile.delete()
                }
                
                body.byteStream().use { inputStream ->
                    apkFile.outputStream().use { outputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead = 0L
                        
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            
                            if (contentLength > 0) {
                                val progress = ((totalBytesRead * 100) / contentLength).toInt()
                                withContext(Dispatchers.Main) {
                                    onProgress(progress)
                                }
                            }
                        }
                    }
                }
                
                // Install APK
                withContext(Dispatchers.Main) {
                    installApk(apkFile)
                }
                
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun installApk(apkFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        
        context.startActivity(intent)
    }

    fun openReleasePage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
