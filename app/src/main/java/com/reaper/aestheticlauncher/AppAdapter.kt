package com.reaper.aestheticlauncher

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Random

class AppAdapter(private val appsList: List<AppModel>) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appNameText: TextView = itemView.findViewById(R.id.appNameText)
        val appPermText: TextView = itemView.findViewById(R.id.appPermText)
        val appSizeText: TextView = itemView.findViewById(R.id.appSizeText)
        val appDateText: TextView = itemView.findViewById(R.id.appDateText)
        val appLinksText: TextView = itemView.findViewById(R.id.appLinksText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appsList[position]
        
        // Make names lower case and remove spaces
        val cliName = app.name.lowercase().replace(" ", "")
        
        // Deterministic pseudo-random generation based on app name hash
        val rnd = Random(app.packageName.hashCode().toLong())
        val isSystem = app.packageName.startsWith("com.android") || app.packageName.startsWith("com.google")
        
        if (isSystem) {
            holder.appPermText.text = "drwxr-xr-x"
            holder.appLinksText.text = (rnd.nextInt(4) + 2).toString()
            holder.appNameText.text = "$cliName/"
        } else {
            holder.appPermText.text = "-rwxr-xr-x"
            holder.appLinksText.text = "1"
            holder.appNameText.text = cliName
        }
        
        val sizes = intArrayOf(1024, 2048, 4096, 8192, 16384, 512, 2451)
        holder.appSizeText.text = sizes[rnd.nextInt(sizes.size)].toString()
        
        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val month = months[rnd.nextInt(12)]
        val day = rnd.nextInt(28) + 1
        val hour = rnd.nextInt(24)
        val min = rnd.nextInt(60)
        holder.appDateText.text = String.format("%s %02d %02d:%02d", month, day, hour, min)

        holder.itemView.setOnClickListener {
            val launchIntent = holder.itemView.context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                holder.itemView.context.startActivity(launchIntent)
            }
        }
        
        // Long click to show app settings
        holder.itemView.setOnLongClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${app.packageName}")
            holder.itemView.context.startActivity(intent)
            true
        }
    }

    override fun getItemCount(): Int {
        return appsList.size
    }
}
