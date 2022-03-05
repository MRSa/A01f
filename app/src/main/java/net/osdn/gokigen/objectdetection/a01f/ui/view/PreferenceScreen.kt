package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import kotlinx.coroutines.launch
import net.osdn.gokigen.objectdetection.a01f.R
import net.osdn.gokigen.objectdetection.a01f.preference.A01fPrefsModel

@Composable
fun PreferenceScreen(navController: NavHostController, prefsModel: A01fPrefsModel, vibrator : IVibrator)
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
            CameraConnectionMethodDropdown(prefsModel, vibrator)
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

@Composable
fun CameraConnectionMethodDropdown(prefsModel: A01fPrefsModel, vibrator : IVibrator)
{
    val scope = rememberCoroutineScope()
    val cameraConnectionMethodExpanded = prefsModel.isCameraConnectionMethodExpanded.observeAsState(initial = prefsModel.isCameraConnectionMethodExpanded.value ?: false)
    val cameraConnectionMethodIndex = prefsModel.cameraConnectionMethodSelectionIndex.observeAsState(initial = prefsModel.cameraConnectionMethodSelectionIndex.value ?: 3)

    var expanded = cameraConnectionMethodExpanded.value
    var selectedIndex = cameraConnectionMethodIndex.value
    val itemLabels = stringArrayResource(id = R.array.connection_method)

    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopStart)) {
        Text(" " + stringResource(id = R.string.pref_connection_method) + " : " + itemLabels[selectedIndex],modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                expanded = true
                prefsModel.setIsCameraConnectionMethodExpanded(true)
            })
            .background(Color.White))
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            itemLabels.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    scope.launch {
                        selectedIndex = index
                        expanded = false
                        prefsModel.setIsCameraConnectionMethodExpanded(false)
                        prefsModel.setCameraConnectionMethodSelectionIndex(index)
                        vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT_SHORT)
                        Log.v("CameraConnectionMethod", "selected Index: $selectedIndex value: ")
                    }
                }) {
                    Text(text = s)
                }
            }
        }
    }
}