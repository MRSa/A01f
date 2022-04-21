package net.osdn.gokigen.objectdetection.a01f.tflite

import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_PT
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import net.osdn.gokigen.objectdetection.a01f.R
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs

class ObjectDetectionModelReader(private val activity: AppCompatActivity, private val max_detect_objects: Int = 10, private val confidence_level: Float = 0.5f) : IAnotherDrawer, ILiveViewRefresher
{
    private val contentResolver = activity.contentResolver
    private lateinit var objectDetector: ObjectDetector
    private lateinit var imageProvider: IImageProvider

    private lateinit var detectResults: MutableList<Detection>
    private lateinit var targetRectF : RectF
    private var isObjectModelReady = false

    fun readObjectModel(uri: Uri) : Boolean
    {
        try
        {
            if (uri.toString().isEmpty())
            {
                readInternalObjectModel()
                return (true)
            }
            Log.v(TAG, " Requested URI : $uri")

            var size = 0
            val cursor = contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.SIZE), null, null, null)
            if (cursor != null)
            {
                cursor.moveToFirst()
                size = cursor.getInt(0)
                cursor.close()
            }
            contentResolver.openInputStream(uri).use { stream ->
                val data = ByteArray(size)
                DataInputStream(stream).readFully(data)
                Log.v(TAG, " File Size is  : $size bytes. (data: ${data.size})")
                val options: ObjectDetectorOptions = ObjectDetectorOptions.builder().setMaxResults(max_detect_objects).build()
                val byteBuffer = ByteBuffer.allocateDirect(size)
                byteBuffer.order(ByteOrder.nativeOrder())
                byteBuffer.put(data)
                objectDetector = ObjectDetector.createFromBufferAndOptions(byteBuffer, options)
                //objectDetector = ObjectDetector.createFromBufferAndOptions(ByteBuffer.wrap(data), options)
            }
            isObjectModelReady = true
            Log.v(TAG, " ----- ObjectDetector is Ready! -----")
            return (true)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        isObjectModelReady = false
        try
        {
            activity.runOnUiThread { Toast.makeText(activity, activity.getString(R.string.object_detection_model_load_failure), Toast.LENGTH_SHORT).show() }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        readInternalObjectModel()
        return (false)
    }

    private fun readInternalObjectModel()
    {
        try
        {
            val fileDescriptor = activity.assets.openFd(TFLITE_OBJECT_FILE)
            val fileSize = fileDescriptor.length.toInt()
            val data = ByteArray(fileSize)
            val inputStream = fileDescriptor.createInputStream()
            inputStream.read(data, 0 , fileSize)
            Log.v(TAG, " File Size is  : $fileSize bytes. (data: ${data.size})")
            val options: ObjectDetectorOptions = ObjectDetectorOptions.builder().setMaxResults(max_detect_objects).build()
            val byteBuffer = ByteBuffer.allocateDirect(fileSize)
            byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.put(data)
            objectDetector = ObjectDetector.createFromBufferAndOptions(byteBuffer, options)
            //objectDetector = ObjectDetector.createFromBufferAndOptions(ByteBuffer.wrap(data), options)
            isObjectModelReady = true
            Log.v(TAG, " ===== ObjectDetector is Ready! =====")
        }
        catch (t: Throwable)
        {
            t.printStackTrace()
        }
    }

    fun setImageProvider(imageProvider: IImageProvider)
    {
        this.imageProvider = imageProvider
    }

    override fun onDraw(canvas: Canvas?, imageRectF: RectF, rotationDegrees: Int)
    {
        try
        {
            if (!::targetRectF.isInitialized)
            {
                Log.v(TAG, " object file is not ready... ")
                return
            }

            if (canvas != null)
            {
                Log.v(TAG, "onDraw...")

                val paintText = Paint()
                paintText.strokeWidth = 1.0f
                paintText.style = Paint.Style.FILL
                paintText.isAntiAlias = true
                paintText.textSize = TypedValue.applyDimension(COMPLEX_UNIT_PT, 9.0f, activity.resources.displayMetrics)
                paintText.setShadowLayer(5.0f, 3.0f, 3.0f, Color.BLACK)

                val paintRect = Paint()
                paintRect.strokeWidth = 3.0f
                paintRect.style = Paint.Style.STROKE
                paintRect.isAntiAlias = true
                paintRect.setShadowLayer(5.0f, 3.0f, 3.0f, Color.BLACK)

                val posWidth = abs(imageRectF.right - imageRectF.left) / abs(targetRectF.right - targetRectF.left)
                val posHeight = abs(imageRectF.bottom - imageRectF.top) / abs(targetRectF.bottom - targetRectF.top)

                val centerX = canvas.width / 2
                val centerY = canvas.height / 2

                var count = 1
                if (::detectResults.isInitialized)
                {
                    for (r in detectResults)
                    {
                        //Log.v(TAG, "[$count](${r.boundingBox}) : ${r.categories[0].label} : ${r.categories[0].score}")
                        if (r.categories[0].score > confidence_level)
                        {
                            val drawText = String.format("%s %2.1f%%",r.categories[0].label, +(r.categories[0].score) * 100.0f)
                            paintRect.color = decideColor(count)
                            paintText.color = decideColor(count)
                            if (rotationDegrees != 0)
                            {
                                canvas.drawText(
                                    drawText,
                                    r.boundingBox.centerY() * posWidth + imageRectF.left,
                                    r.boundingBox.centerX() * posHeight + imageRectF.top,
                                    paintText
                                )

                                canvas.rotate(rotationDegrees.toFloat(), centerX.toFloat(), centerY.toFloat())
                                canvas.drawRoundRect(
                                    r.boundingBox.left * posWidth + imageRectF.left,
                                    r.boundingBox.top * posHeight + imageRectF.top,
                                    r.boundingBox.right * posWidth + imageRectF.left,
                                    r.boundingBox.bottom * posHeight + imageRectF.top,
                                    0.5f,
                                    0.5f,
                                    paintRect
                                )
                                canvas.rotate(-rotationDegrees.toFloat(), centerX.toFloat(), centerY.toFloat())
                            }
                            else
                            {
                                canvas.drawText(
                                    drawText,
                                    r.boundingBox.centerX() * posWidth + imageRectF.left,
                                    r.boundingBox.centerY() * posHeight + imageRectF.top,
                                    paintText
                                )
                                canvas.drawRoundRect(
                                    r.boundingBox.left * posWidth + imageRectF.left,
                                    r.boundingBox.top * posHeight + imageRectF.top,
                                    r.boundingBox.right * posWidth + imageRectF.left,
                                    r.boundingBox.bottom * posHeight + imageRectF.top,
                                    0.5f,
                                    0.5f,
                                    paintRect
                                )
                            }
                            count++
                        }
                    }
                }
                Log.v(TAG, " ----- DETECTED OBJECT : $count")
            }
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
            System.gc()
        }
    }

    private fun decideColor(index: Int) : Int
    {
        val choice = index % 4
        return (when (choice)
        {
            0 -> Color.BLUE
            1 -> Color.GREEN
            2 -> Color.MAGENTA
            3 -> Color.CYAN
            else -> Color.WHITE
        })
    }

    override fun refresh()
    {
        try
        {
            if (isObjectModelReady)
            {
                val bitmap = imageProvider.getImage()
                targetRectF = RectF(0.0f, 0.0f, (bitmap.width).toFloat(), (bitmap.height).toFloat())
                Log.v(TAG, " - - - - Object Detection (${targetRectF})")
                detectResults = objectDetector.detect(TensorImage.fromBitmap(bitmap))
                Log.v(TAG, " - - - - Object Detection (results: ${detectResults.size})")
            }
            else
            {
                Log.v(TAG, " - - - - The object detection model is not ready...")
            }
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
            activity.runOnUiThread {
                try
                {
                    // for avoid OOM loop...
                    val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(activity).edit()
                    editor.putString(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_1, "")
                    editor.apply()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object
    {
        private const val TFLITE_OBJECT_FILE = "aohina-model0-5.tflite"
        private val TAG = ObjectDetectionModelReader::class.java.simpleName
    }
}
