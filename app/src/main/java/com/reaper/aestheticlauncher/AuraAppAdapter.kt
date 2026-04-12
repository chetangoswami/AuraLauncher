package com.reaper.aestheticlauncher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.graphics.Color
import android.net.Uri
import android.os.UserHandle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// New data class to contain component and user profile data accurately
data class AppItem(
    val label: String,
    val packageName: String,
    val componentName: ComponentName,
    val userHandle: UserHandle
)

class AuraAppAdapter(private val appsList: List<AppItem>, private val isWorkProfile: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_SPACER = 0
    private val TYPE_APP = 1

    override fun getItemViewType(position: Int): Int {
        if (position == 0 || position == appsList.size + 1) return TYPE_SPACER
        return TYPE_APP
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_SPACER) {
            val view = View(parent.context)
            return object : RecyclerView.ViewHolder(view) {}
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aura_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_SPACER) {
            val vh = holder.itemView.context.resources.displayMetrics.heightPixels
            holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (vh / 2) - 150)
            return
        }

        val app = appsList[position - 1]
        val vh = holder as AppViewHolder
        
        var name = app.label.uppercase()
        if (isWorkProfile) name += " 💼"
        
        vh.appNameText.text = name

        vh.itemView.setOnClickListener {
            // Must use LauncherApps to securely launch an activity in a specific user profile
            val launcherApps = it.context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
        }
        
        vh.itemView.setOnLongClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${app.packageName}")
            it.context.startActivity(intent)
            true
        }
    }

    override fun getItemCount(): Int {
        return if (appsList.isEmpty()) 0 else appsList.size + 2
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appNameText: TextView = itemView.findViewById(R.id.appName)
    }
}
