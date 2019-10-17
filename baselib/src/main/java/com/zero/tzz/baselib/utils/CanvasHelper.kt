package com.zero.tzz.baselib.utils

import android.content.res.Resources
import android.graphics.*

/**
 *
 * @author Zero_Tzz
 * @date 2019-08-06 15:36
 * @description CanvasHelper 绘制网格
 */
object CanvasHelper {
    private const val SPACE = 50
    private const val ARROW_WIDTH = 40
    private const val ARROW_HEIGHT = 20
    private const val TEXT_SPACE_X = 30
    private const val TEXT_SPACE_Y = 20


    private val mPoint = getWindowSize()
    private val mPaint = Paint()

    private fun getWindowSize(): PointF {
        val point = PointF()
        point.x = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        point.y = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        return point
    }

    /**
     * 画坐标系
     * @param canvas 画布
     * @param coo 坐标系原点位置
     */
    fun drawCoo(canvas: Canvas?, coo: PointF) {
        canvas?.let {
            drawGridLine(it)
            drawCooLine(canvas, coo)
            drawArrowLine(canvas, coo)
            drawCooText(canvas, coo)
        }
    }

    /**
     * 画虚线网格线
     * @param canvas 画布
     */
    private fun drawGridLine(canvas: Canvas?) {
        canvas?.let {
            mPaint.color = Color.GRAY
            mPaint.isAntiAlias = true
            mPaint.strokeWidth = 2f
            mPaint.style = Paint.Style.STROKE
            //设置虚线效果floatArrayOf(可见长度, 不可见长度),偏移值
            mPaint.pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            it.drawPath(getGridPath(SPACE), mPaint)
        }
    }

    /**
     * 画坐标系线
     * @param canvas 画布
     * @param coo 坐标系原点位置
     */
    private fun drawCooLine(canvas: Canvas?, coo: PointF) {
        canvas?.let {
            mPaint.color = Color.BLACK
            mPaint.isAntiAlias = true
            mPaint.strokeWidth = 6f
            mPaint.style = Paint.Style.STROKE
            mPaint.pathEffect = null
            it.drawPath(getCooPath(coo), mPaint)
        }
    }

    /**
     * 画箭头线
     * @param canvas 画布
     * @param coo 坐标系原点位置
     */
    private fun drawArrowLine(canvas: Canvas?, coo: PointF) {
        canvas?.let {
            mPaint.color = Color.BLACK
            mPaint.isAntiAlias = true
            mPaint.strokeWidth = 6f
            mPaint.style = Paint.Style.STROKE
            mPaint.pathEffect = null
            it.drawPath(getArrowPath(coo), mPaint)
        }
    }

    private fun drawCooText(canvas: Canvas?, coo: PointF) {
        canvas?.let {
            mPaint.color = Color.BLACK
            mPaint.isAntiAlias = true
            mPaint.strokeWidth = 6f
            mPaint.textSize = 32f
            mPaint.style = Paint.Style.FILL
            mPaint.pathEffect = null

            mPaint.textSize = 50f
            // X Y
            it.drawText("x", mPoint.x - 60, coo.y - 30, mPaint)
            it.drawText("y", coo.x - 60, mPoint.y - 30, mPaint)
            mPaint.textSize = 32f
            // x正轴
            for (i in 1 until ((mPoint.x - coo.x).toInt() / (SPACE * 2)) + 1) {
                it.drawText(
                    (i * SPACE * 2).toString(),
                    coo.x + (i * SPACE * 2) - TEXT_SPACE_X,
                    coo.y + TEXT_SPACE_Y * 2,
                    mPaint
                )
                it.drawLine(
                    coo.x + (i * SPACE * 2),
                    coo.y,
                    coo.x + (i * SPACE * 2),
                    coo.y - TEXT_SPACE_Y,
                    mPaint
                )
            }
            // x负轴
            for (i in 1 until coo.x.toInt() / (SPACE * 2) + 1) {
                it.drawText(
                    "-" + (i * SPACE * 2).toString(),
                    coo.x - (i * SPACE * 2) - TEXT_SPACE_X,
                    coo.y + TEXT_SPACE_Y * 2,
                    mPaint
                )
                it.drawLine(
                    coo.x - (i * SPACE * 2),
                    coo.y,
                    coo.x - (i * SPACE * 2),
                    coo.y - TEXT_SPACE_Y,
                    mPaint
                )
            }

            // y正轴
            for (i in 1 until ((mPoint.y - coo.y).toInt() / (SPACE * 2)) + 1) {
                it.drawText(
                    (i * SPACE * 2).toString(),
                    coo.x + TEXT_SPACE_X,
                    coo.y + (i * SPACE * 2) + 8,
                    mPaint
                )
                it.drawLine(
                    coo.x,
                    coo.y + (i * SPACE * 2),
                    coo.x + TEXT_SPACE_Y,
                    coo.y + (i * SPACE * 2),
                    mPaint
                )
            }

            // y负轴
            for (i in 1 until coo.y.toInt() / (SPACE * 2) + 1) {
                it.drawText(
                    "-" + (i * SPACE * 2).toString(),
                    coo.x + TEXT_SPACE_X,
                    coo.y - (i * SPACE * 2) + 8,
                    mPaint
                )
                it.drawLine(
                    coo.x,
                    coo.y - (i * SPACE * 2),
                    coo.x + TEXT_SPACE_Y,
                    coo.y - (i * SPACE * 2),
                    mPaint
                )
            }
        }
    }


    /**
     * @param space 线条之间的距离
     */
    private fun getGridPath(space: Int): Path {
        val path = Path()
        // X轴方向
        for (i in 0 until mPoint.x.toInt() / space + 1) {
            path.moveTo(i * space.toFloat(), 0f)
            path.lineTo(i * space.toFloat(), mPoint.y)
        }
        // Y轴方向
        for (i in 0 until mPoint.y.toInt() / space + 1) {
            path.moveTo(0f, i * space.toFloat())
            path.lineTo(mPoint.x, i * space.toFloat())
        }
        return path
    }

    private fun getCooPath(coo: PointF): Path {
        val path = Path()
        path.moveTo(0f, coo.y)
        path.lineTo(mPoint.x, coo.y)

        path.moveTo(coo.x, 0f)
        path.lineTo(coo.x, mPoint.y)
        return path
    }

    private fun getArrowPath(coo: PointF): Path {
        val path = Path()
        // x轴
        path.moveTo(mPoint.x, coo.y)
        path.lineTo(mPoint.x - ARROW_WIDTH, coo.y - ARROW_HEIGHT)
        path.moveTo(mPoint.x, coo.y)
        path.lineTo(mPoint.x - ARROW_WIDTH, coo.y + ARROW_HEIGHT)

        // y轴
        path.moveTo(coo.x, mPoint.y)
        path.lineTo(coo.x - ARROW_HEIGHT, mPoint.y - ARROW_WIDTH)
        path.moveTo(coo.x, mPoint.y)
        path.lineTo(coo.x + ARROW_HEIGHT, mPoint.y - ARROW_WIDTH)
        return path
    }
}