# PixelArtImageView

[![](https://jitpack.io/v/Josh-Owen/PixelArtImageView.svg)](https://jitpack.io/#Josh-Owen/PixelArtImageView)

PixelArtImageView is a lightweight library used to convert regular images into their pixel art 
targeting Android API 21 and above. The view extends [ImageView](https://developer.android.com/reference/android/widget/ImageView) 
behaviour and can therefore use any of it's properties. 
The library also implements callbacks allowing you intercept essential events such as once an 
image has begun being converted and upon completion in addition to providing some alternative configuration options. 

![](Images/Image1.JPG)![](Images/Image2.JPG)

## Getting started

### Download

For detailed instructions please check out the
official [Jitpack documentation](https://jitpack.io/#Josh-Owen/PixelArtImageView/tag).

Additionally you can download the project from the
Github's [release's page](https://github.com/Josh-Owen/PixelArtImageView/releases).

Or use Gradle:

```
gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    implementation 'com.github.Josh-Owen:PixelArtImageView:1.0.0'
}
```

Or Maven:

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Josh-Owen</groupId>
    <artifactId>PixelArtImageView</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage

The library makes use of a builder pattern and a simple usage of this library in Kotlin with:

```
ivPixelatedImage
    .load(R.drawable.yourdrawable)
    .setGridHeight(gridHeight)
    .setGridWidth(gridWidth)
    .onStart {  // Show your progress bar here  }
    .onFinished {  // Hide your progress bar here }
    .build()
```

Or alternatively it can be initialised in XML:

```
<com.joshowen.pixelart_imageview.PixelArtImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:canvasWidth="88"
    app:canvasHeight="88"
    app:enableAutomaticallyPixelate="true"
    app:srcCompat="@drawable/yourdrawable" />
```

Note: The XML approach will not show a progress bar and by default the image will only show once it 
has finished being pixelated. 

If you would like the original image to be displayed temporarily before being converted to a 
pixelated image the following properties can be applied:

In Kotlin in your builder:

``` 
.setOriginalImageHiddenByDefault(false)
```

Or in XML:

```
app:originalImageHiddenByDefault="false"
```



Note: There are code samples demonstrating the usage of all of which in the demo project.

And that is all folks! I am open to suggestions in how to further improve this library to increase
it's utility.

Be kind & help others. Cya!