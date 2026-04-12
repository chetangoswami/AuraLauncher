package com.reaper.aestheticlauncher

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class MeshBackgroundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint1 = Paint()
    private val paint2 = Paint()
    private val paint3 = Paint()

    private var time = 0f
    private var isWorkMode = false
    
    // Efficiently animate the floating positions over 15 seconds
    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 15000 
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener {
            time = it.animatedFraction
            invalidate() // Only triggers redraw natively; uses hardware acceleration!
        }
    }

    init {
        animator.start()
    }

    fun setWorkMode(active: Boolean) {
        if (isWorkMode != active) {
            isWorkMode = active
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        if (w == 0f || h == 0f) return

        // Draw deep absolute black canvas first to prevent ghosting
        canvas.drawColor(Color.parseColor("#050505"))

        // Assign Personal vs Work Profile Aura colors (simulates the CSS filter shift)
        val c1 = if (isWorkMode) Color.parseColor("#44e65100") else Color.parseColor("#444f46e5")
        val c2 = if (isWorkMode) Color.parseColor("#55b71c1c") else Color.parseColor("#55e11d48")
        val c3 = if (isWorkMode) Color.parseColor("#44fbc02d") else Color.parseColor("#440ea5e9")

        // Orb 1 logic
        val cx1 = w * 0.1f + (time * w * 0.2f)
        val cy1 = h * 0.1f + (time * h * 0.1f)
        paint1.shader = RadialGradient(cx1, cy1, w * 0.8f, c1, Color.TRANSPARENT, Shader.TileMode.CLAMP)

        // Orb 2 logic (drift opposite)
        val cx2 = w * 0.8f - (time * w * 0.15f)
        val cy2 = h * 0.8f - (time * h * 0.2f)
        paint2.shader = RadialGradient(cx2, cy2, w * 0.9f, c2, Color.TRANSPARENT, Shader.TileMode.CLAMP)

        // Orb 3 logic
        val cx3 = w * 0.5f + (time * w * 0.3f)
        val cy3 = h * 0.5f - (time * h * 0.15f)
        paint3.shader = RadialGradient(cx3, cy3, w * 0.7f, c3, Color.TRANSPARENT, Shader.TileMode.CLAMP)

        // Simply layering large soft gradients provides a perfect CSS blur/mesh emulation 
        // with practically zero computational overhead compared to real-time RenderScript blurs.
        canvas.drawRect(0f, 0f, w, h, paint1)
        canvas.drawRect(0f, 0f, w, h, paint2)
        canvas.drawRect(0f, 0f, w, h, paint3)
    }
}
