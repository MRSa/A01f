package net.osdn.gokigen.objectdetection.a01f.scene

import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.mangle.scene.CameraProvider
import net.osdn.gokigen.objectdetection.a01f.R

class SceneChanger(private val activity: AppCompatActivity, private val informationNotify: IInformationReceiver, private val vibrator : IVibrator, statusReceiver : ICameraStatusReceiver)
{
    private val cameraProvider = CameraProvider(activity, informationNotify, vibrator, statusReceiver)
    private val cameraControl0 = cameraProvider.getCameraXControl()

    init
    {
        try
        {
            cameraControl0.initialize()
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

            cameraControl0.startCamera(isPreviewView = false)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun finish()
    {
        Log.v(TAG, " finishCamera() ")
        cameraControl0.finishCamera()
    }

    fun getCameraControl() : ICameraControl { return (cameraControl0) }

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
