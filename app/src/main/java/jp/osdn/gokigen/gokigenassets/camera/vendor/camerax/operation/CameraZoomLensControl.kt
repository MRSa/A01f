package jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IZoomLensControl


class CameraZoomLensControl(private val cameraXCameraControl: CameraXCameraControl) : IZoomLensControl
{
    private var isDrivingZoom = false

    override fun canZoom(): Boolean
    {
        return (false)
    }

    override fun updateStatus()
    {
        //
    }

    override fun getMaximumFocalLength(): Float
    {
        return (0.0f)
    }

    override fun getMinimumFocalLength(): Float
    {
       return (0.0f)
    }

    override fun getCurrentFocalLength(): Float
    {
        return (0.0f)
    }

    override fun driveZoomLens(targetLength: Float)
    {
        Log.v(TAG, " driveZoomLens($targetLength)")
    }

    override fun driveZoomLens(isZoomIn: Boolean)
    {
        Log.v(TAG, " driveZoomLens($isZoomIn)")
    }

    override fun moveInitialZoomPosition()
    {
        Log.v(TAG, " moveInitialZoomPosition()")
    }

    override fun isDrivingZoomLens(): Boolean
    {
        return (isDrivingZoom)
    }

    companion object
    {
        private val TAG = CameraZoomLensControl::class.java.simpleName
    }

}
