package com.joshowen.pixelart_imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
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

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        drawable?.let {
            pixelateImage(it.toBitmap())
        }
    }

    private fun pixelateImage(image: Bitmap = drawable.toBitmap()) {

        val output = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)

        val blockSizeX = measuredWidth / canvasWidth
        val blockSizeY = measuredHeight / canvasHeight


        for (i in 0 until canvasWidth) {
            for (j in 0 until canvasHeight) {
                //    if(j % 2 == 1)
                output.setPixels(
                    image.getBlock(i, j),
                    0,
                    blockSizeX,
                    i * blockSizeX,
                    j * blockSizeY,
                    blockSizeX - 1,
                    blockSizeY - 1
                )
            }
        }
        setImageBitmap(output)
    }


    private fun Bitmap.getBlock(row: Int, column: Int): IntArray {

        val blockSizeX = this.width / canvasWidth
        val blockSizeY = this.height / canvasHeight
        val blockPerimeter = blockSizeX * blockSizeY
        val startX = row * blockSizeX
        val startY = column * blockSizeY


        val output = IntArray(blockPerimeter)

//        val num: Int = Random().nextInt(2)
//        if(num == 0) {
//            Arrays.fill(output, Color.BLUE)
//        }
//        else {
//            Arrays.fill(output, Color.GREEN)
//        }
        this.getPixels(output, 0, blockSizeX, startX, startY, blockSizeX, blockSizeY)

        val pixelUsageAndColourMap = output
            .toList()
            .groupingBy { it }
            .eachCount()

        val blockColour = pixelUsageAndColourMap.maxByOrNull { it.value }?.key
        
        blockColour?.let {
            Arrays.fill(output, Color.valueOf(it).toArgb())
        }
        return output
    }
}
