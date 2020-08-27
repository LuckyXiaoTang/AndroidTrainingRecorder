package com.topvision.media

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView

/**
 *
 * @author Zero_Tzz
 * @date 2020-05-12 09:04
 * @description CameraView
 */
class CameraView : SurfaceView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    init {


    }
}