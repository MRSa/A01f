package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.takepicture

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class ContinuousShotControl(private val frameDisplayer: IAutoFocusFrameDisplay)
{
    private var camera: IPanasonicCamera? = null
    private val http = SimpleHttpClient()

    /**
     *
     *
     */
    fun setCamera(panasonicCamera: IPanasonicCamera)
    {
        camera = panasonicCamera
    }

    /**
     *
     *
     */
    fun continuousShot(isStop: Boolean)
    {
        Log.v(TAG, "continuousShot()")
        if (camera == null)
        {
            Log.v(TAG, "IPanasonicCamera is null...")
            return
        }
        try
        {
            val thread = Thread {
                try
                {
                    val command = if (isStop) { camera?.getCmdUrl().toString() + "cam.cgi?mode=camcmd&value=capture_cancel" } else { camera?.getCmdUrl().toString() + "cam.cgi?mode=camcmd&value=capture" }
                    val reply: String = http.httpGet(command, TIMEOUT_MS)
                    if (!reply.contains("ok"))
                    {
                        Log.v(TAG, "Capture Failure... : $reply")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                frameDisplayer.hideFocusFrame()
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ContinuousShotControl::class.java.simpleName
        private const val TIMEOUT_MS = 3000
    }


}