package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IZoomLensControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class PanasonicCameraZoomLensControl : IZoomLensControl
{
    private var camera: IPanasonicCamera? = null
    private var isZooming = false

    fun setCamera(panasonicCamera: IPanasonicCamera)
    {
        camera = panasonicCamera
    }

    override fun canZoom(): Boolean
    {
        Log.v(TAG, "canZoom()")
        return true
    }

    override fun updateStatus()
    {
        Log.v(TAG, "updateStatus()")
    }

    override fun getMaximumFocalLength(): Float
    {
        Log.v(TAG, "getMaximumFocalLength()")
        return 0.0f
    }

    override fun getMinimumFocalLength(): Float
    {
        Log.v(TAG, "getMinimumFocalLength()")
        return 0.0f
    }

    override fun getCurrentFocalLength(): Float
    {
        Log.v(TAG, "getCurrentFocalLength()")
        return 0.0f
    }

    override fun driveZoomLens(targetLength: Float)
    {
        Log.v(TAG, "driveZoomLens() : $targetLength")
    }

    override fun moveInitialZoomPosition()
    {
        Log.v(TAG, "moveInitialZoomPosition()")
    }

    override fun isDrivingZoomLens(): Boolean
    {
        Log.v(TAG, "isDrivingZoomLens()")
        return isZooming
    }

    /**
     *
     *
     */
    override fun driveZoomLens(isZoomIn: Boolean)
    {
        Log.v(TAG, "driveZoomLens() : $isZoomIn")
        if (camera == null)
        {
            Log.v(TAG, "IPanasonicCameraApi is null...")
            return
        }
        try
        {
            val http = SimpleHttpClient()
            val command: String = if (isZooming) {
                "cam.cgi?mode=camcmd&value=zoomstop"
            } else {
                if (isZoomIn) "cam.cgi?mode=camcmd&value=tele-normal" else "cam.cgi?mode=camcmd&value=wide-normal"
            }
            val thread = Thread {
                try
                {
                    val reply: String = http.httpGet(camera?.getCmdUrl() + command, TIMEOUT_MS)
                    if (reply.contains("ok"))
                    {
                        isZooming = !isZooming
                    }
                    else
                    {
                        Log.v(TAG, "driveZoomLens() reply is failure.")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
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
        private val TAG = PanasonicCameraZoomLensControl::class.java.simpleName
        private const val TIMEOUT_MS = 3000
    }
}