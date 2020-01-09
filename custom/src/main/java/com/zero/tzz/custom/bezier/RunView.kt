package com.zero.tzz.custom.bezier

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin

/**
 *
 * @author Zero_Tzz
 * @date 2020-01-09 10:25
 * @description BeizerLoading
 */
class RunView : View {
    private val TAG = "RunView"

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val mPaint = Paint()
    private val mPath = Path()

    init {
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 3f
        mPaint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        run()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            mPaint.color = Color.BLACK
            it.drawLine(0f, height / 2f, width.toFloat(), height / 2f, mPaint)
            it.drawLine(width / 2f, 0f, width / 2f, height.toFloat(), mPaint)
            it.translate(width / 2f, height / 2f)

            drawCenter(it)
            drawBall(it)
        }
    }

    private fun drawCenter(canvas: Canvas) {
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, 240f, mPaint)
    }

    val angle = 30f
    val rectF = RectF(-400f, -200f, 400f, 200f)
    private fun drawBall(canvas: Canvas) {
        canvas.rotate(angle)
        canvas.translate(
            (cos(angle.toDouble()) * rectF.height() / 2).toFloat(),
            (sin(angle.toDouble()) * rectF.height() / 2).toFloat()
        )

        mPaint.color = Color.RED
        mPaint.style = Paint.Style.STROKE
        canvas.drawPath(mPath, mPaint)

        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(mCurrentPos[0], mCurrentPos[1], 20f, mPaint)
    }

    private var mCurrentPos = FloatArray(2)
    private fun run() {
        mPath.addOval(rectF, Path.Direction.CW)
        val pathMeasure = PathMeasure(mPath, false)
        val anim = ValueAnimator.ofFloat(0f, pathMeasure.length)
        anim.addUpdateListener {
            val value = it.animatedValue as Float
            Log.d(TAG, "run: $value")
            pathMeasure.getPosTan(value, mCurrentPos, null)
            invalidate()
        }
        anim.interpolator = LinearInterpolator()
        anim.repeatMode = ValueAnimator.RESTART
        anim.repeatCount = ValueAnimator.INFINITE
        anim.duration = 5000
        anim.start()
    }
}