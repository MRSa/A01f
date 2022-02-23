package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.wrapper

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.IOmdsProtocolNotify
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation.OmdsSingleShotControl
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import java.lang.Exception

class OmdsCaptureControl(frameDisplay: IAutoFocusFrameDisplay, indicator: IIndicatorControl, statusChecker : ICameraStatus): ICaptureControl, IOmdsProtocolNotify
{
    private val singleShotControl = OmdsSingleShotControl(frameDisplay, indicator)

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

    override fun detectedOpcProtocol(opcProtocol: Boolean)
    {
        singleShotControl.detectedOpcProtocol(opcProtocol)
    }

    companion object
    {
        private val TAG: String = OmdsCaptureControl::class.java.simpleName
    }

}
