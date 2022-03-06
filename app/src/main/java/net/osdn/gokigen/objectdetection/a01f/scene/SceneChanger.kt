package net.osdn.gokigen.objectdetection.a01f.scene

import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.mangle.scene.CameraProvider
import net.osdn.gokigen.objectdetection.a01f.R
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor

class SceneChanger(private val activity: AppCompatActivity, private val informationNotify: IInformationReceiver, private val vibrator : IVibrator, statusReceiver : ICameraStatusReceiver)
{
    private val cameraProvider = CameraProvider(activity, informationNotify, vibrator, statusReceiver)
    private lateinit var cameraControl: ICameraControl  // = cameraProvider.getCameraXControl()

    init
    {
        try
        {
            val preference = PreferenceManager.getDefaultSharedPreferences(activity)
            val connectionIndex = try {
                (preference.getString(
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX,
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX_DEFAULT_VALUE
                ))?.toInt() ?: 3
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                3
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
            cameraControl.initialize()
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
        private val  TAG = SceneChanger::class.java.simpleName
    }
}
