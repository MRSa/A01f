package net.osdn.gokigen.objectdetection.a01f

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.ImageFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*


class MyScreenCaptureManager(private val activity: MainActivity)
{
    private lateinit var imageReader: ImageReader
    private lateinit var displayMetrics : DisplayMetrics
    private lateinit var virtualDisplay: VirtualDisplay

    fun prepare(mediaProjection : MediaProjection)
    {
        try
        {
            Log.v(TAG, "prepare(mediaProjection) ")
            displayMetrics = activity.resources.displayMetrics
            imageReader = ImageReader.newInstance(displayMetrics.widthPixels, displayMetrics.heightPixels, ImageFormat.RGB_565, 2);
            virtualDisplay = mediaProjection.createVirtualDisplay("ScreenShot", displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,imageReader.surface, null, null)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun captureScreenshot(): Bitmap?
    {
        try
        {
            val image: Image = imageReader.acquireLatestImage()
            val planes: Array<Image.Plane> = image.planes
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride: Int = planes[0].pixelStride
            val rowStride: Int = planes[0].rowStride
            val rowPadding: Int = rowStride - pixelStride * displayMetrics.widthPixels

            val bitmap = Bitmap.createBitmap(displayMetrics.widthPixels + rowPadding / pixelStride, displayMetrics.heightPixels, Bitmap.Config.RGB_565)
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()
            return (bitmap)
        }
        catch (t: Throwable)
        {
            t.printStackTrace()
        }
        return (null)
    }

    private fun prepareLocalOutputDirectory(): File
    {
        val mediaDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        mediaDir?.mkdirs()
        return (if (mediaDir != null && mediaDir.exists()) mediaDir else activity.filesDir)
    }

    fun takeScreenShot()
    {
       try
        {
            val bitmap = captureScreenshot()
            if (bitmap != null)
            {
                val photoFile = File(prepareLocalOutputDirectory(), "L" + SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".png")
                val out = FileOutputStream(photoFile)
                bitmap.compress(CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    fun release()
    {
        try
        {
            Log.v(TAG,"release VirtualDisplay")
            virtualDisplay.release()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = MyScreenCaptureManager::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

}