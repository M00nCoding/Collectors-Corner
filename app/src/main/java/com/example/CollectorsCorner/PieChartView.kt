package com.example.CollectorsCorner

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


// No changes needed as this is used for the manual added PieChart for the users progress to display
class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var progressData: Map<String, Int> = emptyMap()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#BA55D3") // MediumOrchid color for the stroke
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE // Changed to white color
        textSize = 56f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Bold text
    }

    private val gradientColors = intArrayOf(
        Color.parseColor("#DDA0DD"), // Plum
        Color.parseColor("#BA55D3")  // MediumOrchid for a more purple look
    )
    private var gradient: SweepGradient? = null

    fun setProgressData(data: Map<String, Int>) {
        this.progressData = data
        invalidate() // Redraw the view with new data
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val centerX = w / 2f
        val centerY = h / 2f
        gradient = SweepGradient(centerX, centerY, gradientColors, null)
        paint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (progressData.isEmpty()) return

        var startAngle = 0f

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - 20

        // Draw the full circle outline
        canvas.drawCircle(centerX, centerY, radius, outlinePaint)

        for ((_, value) in progressData) {
            val sweepAngle = (value / 100f) * 360 // Convert percentage to sweep angle
            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                true,
                paint
            )

            // Draw the stroke for the segment
            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                true,
                outlinePaint
            )

            // Calculate the position for the percentage text
            val angle = startAngle + sweepAngle / 2
            val x = centerX + (radius / 2) * cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = centerY + (radius / 2) * sin(Math.toRadians(angle.toDouble())).toFloat()
            val percentage = value // Already calculated as percentage in AchievementsActivity
            canvas.drawText("$percentage%", x, y, textPaint)

            startAngle += sweepAngle
        }
    }
}