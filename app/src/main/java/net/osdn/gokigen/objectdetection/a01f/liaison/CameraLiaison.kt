package net.osdn.gokigen.objectdetection.a01f.liaison

import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import net.osdn.gokigen.objectdetection.a01f.R
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor
import net.osdn.gokigen.objectdetection.a01f.tflite.ObjectDetectionModelReader

class CameraLiaison(private val activity: AppCompatActivity, private val informationNotify: IInformationReceiver, private val vibrator : IVibrator, statusReceiver : ICameraStatusReceiver)
{
    private val drawers = AnotherDrawerHolder()
    private val cameraProvider = CameraProvider(activity, informationNotify, vibrator, statusReceiver)
    private lateinit var cameraControl: ICameraControl  // = cameraProvider.getCameraXControl()
    private lateinit var objectDetectionModel: ObjectDetectionModelReader
    private lateinit var objectDetectionModel2nd : ObjectDetectionModelReader

    init
    {
        try
        {
            val preference = PreferenceManager.getDefaultSharedPreferences(activity)
            val connectionIndex =
                try
                {
                    (preference.getString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX_DEFAULT_VALUE
                    ))?.toInt() ?: 2
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    3
                }
            val is2ndDetection: Boolean =
                try
                {
                    preference.getBoolean(IPreferencePropertyAccessor.PREFERENCE_USE_SECOND_OBJECT_DETECTION_MODEL, IPreferencePropertyAccessor.PREFERENCE_USE_SECOND_OBJECT_DETECTION_MODEL_DEFAULT_VALUE)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    false
                }
            try
            {
                initializeObjectDetectionModel(0)
                if (is2ndDetection)
                {
                    initializeObjectDetectionModel(1)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            cameraControl = try
            {
                val items = activity.resources.getStringArray(R.array.connection_method_value)
                cameraProvider.decideCameraControl(items[connectionIndex], 0)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                cameraProvider.getCameraXControl()
            }
            Log.v(TAG, " setImageProvider... [2nd:$is2ndDetection]")
            cameraProvider.setRefresher(objectDetectionModel)
            objectDetectionModel.setImageProvider(cameraProvider.getImageProvider())
            if ((is2ndDetection)&&(::objectDetectionModel2nd.isInitialized))
            {
                cameraProvider.setRefresher(objectDetectionModel2nd)
                objectDetectionModel2nd.setImageProvider(cameraProvider.getImageProvider())
            }
            cameraControl.initialize()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun initializeObjectDetectionModel(number: Int = 0)
    {
        try
        {
            val preference = PreferenceManager.getDefaultSharedPreferences(activity)
            val key =
                if (number == 0)
                {
                    IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_0
                }
                else
                {
                    IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_1
                }
            val defaultValue =
                if (number == 0)
                {
                    IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_DEFAULT_VALUE_0
                }
                else
                {
                    IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_DEFAULT_VALUE_1
                }

            val maxObjectKey =
                if (number == 0)
                {
                    IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_0
                }
                else
                {
                    IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_1
                }
            val maxObjectDefaultValue =
                if (number == 0)
                {
                    IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_DEFAULT_VALUE_0
                }
                else
                {
                    IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_DEFAULT_VALUE_1
                }
            var maxObject: Int = (preference.getString(maxObjectKey, maxObjectDefaultValue) ?: "10").toInt()
            if ((maxObject <= 0)||(maxObject >= 100))
            {
                // オブジェクトの検出数は 1～99 までにする、その範囲を逸脱していた場合は 10 にする
                maxObject = 10
            }
            val modelUri = (preference.getString(key, defaultValue) ?: "").toUri()
            if (number == 0)
            {
                if (!::objectDetectionModel.isInitialized)
                {
                    objectDetectionModel = ObjectDetectionModelReader(activity, id = 0, maxObject, 0.5f)
                    drawers.addAnotherDrawer(objectDetectionModel)
                }
                if (!objectDetectionModel.readObjectModel(modelUri))
                {
                    Log.v(TAG, " -=-=-=-=-=-=-=-=-=-=-=-=- Object Detection Model Read Failure... $modelUri  -=-=-=-=-=-=-=-=-=-=-=-=- ")
                }
            }
            else
            {
                if (!::objectDetectionModel2nd.isInitialized)
                {
                    objectDetectionModel2nd = ObjectDetectionModelReader(activity, id = 1, maxObject, 0.5f)
                    drawers.addAnotherDrawer(objectDetectionModel2nd)
                }
                if (!objectDetectionModel2nd.readObjectModel(modelUri))
                {
                    Log.v(TAG, " -=-=-=-=-=-=-=-=-=-=-=-=- Object Detection Model(2nd) Read Failure... $modelUri  -=-=-=-=-=-=-=-=-=-=-=-=- ")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }




    fun initialize()
    {
        try
        {
            val msg = activity.getString(R.string.app_name)
            informationNotify.updateMessage(msg, isBold = false, isColor = true, color = Color.LTGRAY)

            cameraControl.startCamera(isPreviewView = false)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun connectToCamera()
    {
        try
        {
            cameraControl.connectToCamera()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun finish()
    {
        Log.v(TAG, " finishCamera() ")
        cameraControl.finishCamera(false)
    }

    fun getCameraControl() : ICameraControl { return (cameraControl) }
    fun getVibrator() : IVibrator { return (vibrator) }
    fun getAnotherDrawer() : IAnotherDrawer { return (drawers) }

    fun handleKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        try
        {
            //
            Log.v(TAG, " handleKeyDown($keyCode, ${event.action})")
        }
        catch (e : java.lang.Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    companion object
    {
        private val  TAG = CameraLiaison::class.java.simpleName
    }
}
