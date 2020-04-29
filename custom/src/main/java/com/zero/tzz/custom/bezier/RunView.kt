package com.zero.tzz.custom.bezier

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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

    private val mPaint = Paint()
    private val mPath = Path()
    private var mCenterRadius = 240f
    private var mRunballRadius = 20f

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
        canvas.drawCircle(0f, 0f, mCenterRadius, mPaint)
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
        canvas.drawCircle(mCurrentPos[0], mCurrentPos[1], mRunballRadius, mPaint)
    }

    private var mCurrentPos = FloatArray(2)
    private fun run() {
        mPath.addOval(rectF, Path.Direction.CW)
        val pathMeasure = PathMeasure(mPath, false)
        val anim = ValueAnimator.ofFloat(0f, pathMeasure.length)
        anim.addUpdateListener {
            val value = it.animatedValue as Float
            pathMeasure.getPosTan(value, mCurrentPos, null)
            if (!checkIntersect()) {
                invalidate()
            }
        }
        anim.interpolator = LinearInterpolator()
        anim.repeatMode = ValueAnimator.RESTART
        anim.repeatCount = ValueAnimator.INFINITE
        anim.duration = 3000
        anim.start()
    }

    private fun checkIntersect(): Boolean {
        val x = mCurrentPos[0]
        val y = mCurrentPos[1]
        val tempPosX = x / cos(angle) - (cos(angle.toDouble()) * rectF.height() / 2).toFloat()
        val tempPosY = y / sin(angle) - (sin(angle.toDouble()) * rectF.height() / 2).toFloat()
        val centerOfCircleDistance = sqrt(tempPosX.pow(2) + tempPosY.pow(2))
        Log.d(TAG, "checkIntersect: $x, $y, $centerOfCircleDistance")
        return centerOfCircleDistance in 0f..(mCenterRadius + mRunballRadius)
    }
}