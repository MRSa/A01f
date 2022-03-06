package net.osdn.gokigen.objectdetection.a01f.tflite

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions

class ObjectDetectionModelReader(private val contentResolver: ContentResolver, private val max_detect_objects: Int)
{
    private lateinit var objectDetector: ObjectDetector

    fun readObjectModel(context: Context, uri: Uri) : Boolean
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
