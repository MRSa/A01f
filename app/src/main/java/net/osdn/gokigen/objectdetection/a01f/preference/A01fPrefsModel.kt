package net.osdn.gokigen.objectdetection.a01f.preference

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager

class A01fPrefsModel : ViewModel()
{
    private lateinit var preference : SharedPreferences
    private val captureLiveViewImage : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val cameraConnectionMethodExpanded : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val cameraConnectionMethodIndex : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }

    val captureBothLvAndCamera: LiveData<Boolean> = captureLiveViewImage
    val isCameraConnectionMethodExpanded: LiveData<Boolean> = cameraConnectionMethodExpanded
    val cameraConnectionMethodSelectionIndex: LiveData<Int> = cameraConnectionMethodIndex

    fun initializePreferences(activity: AppCompatActivity)
    {
        try
        {
            preference = PreferenceManager.getDefaultSharedPreferences(activity)
            captureLiveViewImage.value = preference.getBoolean(
                IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW,
                IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE
            )
            cameraConnectionMethodIndex.value = (preference.getString(
                IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX,
                IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX_DEFAULT_VALUE
            ))?.toInt()
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

}
