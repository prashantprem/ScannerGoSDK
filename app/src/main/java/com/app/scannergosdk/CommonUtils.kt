package com.app.scannergosdk

object CommonUtils {
    fun getScaledDimension(imgSize: DimensionUtil, boundary: DimensionUtil): DimensionUtil {
        val original_width: Int = imgSize.getWidth()
        val original_height: Int = imgSize.getHeight()
        val bound_width: Int = boundary.getWidth()
        val bound_height: Int = boundary.getHeight()
        var new_width = original_width
        var new_height = original_height
        if (original_width > bound_width) {
            new_width = bound_width
            new_height = new_width * original_height / original_width
        }
        if (new_height > bound_height) {
            new_height = bound_height
            new_width = new_height * original_width / original_height
        }
        return DimensionUtil(new_width, new_height)
    }

}