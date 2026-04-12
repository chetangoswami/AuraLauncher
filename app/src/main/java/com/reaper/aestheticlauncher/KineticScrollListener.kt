package com.reaper.aestheticlauncher

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KineticScrollListener : RecyclerView.OnScrollListener() {
    
    private var lastCenteredIndex = -1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        applyPhysics(recyclerView)
    }

    fun applyPhysics(recyclerView: RecyclerView) {
        val centerY = recyclerView.height / 2f
        val maxDist = recyclerView.height * 0.65f
        
        var minAbsDist = Float.MAX_VALUE
        var currentCenteredIndex = -1

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val appNode = child.findViewById<TextView>(R.id.appName) ?: continue

            val childCenterY = child.top + (child.height / 2f)
            val distance = childCenterY - centerY
            val absDist = Math.abs(distance)

            // Tracking for Haptics
            if (absDist < minAbsDist) {
                minAbsDist = absDist
                currentCenteredIndex = recyclerView.getChildAdapterPosition(child)
            }

            var ratio = 1f - (absDist / maxDist)
            if (ratio < 0) ratio = 0f

            val scale = 0.5f + (ratio * 1.0f) // Scales up to 1.5x in center
            val angle = (distance / maxDist) * 75f // Tilt back significantly

            // 3D physics mapping
            appNode.scaleX = scale
            appNode.scaleY = scale
            appNode.rotationX = -angle // Bend cylinder backwards from user
            
            if (absDist < 120) {
                appNode.setTextColor(Color.WHITE)
                appNode.setShadowLayer(40f, 0f, 0f, Color.parseColor("#99FFFFFF"))
            } else {
                appNode.setTextColor(Color.parseColor("#44FFFFFF"))
                appNode.setShadowLayer(0f, 0f, 0f, 0)
            }
        }
        
        // Execute the physical vault dial vibration forcefully via system API
        if (currentCenteredIndex != -1 && currentCenteredIndex != lastCenteredIndex) {
            lastCenteredIndex = currentCenteredIndex
            try {
                val vibrator = recyclerView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } catch (e: Exception) {}
        }
    }
}
