package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.takepicture

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient


class SingleShotControl(private val frameDisplayer: IAutoFocusFrameDisplay)
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
    fun singleShot()
    {
        Log.v(TAG, "singleShot()")
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
                    val reply: String = http.httpGet(camera?.getCmdUrl().toString() + "cam.cgi?mode=camcmd&value=capture", TIMEOUT_MS)
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
        private val TAG = SingleShotControl::class.java.simpleName
        private const val TIMEOUT_MS = 3000
    }
}
