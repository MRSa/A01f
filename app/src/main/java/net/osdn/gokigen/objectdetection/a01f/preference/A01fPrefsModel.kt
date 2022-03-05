package net.osdn.gokigen.objectdetection.a01f.preference

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager

class A01fPrefsModel : ViewModel()
{
    private val captureLiveViewImage : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private lateinit var preference : SharedPreferences
    val captureBothLvAndCamera: LiveData<Boolean> = captureLiveViewImage

    fun initializePreferences(activity: AppCompatActivity)
    {
        try
        {
            preference = PreferenceManager.getDefaultSharedPreferences(activity)
            captureLiveViewImage.value = preference.getBoolean(
                IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW,
                IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE
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
}
