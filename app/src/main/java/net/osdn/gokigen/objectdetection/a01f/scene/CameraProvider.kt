package jp.osdn.gokigen.mangle.scene

import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.preference.CameraPreference
import jp.osdn.gokigen.gokigenassets.camera.DummyCameraControl
import jp.osdn.gokigen.gokigenassets.camera.preference.ICameraPreferenceProvider
import jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation.CameraControl
import jp.osdn.gokigen.gokigenassets.camera.console.ConsolePanelControl
import jp.osdn.gokigen.gokigenassets.camera.example.ExamplePictureControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.OmdsCameraControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.CameraControlCoordinator
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.wrapper.PanasonicCameraControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.PixproCameraControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.RicohPentaxCameraControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.SonyCameraControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.ThetaCameraControl
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_CAMERAX
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_CONSOLE
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_EXAMPLE
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_NONE
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_OMDS
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PANASONIC
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PENTAX
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PIXPRO
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_SONY
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_THETA
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.USE_ONLY_SINGLE_CAMERA_X
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.USE_ONLY_SINGLE_CAMERA_X_DEFAULT_VALUE


class CameraProvider(private val activity: AppCompatActivity, private val informationNotify: IInformationReceiver, private val vibrator : IVibrator, private val statusReceiver : ICameraStatusReceiver)
{
    private val cameraCoordinator = CameraControlCoordinator(informationNotify)
    private var cameraXisCreated = false
    private var isOnlySingleCamera = false
    private lateinit var cameraXControl0: ICameraControl

    fun decideCameraControl(preferenceKey : String, number : Int) : ICameraControl
    {
        try
        {
            val wrapper = PreferenceAccessWrapper(activity)
            isOnlySingleCamera = wrapper.getBoolean(USE_ONLY_SINGLE_CAMERA_X, USE_ONLY_SINGLE_CAMERA_X_DEFAULT_VALUE)

            val cameraPreference = setupCameraPreference0(wrapper)

            return (when (cameraPreference.getCameraMethod()) {
                PREFERENCE_CAMERA_METHOD_NONE -> DummyCameraControl(number)
                PREFERENCE_CAMERA_METHOD_CONSOLE -> prepareConsolePanelControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_EXAMPLE -> prepareExamplePictureControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_CAMERAX -> prepareCameraXControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_THETA -> prepareThetaCameraControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_PENTAX -> preparePentaxCameraControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_PANASONIC -> preparePanasonicCameraControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_SONY -> prepareSonyCameraControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_PIXPRO -> preparePixproCameraControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_OMDS -> prepareOmdsCameraControl(cameraPreference, number)
                else -> DummyCameraControl(number)
            })
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (DummyCameraControl())
    }

    fun getCameraXControl(number : Int = 0) : ICameraControl
    {
        try
        {
            return (prepareCameraXControl(setupCameraPreference0(PreferenceAccessWrapper(activity)), number))
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (DummyCameraControl())
    }

    fun getCameraSelection(preferenceKey : String) : Int
    {
        var cameraSequence = 0
        try
        {
            val wrapper = PreferenceAccessWrapper(activity)
            cameraSequence = wrapper.getString(preferenceKey, "0").toInt()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (cameraSequence)
    }

    private fun setupCameraPreference0(wrapper : PreferenceAccessWrapper) : ICameraPreferenceProvider
    {
        return (CameraPreference(0, wrapper, PREFERENCE_CAMERA_METHOD_NONE))
    }

    private fun prepareThetaCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (ThetaCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, number))
    }

    private fun preparePentaxCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (RicohPentaxCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, number))
    }

    private fun preparePanasonicCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (PanasonicCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, cameraCoordinator, number))
    }

    private fun prepareSonyCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (SonyCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, cameraCoordinator, number))
    }

    private fun preparePixproCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (PixproCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, number))
    }

    private fun prepareOmdsCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (OmdsCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, number))
    }

    private fun prepareConsolePanelControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (ConsolePanelControl(activity, vibrator, informationNotify, cameraPreference, number))
    }

    private fun prepareExamplePictureControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (ExamplePictureControl(activity, vibrator, informationNotify, cameraPreference, number))
    }

    private fun prepareCameraXControl(cameraPreference : ICameraPreferenceProvider, number : Int): ICameraControl
    {
        if ((cameraXisCreated)&&(::cameraXControl0.isInitialized))
        {
            return (cameraXControl0)
        }
        cameraXControl0 = CameraControl(activity, cameraPreference, vibrator, informationNotify, number)
        cameraXisCreated = true
        return (cameraXControl0)
    }
}
