package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.takepicture

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class ContinuousShotControl(private val frameDisplayer: IAutoFocusFrameDisplay)
{
    private lateinit var camera: IPanasonicCamera
    private val http = SimpleHttpClient()

    fun setCamera(panasonicCamera: IPanasonicCamera)
    {
        camera = panasonicCamera
    }

    fun continuousShot(isStop: Boolean)
    {
        Log.v(TAG, "continuousShot()")
        if (!::camera.isInitialized)
        {
            Log.v(TAG, "IPanasonicCamera is not initialized...")
            return
        }
        try
        {
            val thread = Thread {
                try
                {
                    val sessionId = camera.getCommunicationSessionId()
                    val command = if (isStop) { camera.getCmdUrl().toString() + "cam.cgi?mode=camcmd&value=capture_cancel" } else { camera.getCmdUrl().toString() + "cam.cgi?mode=camcmd&value=capture" }
                    val reply = if (!sessionId.isNullOrEmpty())
                    {
                        val headerMap: MutableMap<String, String> = HashMap()
                        headerMap["X-SESSION_ID"] = sessionId
                        http.httpGetWithHeader(command, headerMap, null, TIMEOUT_MS) ?: ""
                    }
                    else
                    {
                        http.httpGet(command, TIMEOUT_MS)
                    }
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