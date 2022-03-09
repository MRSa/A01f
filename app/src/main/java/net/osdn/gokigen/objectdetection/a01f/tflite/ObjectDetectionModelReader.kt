package net.osdn.gokigen.objectdetection.a01f.tflite

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ObjectDetectionModelReader(private val contentResolver: ContentResolver, private val max_detect_objects: Int)
{
    private lateinit var objectDetector: ObjectDetector

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

    companion object
    {
        private val TAG = ObjectDetectionModelReader::class.java.simpleName
    }
}
