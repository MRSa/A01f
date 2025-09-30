package net.osdn.gokigen.objectdetection.a01f.tflite

import android.graphics.*
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
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector.ObjectDetectorOptions
import com.google.mediapipe.framework.image.BitmapImageBuilder

import java.io.DataInputStream
import java.nio.ByteBuffer
import java.util.Locale
import kotlin.math.abs
import androidx.core.content.edit
import java.nio.ByteOrder

class ObjectDetectionModelReader(
    private val activity: AppCompatActivity,
    private val id: Int = 0,
    private val maxDetectObjects: Int = 10,
    private val textPositionOffset: Float = 1.0f,
    private val colorOffset: Int = 0,
    private val strokeWidth: Float = 3.0f,
    private val objectHeader: String = "",
    private val confidenceLevel: Float = 0.5f
) : IAnotherDrawer, ILiveViewRefresher
{
    private val contentResolver = activity.contentResolver
    private lateinit var objectDetector: ObjectDetector
    private lateinit var imageProvider: IImageProvider

    private lateinit var detectionResult: ObjectDetectorResult
    private lateinit var targetRectF : RectF
    private var isObjectModelReady = false

    fun readObjectModel(uri: Uri) : Boolean
    {
        try
        {
            if (uri.toString().isEmpty())
            {
                readInternalObjectModel()
                return true
            }
            Log.v(TAG, " $id Requested URI : $uri")

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
                val byteBuffer = ByteBuffer.allocateDirect(size)
                byteBuffer.order(ByteOrder.nativeOrder())
                byteBuffer.put(data)
                byteBuffer.position(0) // バッファの読み取り位置を先頭に戻す
                val options: ObjectDetectorOptions = ObjectDetectorOptions.builder()
                    .setBaseOptions(BaseOptions.builder()
                        .setModelAssetBuffer(byteBuffer)
                        .build())
                    .setRunningMode(RunningMode.IMAGE)
                    .setMaxResults(maxDetectObjects)
                    // .setMinDetectionConfidence(confidenceLevel)
                    .build()
                objectDetector = ObjectDetector.createFromOptions(activity, options)
            }
            isObjectModelReady = true
            Log.v(TAG, " ----- ObjectDetector($id) is Ready! -----")
            return true
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
        return false
    }

    private fun readInternalObjectModel()
    {
        try
        {
            val targetObjectFile = if (id != 0) { TFLITE_OBJECT_FILE2 } else { TFLITE_OBJECT_FILE1 }
            val fileDescriptor = activity.assets.openFd(targetObjectFile)
            val fileSize = fileDescriptor.length.toInt()
            val data = ByteArray(fileSize)
            val inputStream = fileDescriptor.createInputStream()
            inputStream.read(data, 0 , fileSize)
            Log.v(TAG, " File Size is  : $fileSize bytes. (data: ${data.size})")
            val byteBuffer = ByteBuffer.allocateDirect(fileSize)
            byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.put(data)
            byteBuffer.position(0) // バッファの読み取り位置を先頭に戻す
            val options: ObjectDetectorOptions = ObjectDetectorOptions.builder()
                .setBaseOptions(BaseOptions.builder()
                    .setModelAssetBuffer(byteBuffer)
                    .build())
                .setRunningMode(RunningMode.IMAGE)
                .setMaxResults(maxDetectObjects)
                //.setMinDetectionConfidence(confidenceLevel) // 信頼度の閾値を設定
                .build()
            objectDetector = ObjectDetector.createFromOptions(activity, options)
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
                Log.v(TAG, "onDraw($id)...")

                val paintText = Paint()
                paintText.strokeWidth = 1.0f
                paintText.style = Paint.Style.FILL
                paintText.isAntiAlias = true
                paintText.textSize = TypedValue.applyDimension(COMPLEX_UNIT_PT, 9.0f, activity.resources.displayMetrics)
                paintText.setShadowLayer(5.0f, 3.0f, 3.0f, Color.BLACK)

                val paintRect = Paint()
                paintRect.strokeWidth = strokeWidth
                paintRect.style = Paint.Style.STROKE
                paintRect.isAntiAlias = true
                paintRect.setShadowLayer(5.0f, 3.0f, 3.0f, Color.BLACK)
                if (id > 0)
                {
                    paintRect.pathEffect = DashPathEffect(floatArrayOf(15.0f, 15.0f), 0.0f)
                }

                val posWidth = abs(imageRectF.right - imageRectF.left) / abs(targetRectF.right - targetRectF.left)
                val posHeight = abs(imageRectF.bottom - imageRectF.top) / abs(targetRectF.bottom - targetRectF.top)

                val centerX = canvas.width / 2
                val centerY = canvas.height / 2

                var count = 1
                if (::detectionResult.isInitialized)
                {
                    for (r in detectionResult.detections())
                    {
                        val boundingBox = r.boundingBox()
                        val category = r.categories()[0]

                        //Log.v(TAG, "[$count](${r.boundingBox}) : ${r.categories[0].label} : ${r.categories[0].score}")
                        if (category.score() > confidenceLevel)
                        {
                            val drawText = String.format(Locale.US, "%s %s %2.1f%%",objectHeader, category.categoryName(), +(category.score()) * 100.0f)
                            paintRect.color = decideColor(count)
                            paintText.color = decideColor(count)
                            if (rotationDegrees != 0)
                            {
                                canvas.drawText(
                                    drawText,
                                    boundingBox.centerX() * posWidth + imageRectF.left,
                                    boundingBox.centerY() * posHeight * textPositionOffset + imageRectF.top,
                                    paintText
                                )

                                canvas.rotate(rotationDegrees.toFloat(), centerX.toFloat(), centerY.toFloat())
                                canvas.drawRoundRect(
                                    boundingBox.left * posWidth + imageRectF.left,
                                    boundingBox.top * posHeight + imageRectF.top,
                                    boundingBox.right * posWidth + imageRectF.left,
                                    boundingBox.bottom * posHeight + imageRectF.top,
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
                                    boundingBox.centerX() * posWidth + imageRectF.left,
                                    boundingBox.centerY() * posHeight  * textPositionOffset + imageRectF.top,
                                    paintText
                                )
                                canvas.drawRoundRect(
                                    boundingBox.left * posWidth + imageRectF.left,
                                    boundingBox.top * posHeight + imageRectF.top,
                                    boundingBox.right * posWidth + imageRectF.left,
                                    boundingBox.bottom * posHeight + imageRectF.top,
                                    0.5f,
                                    0.5f,
                                    paintRect
                                )
                            }
                            count++
                        }
                    }
                }
                Log.v(TAG, " ----- DETECTED OBJECT($id) : $count")
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
        val choice = ((index + colorOffset) % 11)
        return (when (choice)
        {
            0 -> Color.rgb(255, 20, 147)   // deep pink
            1 -> Color.rgb(0, 250, 0)      // green
            2 -> Color.rgb(255, 0, 255)    // magenta
            3 -> Color.rgb(0, 255, 255)    // aqua
            4 -> Color.rgb(127, 255, 212)   // aquamarine
            5 -> Color.rgb(255,165,0)       // orange
            6 -> Color.rgb(0, 0, 255)       // blue
            7 -> Color.rgb(144, 238, 144)   // light green
            8 -> Color.rgb(138, 43, 226)    // blue violet
            9 -> Color.rgb(250, 128, 114)   // salmon
            10 -> Color.rgb(255, 215, 0)     // gold
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
                Log.v(TAG, " - - - - Object Detection[$id] (${targetRectF})")
                val mpImage = BitmapImageBuilder(bitmap).build()
                detectionResult = objectDetector.detect(mpImage)
                Log.v(TAG, " - - - - Object Detection[$id] (results: ${detectionResult.detections().size})")
            }
            else
            {
                Log.v(TAG, " - - - - The object detection model($id) is not ready...")
            }
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
            activity.runOnUiThread {
                try
                {
                    // for avoid OOM loop...
                    PreferenceManager.getDefaultSharedPreferences(activity).edit {
                        putString(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_1, "")
                    }
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
        private const val TFLITE_OBJECT_FILE1 = "aohina-model0_11.tflite"
        private const val TFLITE_OBJECT_FILE2 = "aohinakoko0_7.tflite"
        private val TAG = ObjectDetectionModelReader::class.java.simpleName
    }
}
