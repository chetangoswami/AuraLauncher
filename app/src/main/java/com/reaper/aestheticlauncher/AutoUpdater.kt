package com.reaper.aestheticlauncher

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object AutoUpdater {

    fun checkForUpdates(context: Context, onUpdateAvailable: (String) -> Unit) {
        thread {
            try {
                val url = URL("https://api.github.com/repos/chetangoswami/AuraLauncher/releases/latest")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    val tagName = json.getString("tag_name").removePrefix("v")
                    
                    val pkgInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val currentVersion = pkgInfo.versionName?.removePrefix("v") ?: ""
                    
                    if (tagName != currentVersion) {
                        val assets = json.getJSONArray("assets")
                        var apkUrl: String? = null
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            if (asset.getString("name").endsWith(".apk")) {
                                apkUrl = asset.getString("browser_download_url")
                                break
                            }
                        }
                        
                        if (apkUrl != null) {
                            Handler(Looper.getMainLooper()).post {
                                onUpdateAvailable(apkUrl)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AutoUpdater", "Error checking for updates", e)
            }
        }
    }

    fun startUpdateDownload(context: Context, apkUrl: String) {
        try {
            val oldFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AuraLauncher_Update.apk")
            if (oldFile.exists()) {
                oldFile.delete()
            }
        } catch (e: Exception) {
            Log.e("AutoUpdater", "Failed to delete old apk", e)
        }

        Toast.makeText(context, "Update downloading in background...", Toast.LENGTH_SHORT).show()
        
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Aura Launcher Update")
            .setDescription("Downloading latest update...")
            .setMimeType("application/vnd.android.package-archive")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "AuraLauncher_Update.apk")

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (statusIndex >= 0 && cursor.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                            try {
                                val localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                                val downloadedFile = if (localUriIndex >= 0 && cursor.getString(localUriIndex) != null) {
                                    val localUri = Uri.parse(cursor.getString(localUriIndex))
                                    File(localUri.path ?: "")
                                } else {
                                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AuraLauncher_Update.apk")
                                }
                                
                                val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", downloadedFile)

                                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(contentUri, "application/vnd.android.package-archive")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                context.startActivity(installIntent)
                            } catch(e: Exception) {
                                Log.e("AutoUpdater", "Failed to launch installer intent", e)
                                Toast.makeText(context, "Update failed to install.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    cursor.close()
                    context.unregisterReceiver(this)
                }
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }
}
