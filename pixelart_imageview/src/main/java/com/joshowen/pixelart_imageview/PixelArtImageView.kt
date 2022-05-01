package com.joshowen.pixelart_imageview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class PixelArtImageView(context: Context, attrs: AttributeSet?) :
    AppCompatImageView(context, attrs) {

    private var canvasWidth = 0
    private var canvasHeight = 0

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PixelArtImageView, 0, 0).apply {

            canvasWidth =
                getInteger(R.styleable.PixelArtImageView_canvasWidth, DEFAULT_CANVAS_WIDTH)

            canvasHeight =
                getInteger(R.styleable.PixelArtImageView_canvasHeight, DEFAULT_CANVAS_HEIGHT)

            val imageResourceId = getResourceId(R.styleable.PixelArtImageView_originalImage, -1)

            if(imageResourceId == -1) {
                throw IllegalStateException("An image needs to be provided to the PixelArtImageView.")
            }
            
        }
    }
}