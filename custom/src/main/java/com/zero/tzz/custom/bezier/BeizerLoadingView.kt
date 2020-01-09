package com.zero.tzz.custom.bezier

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 *
 * @author Zero_Tzz
 * @date 2020-01-09 10:25
 * @description BeizerLoading
 */
class BeizerLoadingView : View {
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

    companion object {
        private const val STU = 0.552284749831f
    }

    private val mPaint = Paint()
    private val mPath = Path()
    private var mCenterRaius = 360f

    init {
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 3f
        mPaint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterRaius = width * 0.4f
    }

    private var index = 0
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            mPaint.color = Color.BLACK
            it.drawLine(0f, height / 2f, width.toFloat(), height / 2f, mPaint)
            it.drawLine(width / 2f, 0f, width / 2f, height.toFloat(), mPaint)
            it.translate(width / 2f, height / 2f)
            when (index) {
                0 -> drawCenterCircle(it)
                1 -> drawCenterCircle2(it)
                2 -> drawCenterCircle3(it)
                3 -> drawCenterCircle4(it)
                else -> index = -1
            }
            index++
        }
        postInvalidateDelayed(200)
    }

    private fun drawCenterCircle(canvas: Canvas) {
        mPath.reset()
        mPaint.color = Color.parseColor("#FF69B4")
        mPaint.style = Paint.Style.FILL
        mPath.moveTo(0f, -mCenterRaius)
        val controlRadius = mCenterRaius * STU
        mPath.cubicTo(
            controlRadius,
            -mCenterRaius,
            mCenterRaius,
            -(mCenterRaius - controlRadius),
            mCenterRaius,
            0f
        )
        mPath.lineTo(0f, 0f)
        mPath.close()
        canvas.drawPath(mPath, mPaint)
    }

    private fun drawCenterCircle2(canvas: Canvas) {
        mPath.reset()
        mPaint.color = Color.parseColor("#FF69B4")
        mPaint.style = Paint.Style.FILL
        mPath.moveTo(0f, -mCenterRaius)
        val controlRadius = mCenterRaius * STU
        mPath.cubicTo(
            mCenterRaius * 0.8f,
            -mCenterRaius,
            mCenterRaius,
            -mCenterRaius * 0.8f,
            mCenterRaius,
            0f
        )
        mPath.lineTo(0f, 0f)
        mPath.close()
        canvas.drawPath(mPath, mPaint)
    }

    private fun drawCenterCircle3(canvas: Canvas) {
        mPath.reset()
        mPaint.color = Color.parseColor("#FF69B4")
        mPaint.style = Paint.Style.FILL
        mPath.moveTo(0f, -mCenterRaius)
        val controlRadius = mCenterRaius * STU
        mPath.cubicTo(
            0f,
            -controlRadius,
            controlRadius,
            0f,
            mCenterRaius,
            0f
        )
        mPath.lineTo(0f, 0f)
        mPath.close()
        canvas.drawPath(mPath, mPaint)
    }

    private fun drawCenterCircle4(canvas: Canvas) {
        mPath.reset()
        mPaint.color = Color.parseColor("#FF69B4")
        mPaint.style = Paint.Style.FILL
        mPath.moveTo(0f, -mCenterRaius)
        val controlRadius = mCenterRaius * STU
        mPath.cubicTo(
            0f,
            -controlRadius,
            mCenterRaius,
            -controlRadius,
            mCenterRaius,
            0f
        )
        mPath.lineTo(0f, 0f)
        mPath.close()
        canvas.drawPath(mPath, mPaint)
    }
}