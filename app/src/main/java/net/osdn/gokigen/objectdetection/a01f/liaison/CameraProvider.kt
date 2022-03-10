package net.osdn.gokigen.objectdetection.a01f.liaison

import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.preference.CameraPreference
import jp.osdn.gokigen.gokigenassets.camera.DummyCameraControl
import jp.osdn.gokigen.gokigenassets.camera.preference.ICameraPreferenceProvider
import jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation.CameraControl
import jp.osdn.gokigen.gokigenassets.camera.console.ConsolePanelControl
import jp.osdn.gokigen.gokigenassets.camera.example.ExamplePictureControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.preference.CameraPreferenceKeySet
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
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher
import jp.osdn.gokigen.gokigenassets.liveview.image.CameraLiveViewListenerImpl
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_METHOD_1
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_METHOD_1_DEFAULT_VALUE
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION1_1
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION1_1_DEFAULT_VALUE
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION2_1
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION2_1_DEFAULT_VALUE
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION3_1
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION3_1_DEFAULT_VALUE
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION4_1
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION4_1_DEFAULT_VALUE
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION5_1
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION5_1_DEFAULT_VALUE
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_SEQUENCE_1
import net.osdn.gokigen.objectdetection.a01f.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_SEQUENCE_1_DEFAULT_VALUE


class CameraProvider(private val activity: AppCompatActivity, private val informationNotify: IInformationReceiver, private val vibrator : IVibrator, private val statusReceiver : ICameraStatusReceiver)
{
    private val liveViewListener = CameraLiveViewListenerImpl(activity, informationNotify)
    private val cameraCoordinator = CameraControlCoordinator(informationNotify)
    private var cameraXisCreated = false
    private lateinit var cameraXControl0: ICameraControl

    fun setRefresher(refresher: ILiveViewRefresher)
    {
        liveViewListener.setRefresher(refresher = refresher)
    }

    fun getImageProvider() : IImageProvider
    {
        return (liveViewListener)
    }

    fun decideCameraControl(connectionMethod : String, number : Int) : ICameraControl
    {
        try
        {
            val wrapper = PreferenceAccessWrapper(activity)
            val cameraPreference = setupCameraPreference0(wrapper)
            return (when (connectionMethod) {
                PREFERENCE_CAMERA_METHOD_NONE -> DummyCameraControl(number)
                PREFERENCE_CAMERA_METHOD_CONSOLE -> prepareConsolePanelControl(cameraPreference, number)
                PREFERENCE_CAMERA_METHOD_EXAMPLE -> prepareExamplePictureControl(cameraPreference, number, liveViewListener)
                PREFERENCE_CAMERA_METHOD_CAMERAX -> prepareCameraXControl(cameraPreference, number, liveViewListener)
                PREFERENCE_CAMERA_METHOD_THETA -> prepareThetaCameraControl(cameraPreference, number, liveViewListener)
                PREFERENCE_CAMERA_METHOD_PENTAX -> preparePentaxCameraControl(cameraPreference, number, liveViewListener)
                PREFERENCE_CAMERA_METHOD_PANASONIC -> preparePanasonicCameraControl(cameraPreference, number, liveViewListener)
                PREFERENCE_CAMERA_METHOD_SONY -> prepareSonyCameraControl(cameraPreference, number, liveViewListener)
                PREFERENCE_CAMERA_METHOD_PIXPRO -> preparePixproCameraControl(cameraPreference, number, liveViewListener)
                PREFERENCE_CAMERA_METHOD_OMDS -> prepareOmdsCameraControl(cameraPreference, number, liveViewListener)
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
            return (prepareCameraXControl(setupCameraPreference0(PreferenceAccessWrapper(activity)), number, liveViewListener))
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
        val method  = wrapper.getString(PREFERENCE_CAMERA_METHOD_1, PREFERENCE_CAMERA_METHOD_1_DEFAULT_VALUE)
        val sequence  = wrapper.getString(PREFERENCE_CAMERA_SEQUENCE_1, PREFERENCE_CAMERA_SEQUENCE_1_DEFAULT_VALUE)
        val option1  = wrapper.getString(PREFERENCE_CAMERA_OPTION1_1, PREFERENCE_CAMERA_OPTION1_1_DEFAULT_VALUE)
        val option2  = wrapper.getString(PREFERENCE_CAMERA_OPTION2_1, PREFERENCE_CAMERA_OPTION2_1_DEFAULT_VALUE)
        val option3  = wrapper.getString(PREFERENCE_CAMERA_OPTION3_1, PREFERENCE_CAMERA_OPTION3_1_DEFAULT_VALUE)
        val option4  = wrapper.getString(PREFERENCE_CAMERA_OPTION4_1, PREFERENCE_CAMERA_OPTION4_1_DEFAULT_VALUE)
        val option5  = wrapper.getString(PREFERENCE_CAMERA_OPTION5_1, PREFERENCE_CAMERA_OPTION5_1_DEFAULT_VALUE)

        return (CameraPreference(0, wrapper, method, false, sequence, option1, option2, option3, option4, option5, CameraPreferenceKeySet(PREFERENCE_CAMERA_OPTION1_1, PREFERENCE_CAMERA_OPTION2_1, PREFERENCE_CAMERA_OPTION3_1, PREFERENCE_CAMERA_OPTION4_1, PREFERENCE_CAMERA_OPTION5_1)))
    }

    private fun prepareThetaCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl) : ICameraControl
    {
        return (ThetaCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, number, liveViewListener))
    }

    private fun preparePentaxCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl) : ICameraControl
    {
        return (RicohPentaxCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, number, liveViewListener))
    }

    private fun preparePanasonicCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl) : ICameraControl
    {
        return (PanasonicCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, cameraCoordinator, number, liveViewListener))
    }

    private fun prepareSonyCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl) : ICameraControl
    {
        return (SonyCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, cameraCoordinator, number, liveViewListener))
    }

    private fun preparePixproCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl) : ICameraControl
    {
        return (PixproCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, number, liveViewListener))
    }

    private fun prepareOmdsCameraControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl) : ICameraControl
    {
        return (OmdsCameraControl(activity, vibrator, informationNotify, cameraPreference, statusReceiver, number, liveViewListener))
    }

    private fun prepareConsolePanelControl(cameraPreference : ICameraPreferenceProvider, number : Int) : ICameraControl
    {
        return (ConsolePanelControl(activity, vibrator, informationNotify, cameraPreference, number))
    }

    private fun prepareExamplePictureControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl) : ICameraControl
    {
        return (ExamplePictureControl(activity, vibrator, informationNotify, cameraPreference, number, liveViewListener))
    }

    private fun prepareCameraXControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl): ICameraControl
    {
        if ((cameraXisCreated)&&(::cameraXControl0.isInitialized))
        {
            return (cameraXControl0)
        }
        cameraXControl0 = CameraControl(activity, cameraPreference, vibrator, informationNotify, statusReceiver, number, liveViewListener)
        cameraXisCreated = true
        return (cameraXControl0)
    }
}
