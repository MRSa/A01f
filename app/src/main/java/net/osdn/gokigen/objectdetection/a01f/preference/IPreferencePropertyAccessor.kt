package net.osdn.gokigen.objectdetection.a01f.preference

interface IPreferencePropertyAccessor
{
    companion object
    {
        // --- Camera Specific Preferences
        const val THETA_LIVEVIEW_RESOLUTION = "theta_liveview_resolution"
        const val THETA_LIVEVIEW_RESOLUTION_DEFAULT_VALUE = "{\"width\": 640, \"height\": 320, \"framerate\": 30}"
    }
}
