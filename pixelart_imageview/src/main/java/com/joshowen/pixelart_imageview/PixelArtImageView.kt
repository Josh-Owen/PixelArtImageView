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
    private var canvasGridWidth = DEFAULT_CANVAS_GRID_WIDTH
    private var canvasGridHeight = DEFAULT_CANVAS_GRID_HEIGHT

    private var isImageHiddenByDefault = DEFAULT_IMAGE_HIDDEN_BY_DEFAULT
    private var isAutomaticPixelationEnabled = DEFAULT_AUTOMATICALLY_PIXELATE

    //endregion

    //region Threading
    private val pixelHandler = Handler(Looper.getMainLooper())
    private val pixelExecutor = Executors.newSingleThreadExecutor()
    //endregion

    //region Callbacks
    private var onStart: (() -> Unit)? = null
    private var onFinished: (() -> Unit)? = null
    private var callback : ImagePixelationListener? = null
    //endregion

    //endregion

    //region Constructor
    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PixelArtImageView, 0, 0).apply {

            canvasGridWidth =
                getInteger(R.styleable.PixelArtImageView_gridWidth, DEFAULT_CANVAS_GRID_WIDTH)

            canvasGridHeight =
                getInteger(R.styleable.PixelArtImageView_gridHeight, DEFAULT_CANVAS_GRID_HEIGHT)

            if (canvasGridWidth <= 0 || canvasGridHeight <= 0) {
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
                beginConversion()
            }
        }
    }
    //endregion

    //region Image Conversion
    private fun beginConversion() {

        if (drawable == null) {
            throw IllegalStateException("No image provided to PixelArtImageView.")
        }

        val currentImageBitmap = drawable.toBitmap()

        onStart?.let {
            it()
        }
        callback?.onStart()

        pixelExecutor.execute {

            val pixelatedImage = currentImageBitmap.convertToPixelatedBitmap()

            pixelHandler.post {
                setImageBitmap(pixelatedImage)
                visibility = View.VISIBLE

                onFinished?.let {
                    it()
                }
                callback?.onFinished()
            }
        }
    }

    private fun Bitmap.convertToPixelatedBitmap(): Bitmap {

        val blockSizeX = this.width / canvasGridWidth
        val blockSizeY = this.height / canvasGridHeight
        val blockPerimeter = blockSizeX * blockSizeY

        for (i in 0 until canvasGridWidth) {
            val startX = i * blockSizeX
            for (j in 0 until canvasGridHeight) {

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
                    startX,
                    startY,
                    blockSizeX,
                    blockSizeY
                )
            }
        }
        return this
    }
    //endregion

    //region Builder Functions

    fun load(resourceId: Int) : PixelArtImageView {
        visibility = if (isImageHiddenByDefault) View.INVISIBLE else View.VISIBLE
        this.setImageResource(resourceId)
        return this
    }

    fun setGridWidth(gridWidth : Int) : PixelArtImageView {
        this.canvasGridWidth = gridWidth
        return this
    }

    fun setGridHeight(gridHeight : Int) : PixelArtImageView  {
        this.canvasGridHeight = gridHeight
        return this
    }

    fun onStart(onStart: () -> Unit) : PixelArtImageView{
        this.onStart = onStart
        return this
    }

    fun onFinished(onFinished: () -> Unit) : PixelArtImageView {
        this.onFinished = onFinished
        return this
    }

    fun addPixelationListener(callback : ImagePixelationListener) : PixelArtImageView {
        this.callback = callback
        return this
    }

    fun setOriginalImageHiddenByDefault(isHidden : Boolean) : PixelArtImageView {
        this.isImageHiddenByDefault = isHidden
        return this
    }

    fun build() : PixelArtImageView {
        this.beginConversion()
        return this
    }

    //endregion
}
