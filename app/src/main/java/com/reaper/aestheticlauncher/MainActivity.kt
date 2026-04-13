package com.reaper.aestheticlauncher

import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.Color
import android.os.Bundle
import android.os.UserManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var meshBg: MeshBackgroundView
    private lateinit var tabPersonal: TextView
    private lateinit var tabWork: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        
        setContentView(R.layout.activity_main)

        meshBg = findViewById(R.id.mesh_bg)
        viewPager = findViewById(R.id.view_pager)
        tabPersonal = findViewById(R.id.tab_personal)
        tabWork = findViewById(R.id.tab_work)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {} 
        })

        loadApps()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    tabPersonal.setTextColor(Color.WHITE)
                    tabWork.setTextColor(Color.parseColor("#66FFFFFF"))
                    meshBg.setWorkMode(false)
                } else {
                    tabWork.setTextColor(Color.WHITE)
                    tabPersonal.setTextColor(Color.parseColor("#66FFFFFF"))
                    meshBg.setWorkMode(true)
                }
            }
        })
        
        tabPersonal.setOnClickListener { viewPager.currentItem = 0 }
        tabWork.setOnClickListener { viewPager.currentItem = 1 }
        
        // Setup Micro-Dock Taps
        findViewById<android.widget.ImageView>(R.id.dock_phone).setOnClickListener {
            startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL))
        }
        findViewById<android.widget.ImageView>(R.id.dock_msg).setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_MAIN)
            intent.addCategory(android.content.Intent.CATEGORY_APP_MESSAGING)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            try { startActivity(intent) } catch (e: Exception) {}
        }
        findViewById<android.widget.ImageView>(R.id.dock_web).setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://google.com"))
            try { startActivity(intent) } catch (e: Exception) {}
        }
        findViewById<android.widget.ImageView>(R.id.dock_cam).setOnClickListener {
            try { startActivity(android.content.Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)) } catch (e: Exception) {}
        }
    }

    private fun loadApps() {
        val launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val userManager = getSystemService(Context.USER_SERVICE) as UserManager
        
        val profiles = userManager.userProfiles
        val personalProfile = android.os.Process.myUserHandle()
        
        // Natively identify the securely managed Android Work Profile
        val workProfile = profiles.firstOrNull { it != personalProfile }

        val personalAppsInfo = launcherApps.getActivityList(null, personalProfile)
            .filter { it.applicationInfo.packageName != packageName }
            .map { AppItem(it.label.toString(), it.applicationInfo.packageName, it.componentName, it.user) }
            .sortedBy { it.label }

        val workAppsInfo = if (workProfile != null) {
            launcherApps.getActivityList(null, workProfile)
                .map { AppItem(it.label.toString(), it.applicationInfo.packageName, it.componentName, it.user) }
                .sortedBy { it.label }
        } else {
            emptyList()
        }

        val adapter = ProfilePagerAdapter()
        viewPager.adapter = adapter
        adapter.submitData(personalAppsInfo, workAppsInfo)
    }
}
