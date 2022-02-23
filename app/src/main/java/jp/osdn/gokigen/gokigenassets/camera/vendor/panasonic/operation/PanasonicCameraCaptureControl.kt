package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.takepicture.SingleShotControl
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay


class PanasonicCameraCaptureControl(frameDisplayer: IAutoFocusFrameDisplay, private val indicator: IIndicatorControl) : ICaptureControl
{
    private var singleShotControl = SingleShotControl(frameDisplayer)


    fun setCamera(panasonicCamera: IPanasonicCamera)
    {
        singleShotControl.setCamera(panasonicCamera)
    }

    /**
     * 撮影する
     *
     */
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
        private val TAG = PanasonicCameraCaptureControl::class.java.simpleName
    }
}
