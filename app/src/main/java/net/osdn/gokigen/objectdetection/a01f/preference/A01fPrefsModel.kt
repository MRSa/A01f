package net.osdn.gokigen.objectdetection.a01f.preference

import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus

class A01fPrefsModel : ViewModel()
{
    private lateinit var preference : SharedPreferences
    private lateinit var contentResolver: ContentResolver
    private val captureLiveViewImage : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val cameraConnectionMethodExpanded : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val cameraConnectionMethodIndex : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    private val connectionStatus : MutableLiveData<ICameraConnectionStatus.CameraConnectionStatus> by lazy { MutableLiveData<ICameraConnectionStatus.CameraConnectionStatus>() }

    private val useSecondObjectDetectionModel : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    val captureBothLvAndCamera: LiveData<Boolean> = captureLiveViewImage
    val isCameraConnectionMethodExpanded: LiveData<Boolean> = cameraConnectionMethodExpanded
    val cameraConnectionMethodSelectionIndex: LiveData<Int> = cameraConnectionMethodIndex
    val cameraConnectionStatus: LiveData<ICameraConnectionStatus.CameraConnectionStatus> = connectionStatus
    val useSecondObjectDetection: LiveData<Boolean> = useSecondObjectDetectionModel

    fun initializePreferences(activity: AppCompatActivity)
    {
        try
        {
            contentResolver = activity.contentResolver
            preference = PreferenceManager.getDefaultSharedPreferences(activity)
            captureLiveViewImage.value = preference.getBoolean(
                IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW,
                IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE
            )
            cameraConnectionMethodIndex.value = (preference.getString(
                IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX,
                IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX_DEFAULT_VALUE
            ))?.toInt()
            useSecondObjectDetectionModel.value = preference.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_USE_SECOND_OBJECT_DETECTION_MODEL,
                IPreferencePropertyAccessor.PREFERENCE_USE_SECOND_OBJECT_DETECTION_MODEL_DEFAULT_VALUE
            )
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setCaptureBothLvAndCamera(value: Boolean)
    {
        captureLiveViewImage.value = value
        try
        {
            val editor: SharedPreferences.Editor = preference.edit()
            editor.putBoolean(IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW, value)
            editor.apply()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setUseSecondObjectDetection(value: Boolean)
    {
        useSecondObjectDetectionModel.value = value
        try
        {
            val editor: SharedPreferences.Editor = preference.edit()
            editor.putBoolean(IPreferencePropertyAccessor.PREFERENCE_USE_SECOND_OBJECT_DETECTION_MODEL, value)
            editor.apply()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setIsCameraConnectionMethodExpanded(value: Boolean)
    {
        cameraConnectionMethodExpanded.value = value
    }

    fun setCameraConnectionMethodSelectionIndex(value: Int)
    {
        cameraConnectionMethodIndex.value = value
        try
        {
            val editor: SharedPreferences.Editor = preference.edit()
            editor.putString(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX, "" + value)
            editor.apply()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setCameraConnectionStatus(value: ICameraConnectionStatus.CameraConnectionStatus)
    {
        connectionStatus.postValue(value)
        //connectionStatus.value = value
    }

    fun getObjectDetectionFileName(number: Int = 0) : String
    {
        if (::preference.isInitialized)
        {
            try
            {
                val modeFileString =
                    if (number == 0)
                    {
                        preference.getString(IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_0, IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_DEFAULT_VALUE_0) ?: ""
                    }
                    else
                    {
                        preference.getString(IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_1, IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_DEFAULT_VALUE_1) ?: ""
                    }
                val fileName = modeFileString.substring(modeFileString.lastIndexOf("%2F") + "%2f".length)
                if (fileName.isNotEmpty())
                {
                    return (fileName)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        return (if (number != 0) { " (aohinakoko)" } else { " (aohina)" })
    }

    fun setObjectDetectionFileModel(uri: Uri, number: Int = 0)
    {
        if (::preference.isInitialized)
        {
            try
            {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val editor: SharedPreferences.Editor = preference.edit()
                val key =
                    if (number == 0)
                    {
                        IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_0
                    }
                    else
                    {
                        IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_1
                    }
                editor.putString(key, uri.toString())
                editor.apply()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}
