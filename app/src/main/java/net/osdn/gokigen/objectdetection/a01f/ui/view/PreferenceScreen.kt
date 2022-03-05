package net.osdn.gokigen.objectdetection.a01f.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import net.osdn.gokigen.objectdetection.a01f.R
import net.osdn.gokigen.objectdetection.a01f.preference.A01fPrefsModel

@Composable
fun PreferenceScreen(navController: NavHostController, prefsModel: A01fPrefsModel)
{
    val scope = rememberCoroutineScope()
    val captureBothLiveViewAndCamera = prefsModel.captureBothLvAndCamera.observeAsState(initial = prefsModel.captureBothLvAndCamera.value ?: false)

    MaterialTheme {
        Column {
            SharedPrefsToggle(
                text = stringResource(R.string.pref_capture_both_camera_and_live_view),
                value = captureBothLiveViewAndCamera.value,
                onValueChanged = {
                    scope.launch {
                        prefsModel.setCaptureBothLvAndCamera(!captureBothLiveViewAndCamera.value)
                    }
                }
            )
        }
    }
}

@Composable
fun SharedPrefsToggle(text: String, value: Boolean, onValueChanged: (Boolean) -> Unit)
{
    Row (verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = value, onCheckedChange = onValueChanged)
        Text(text)
    }
}
