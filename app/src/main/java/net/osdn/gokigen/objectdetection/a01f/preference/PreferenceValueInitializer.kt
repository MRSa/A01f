package net.osdn.gokigen.objectdetection.a01f.preference

import android.content.Context
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.preference.IPreferenceValueInitializer
import androidx.core.content.edit

class PreferenceValueInitializer() : IPreferenceValueInitializer
{
    override fun initializePreferences(context : Context)
    {
        try
        {
            initializeApplicationPreferences(context)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun initializeApplicationPreferences(context : Context)
    {
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context) ?: return
            val items : Map<String, *> = preferences.all
            preferences.edit {

                if (!items.containsKey(IPreferencePropertyAccessor.EXTERNAL_STORAGE_LOCATION)) {
                    putString(
                        IPreferencePropertyAccessor.EXTERNAL_STORAGE_LOCATION,
                        IPreferencePropertyAccessor.EXTERNAL_STORAGE_LOCATION_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_NOTIFICATIONS)) {
                    putBoolean(
                        IPreferencePropertyAccessor.PREFERENCE_NOTIFICATIONS,
                        IPreferencePropertyAccessor.PREFERENCE_NOTIFICATIONS_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_USE_CAMERA_X_PREVIEW)) {
                    putBoolean(
                        IPreferencePropertyAccessor.PREFERENCE_USE_CAMERA_X_PREVIEW,
                        IPreferencePropertyAccessor.PREFERENCE_USE_CAMERA_X_PREVIEW_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_SAVE_LOCAL_LOCATION)) {
                    putBoolean(
                        IPreferencePropertyAccessor.PREFERENCE_SAVE_LOCAL_LOCATION,
                        IPreferencePropertyAccessor.PREFERENCE_SAVE_LOCAL_LOCATION_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.SHOW_GRID_STATUS)) {
                    putBoolean(
                        IPreferencePropertyAccessor.SHOW_GRID_STATUS,
                        IPreferencePropertyAccessor.SHOW_GRID_STATUS_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.CACHE_LIVE_VIEW_PICTURES)) {
                    putBoolean(
                        IPreferencePropertyAccessor.CACHE_LIVE_VIEW_PICTURES,
                        IPreferencePropertyAccessor.CACHE_LIVE_VIEW_PICTURES_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.NUMBER_OF_CACHE_PICTURES)) {
                    putString(
                        IPreferencePropertyAccessor.NUMBER_OF_CACHE_PICTURES,
                        IPreferencePropertyAccessor.NUMBER_OF_CACHE_PICTURES_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW)) {
                    putBoolean(
                        IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW,
                        IPreferencePropertyAccessor.CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.CAPTURE_ONLY_LIVE_VIEW)) {
                    putBoolean(
                        IPreferencePropertyAccessor.CAPTURE_ONLY_LIVE_VIEW,
                        IPreferencePropertyAccessor.CAPTURE_ONLY_LIVE_VIEW_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.CAPTURE_ONLY_EXTERNAL_CAMERA)) {
                    putBoolean(
                        IPreferencePropertyAccessor.CAPTURE_ONLY_EXTERNAL_CAMERA,
                        IPreferencePropertyAccessor.CAPTURE_ONLY_EXTERNAL_CAMERA_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.USE_ONLY_SINGLE_CAMERA_X)) {
                    putBoolean(
                        IPreferencePropertyAccessor.USE_ONLY_SINGLE_CAMERA_X,
                        IPreferencePropertyAccessor.USE_ONLY_SINGLE_CAMERA_X_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.USE_SELF_TIMER)) {
                    putString(
                        IPreferencePropertyAccessor.USE_SELF_TIMER,
                        IPreferencePropertyAccessor.USE_SELF_TIMER_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.SHOW_GRID)) {
                    putString(
                        IPreferencePropertyAccessor.SHOW_GRID,
                        IPreferencePropertyAccessor.SHOW_GRID_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.EXTERNAL_STORAGE_LOCATION)) {
                    putString(
                        IPreferencePropertyAccessor.EXTERNAL_STORAGE_LOCATION,
                        IPreferencePropertyAccessor.EXTERNAL_STORAGE_LOCATION_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.THETA_LIVEVIEW_RESOLUTION)) {
                    putString(
                        IPreferencePropertyAccessor.THETA_LIVEVIEW_RESOLUTION,
                        IPreferencePropertyAccessor.THETA_LIVEVIEW_RESOLUTION_DEFAULT_VALUE
                    )
                }

                ///// CAMERA METHOD /////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_1,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_1_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_2)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_2,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_2_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_3)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_3,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_3_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_4)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_4,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_4_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_5)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_5,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_5_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_6)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_6,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_6_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_7)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_7,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_7_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_8)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_8,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_8_DEFAULT_VALUE
                    )
                }

                ///// CONNECTION SEQUENCE /////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_1,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_1_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_2)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_2,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_2_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_3)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_3,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_3_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_4)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_4,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_4_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_5)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_5,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_5_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_6)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_6,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_6_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_7)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_7,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_7_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_8)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_8,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_8_DEFAULT_VALUE
                    )
                }

                ////////// CAMERA 1 //////////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_1,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_1_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_1,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_1_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_1,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_1_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_1,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_1_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_1,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_1_DEFAULT_VALUE
                    )
                }

                ////////// CAMERA 2 //////////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_2)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_2,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_2_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_2)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_2,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_2_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_2)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_2,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_2_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_2)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_2,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_2_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_2)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_2,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_2_DEFAULT_VALUE
                    )
                }

                ////////// CAMERA 3 //////////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_3)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_3,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_3_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_3)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_3,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_3_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_3)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_3,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_3_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_3)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_3,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_3_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_3)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_3,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_3_DEFAULT_VALUE
                    )
                }

                ////////// CAMERA 4 //////////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_4)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_4,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_4_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_4)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_4,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_4_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_4)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_4,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_4_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_4)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_4,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_4_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_4)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_4,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_4_DEFAULT_VALUE
                    )
                }

                ////////// CAMERA 5 //////////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_5)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_5,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_5_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_5)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_5,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_5_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_5)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_5,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_5_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_5)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_5,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_5_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_5)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_5,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_5_DEFAULT_VALUE
                    )
                }

                ////////// CAMERA 6 //////////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_6)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_6,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_6_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_6)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_6,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_6_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_6)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_6,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_6_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_6)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_6,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_6_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_6)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_6,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_6_DEFAULT_VALUE
                    )
                }

                ////////// CAMERA 7 //////////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_7)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_7,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_7_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_7)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_7,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_7_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_7)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_7,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_7_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_7)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_7,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_7_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_7)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_7,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_7_DEFAULT_VALUE
                    )
                }

                ////////// CAMERA 8 //////////
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_8)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_8,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_8_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_8)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_8,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_8_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_8)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_8,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_8_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_8)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_8,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_8_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_8)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_8,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_8_DEFAULT_VALUE
                    )
                }

                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX,
                        IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_INDEX_DEFAULT_VALUE
                    )
                }

                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_0)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_0,
                        IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_DEFAULT_VALUE_0
                    )
                }

                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_0)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_0,
                        IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_DEFAULT_VALUE_0
                    )
                }

                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_1,
                        IPreferencePropertyAccessor.PREFERENCE_OBJECT_DETECTION_MODEL_FILE_DEFAULT_VALUE_1
                    )
                }

                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_1)) {
                    putString(
                        IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_1,
                        IPreferencePropertyAccessor.PREFERENCE_NUMBER_OF_OBJECT_DETECTION_DEFAULT_VALUE_1
                    )
                }

                if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_USE_SECOND_OBJECT_DETECTION_MODEL)) {
                    putBoolean(
                        IPreferencePropertyAccessor.PREFERENCE_USE_SECOND_OBJECT_DETECTION_MODEL,
                        IPreferencePropertyAccessor.PREFERENCE_USE_SECOND_OBJECT_DETECTION_MODEL_DEFAULT_VALUE
                    )
                }

            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }
}
