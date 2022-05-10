package com.joshowen.pixelart_imageview

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import java.util.*
import java.util.concurrent.Executors


class PixelArtImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    //region Variables

    //region Image Properties
    private var canvasWidth = DEFAULT_CANVAS_WIDTH
    private var canvasHeight = DEFAULT_CANVAS_HEIGHT

    private var isImageHiddenByDefault = DEFAULT_IMAGE_HIDDEN_BY_DEFAULT
    private var isAutomaticPixelationEnabled = DEFAULT_AUTOMATICALLY_PIXELATE

    //endregion

    //region Threading
    private val pixelHandler = Handler(Looper.getMainLooper())
    private val pixelExecutor = Executors.newSingleThreadExecutor()
    //endregion

    //region Callbacks
    private var onFinished: (() -> Unit)? = null
    private var onStart: (() -> Unit)? = null
    //endregion

    //endregion

    //region Constructor
    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PixelArtImageView, 0, 0).apply {

            canvasWidth =
                getInteger(R.styleable.PixelArtImageView_canvasWidth, DEFAULT_CANVAS_WIDTH)

            canvasHeight =
                getInteger(R.styleable.PixelArtImageView_canvasHeight, DEFAULT_CANVAS_HEIGHT)

            if (canvasWidth <= 0 || canvasHeight <= 0) {
                throw IllegalStateException("canvasWidth or canvasHeight needs to be initialised with values greater than 0.")
            }

            isImageHiddenByDefault = getBoolean(
                R.styleable.PixelArtImageView_originalImageHiddenByDefault,
                DEFAULT_IMAGE_HIDDEN_BY_DEFAULT
            )

            isAutomaticPixelationEnabled = getBoolean(
                R.styleable.PixelArtImageView_enableAutomaticallyPixelate,
                DEFAULT_AUTOMATICALLY_PIXELATE
            )

            visibility = if (isImageHiddenByDefault) View.INVISIBLE else View.VISIBLE

            if (isAutomaticPixelationEnabled) {
                startConversion()
            }
        }
    }
    //endregion

    //region Conversions
    private fun startConversion() {

        if (drawable == null) {
            throw IllegalStateException("No image provided to PixelArtImageView.")
        }

        val currentImageBitmap = drawable.toBitmap()

        onStart?.let {
            it()
        }

        pixelExecutor.execute {

            val pixelatedImage = currentImageBitmap.convertToPixelatedBitmap()

            pixelHandler.post {
                setImageBitmap(pixelatedImage)
                visibility = View.VISIBLE

                onFinished?.let {
                    it()
                }
            }
        }
    }
    //endregion

    //region Builder Functions

    fun load(resourceId: Int) : PixelArtImageView {
        visibility = if (isImageHiddenByDefault) View.INVISIBLE else View.VISIBLE
        this.setImageResource(resourceId)
        return this
    }

    fun build() : PixelArtImageView {
        this.startConversion()
        return this
    }

    fun setGridWidth(gridWidth : Int) : PixelArtImageView {
        this.canvasWidth = gridWidth
        return this
    }

    fun setGridHeight(gridHeight : Int) : PixelArtImageView  {
        this.canvasHeight = gridHeight
        return this
    }

    fun onBegin(onStart: () -> Unit) : PixelArtImageView{
        this.onStart = onStart
        return this
    }

    fun onFinished(onFinished: () -> Unit) : PixelArtImageView {
        this.onFinished = onFinished
        return this
    }

    //endregion

    //region Bitmap Extensions
    private fun Bitmap.convertToPixelatedBitmap(): Bitmap {

        val blockSizeX = this.width / canvasWidth
        val blockSizeY = this.height / canvasHeight
        val blockPerimeter = blockSizeX * blockSizeY

        for (i in 0 until canvasWidth) {
            val startX = i * blockSizeX
            for (j in 0 until canvasHeight) {

                val startY = j * blockSizeY

                val blockOfPixels = IntArray(blockPerimeter)
                this.getPixels(blockOfPixels, 0, blockSizeX, startX, startY, blockSizeX, blockSizeY)

                val pixelUsageAndColourMap = blockOfPixels
                    .toList()
                    .groupingBy { it }
                    .eachCount()

                val blockColour = pixelUsageAndColourMap.maxByOrNull { it.value }?.key

                blockColour?.let {
                    Arrays.fill(blockOfPixels, it)
                }

                this.setPixels(
                    blockOfPixels,
                    0,
                    blockSizeX,
                    i * blockSizeX,
                    j * blockSizeY,
                    blockSizeX,
                    blockSizeY
                )
            }
        }
        return this
    }
    //endregion
}
