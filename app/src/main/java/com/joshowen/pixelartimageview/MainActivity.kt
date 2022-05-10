package com.joshowen.pixelartimageview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.joshowen.pixelart_imageview.PixelArtImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val pbLoading = findViewById<ProgressBar>(R.id.pbLoading)
        val btnPixelate = findViewById<Button>(R.id.btnPixelate)
        val ivPixelatedImage = findViewById<PixelArtImageView>(R.id.pixelArtImageView)

        btnPixelate.setOnClickListener {

            ivPixelatedImage
                .setGridHeight(88)
                .setGridWidth(88)
                .load(R.drawable.mountains)
                .onBegin {   pbLoading.visibility = View.VISIBLE }
                .onFinished {  pbLoading.visibility = View.GONE }
                .build()

        }
    }
}