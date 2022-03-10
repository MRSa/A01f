package net.osdn.gokigen.objectdetection.a01f.tflite

import android.content.ContentResolver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs

class ObjectDetectionModelReader(private val contentResolver: ContentResolver, private val max_detect_objects: Int) : IAnotherDrawer, ILiveViewRefresher
{
    private lateinit var objectDetector: ObjectDetector
    private lateinit var imageProvider: IImageProvider

    private lateinit var detectResults: MutableList<Detection>
    private lateinit var targetRectF : RectF

    fun readObjectModel(context: AppCompatActivity, uri: Uri) : Boolean
    {
/*
        try
        {
            val openRequestIntent = Intent(ACTION_OPEN_DOCUMENT)
            openRequestIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            openRequestIntent.addCategory(Intent.CATEGORY_OPENABLE)
            openRequestIntent.type = "* / *"
            openRequestIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
                val targetUri = data.data?.data
                if (targetUri != null)
                {
                    contentResolver.takePersistableUriPermission(targetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }.launch(openRequestIntent)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
*/
        try
        {
            Log.v(TAG, " Request URI : $uri")

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
            Log.v(TAG, " ----- ObjectDetector is Ready! -----")
            return (true)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    fun setImageProvider(imageProvider: IImageProvider)
    {
        this.imageProvider = imageProvider
    }

    fun readObjectModelKeep(context: AppCompatActivity, uri: Uri) : Boolean
    {
        var path = ""
        try
        {
            val cursor = contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DATA), null, null, null)
            if (cursor != null)
            {
                cursor.moveToFirst()
                path = cursor.getString(0)
                cursor.close()
            }
            if (path.isNotEmpty())
            {

                //val modelFile = File(path)
                val options: ObjectDetectorOptions = ObjectDetectorOptions.builder().setMaxResults(max_detect_objects).build()
                objectDetector = ObjectDetector.createFromFileAndOptions(context, path, options)
                return (true)
            }
            else
            {
                Log.v(TAG, " Read Failure : Object Detection Model.")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    override fun onDraw(canvas: Canvas?, imageRectF: RectF, rotationDegrees: Int)
    {
        try
        {
            if (canvas != null)
            {
                Log.v(TAG, "onDraw...")

                val paint = Paint()
                paint.color = Color.GREEN
                paint.strokeWidth = 5.0f
                paint.style = Paint.Style.STROKE
                paint.isAntiAlias = true
                paint.textSize = 38.0f

                val posWidth = abs(imageRectF.right - imageRectF.left) / abs(targetRectF.right - targetRectF.left)
                val posHeight = abs(imageRectF.bottom - imageRectF.top) / abs(targetRectF.bottom - targetRectF.top)

                val centerX = canvas.width / 2
                val centerY = canvas.height / 2

                if (rotationDegrees != 0)
                {
                    canvas.rotate(rotationDegrees.toFloat(), centerX.toFloat(), centerY.toFloat())
                }

                var count = 1
                for (r in detectResults)
                {
                    Log.v(TAG, "[$count](${r.boundingBox}) : ${r.categories[0].label} : ${r.categories[0].score}")
                    if (r.categories[0].score > 0.5)
                    {
                        canvas.drawText(
                            r.categories[0].label,
                            r.boundingBox.centerX() * posWidth + imageRectF.left,
                            r.boundingBox.centerY() * posHeight + imageRectF.top,
                            paint
                        )
                        canvas.drawRoundRect(
                            r.boundingBox.left * posWidth + imageRectF.left,
                            r.boundingBox.top * posHeight + imageRectF.top,
                            r.boundingBox.right * posWidth + imageRectF.left,
                            r.boundingBox.bottom * posHeight + imageRectF.top,
                            0.5f,
                            0.5f,
                            paint
                        )
                    }
                    count++
                }

                if (rotationDegrees != 0)
                {
                    canvas.rotate(-rotationDegrees.toFloat(), centerX.toFloat(), centerY.toFloat())
                }

            }
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
        }
    }

    override fun refresh()
    {
        try
        {
            val bitmap = imageProvider.getImage()
            targetRectF = RectF(0.0f, 0.0f, (bitmap.width).toFloat(), (bitmap.height).toFloat())
            Log.v(TAG, " Object Detection (${targetRectF})")
            detectResults = objectDetector.detect(TensorImage.fromBitmap(bitmap))
            Log.v(TAG, " Object Detection (results: ${detectResults.size})")
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ObjectDetectionModelReader::class.java.simpleName
    }
}
