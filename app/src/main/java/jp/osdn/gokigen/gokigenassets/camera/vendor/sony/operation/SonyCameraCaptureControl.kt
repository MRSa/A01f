package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation.takepicture.SingleShotControl
import java.lang.Exception


class SonyCameraCaptureControl(frameDisplayer: IAutoFocusFrameDisplay, indicator: IIndicatorControl) : ICaptureControl
{
    private val singleShotControl = SingleShotControl(frameDisplayer, indicator)

    fun setCameraApi(sonyCameraApi: ISonyCameraApi)
    {
        singleShotControl.setCameraApi(sonyCameraApi)
    }

    override fun doCapture(kind: Int)
    {
        Log.v(TAG, "doCapture() : $kind")
        try
        {
            singleShotControl.singleShot()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = SonyCameraCaptureControl::class.java.simpleName
    }
}
