package com.zero.tzz.custom.flowlayout.core

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-29 18:17
 * @description FlowLayout
 */
class FlowLayout : ViewGroup {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mLineViews = mutableListOf<MutableList<View>>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (widthMode == MeasureSpec.AT_MOST) throw RuntimeException("流式布局不允许设置layout_width为wrap_content")
        var height = paddingTop + paddingBottom
        var lineWidth = 0
        var lineHeight = 0

        val count = childCount

        for (i in 0 until count) {
            val child = getChildAt(i)
            // 测量child
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
            // 考虑padding
            var lineView = mutableListOf<View>()
            if (childWidth + lineWidth <= widthSize - paddingLeft - paddingRight) {
                // 正常显示，计算这一行最大的高度
                lineHeight = max(childHeight, lineHeight)
                lineWidth += childWidth
                lineView.add(child)
            } else {
                // 超出父布局
                height += lineHeight
                lineWidth = childWidth
                mLineViews.add(lineView)
                lineView = mutableListOf()
                lineView.add(child)
            }
            mLineViews.add(lineView)
        }
        setMeasuredDimension(
            widthSize,
            if (heightMode == MeasureSpec.UNSPECIFIED) height else heightSize
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val size = mLineViews.size
        val pl = paddingLeft
        val pt = paddingTop
//        for (i in 0 until size) {
//            val child = mLineViews[i]
//
//            val lc = pl + child.marginLeft
//            val tc = pt + child.marginTop
//            val rc = lc + child.measuredWidth
//            val bc = tc + child.measuredHeight
//
//            child.layout(lc, tc, rc, bc)
//
//        }
    }
}