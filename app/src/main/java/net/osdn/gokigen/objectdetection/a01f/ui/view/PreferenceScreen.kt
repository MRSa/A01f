package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import kotlinx.coroutines.launch
import net.osdn.gokigen.objectdetection.a01f.R
import net.osdn.gokigen.objectdetection.a01f.preference.A01fPrefsModel
import net.osdn.gokigen.objectdetection.a01f.preference.GetPickFilePermission

@Composable
fun PreferenceScreen(navController: NavHostController, prefsModel: A01fPrefsModel, vibrator : IVibrator)
{
    val scope = rememberCoroutineScope()
    val captureBothLiveViewAndCamera = prefsModel.captureBothLvAndCamera.observeAsState(initial = prefsModel.captureBothLvAndCamera.value ?: false)
    val padding = 2.dp

    MaterialTheme {
        Column {
            Spacer(Modifier.size(padding))
            SharedPrefsToggle(
                text = stringResource(R.string.pref_capture_both_camera_and_live_view),
                value = captureBothLiveViewAndCamera.value,
                onValueChanged = {
                    scope.launch {
                        prefsModel.setCaptureBothLvAndCamera(!captureBothLiveViewAndCamera.value)
                    }
                }
            )
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            FilePickerForObjectDetectionModel(prefsModel)
            Spacer(Modifier.size(padding))
            FilePickerForObjectLabelMapFile(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            CameraConnectionMethodDropdown(prefsModel, vibrator)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
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

@Composable
fun FilePickerForObjectDetectionModel(prefsModel: A01fPrefsModel)
{
    val scope = rememberCoroutineScope()

    //val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { modelUri ->
    val filePickerLauncher = rememberLauncherForActivityResult(GetPickFilePermission()) { modelUri ->
        if (modelUri != null)
        {
            Log.v("File Pick", "Picked file URI: $modelUri")
            scope.launch {
                prefsModel.setObjectDetectionFileModel(modelUri)
            }
        }
    }

    Row (verticalAlignment = Alignment.CenterVertically) {
        Text(" " + stringResource(id = R.string.pref_for_object_detection_model_file) + " " + prefsModel.getObjectDetectionFileName(), modifier = Modifier.clickable { filePickerLauncher.launch("*/*") })
    }
}


@Composable
fun FilePickerForObjectLabelMapFile(prefsModel: A01fPrefsModel)
{
    val scope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { modelUri ->
        if (modelUri != null)
        {
            Log.v("File Pick", "Picked file URI(LABEL MAP): $modelUri")
            scope.launch {
                prefsModel.setObjectDetectionLabelMapFileModel(modelUri)
            }
        }
    }

    Row (verticalAlignment = Alignment.CenterVertically) {
        Text(" " + stringResource(id = R.string.pref_for_object_detection_label_map_file) + " " + prefsModel.getObjectDetectionLabelMapFileName(), modifier = Modifier.clickable { filePickerLauncher.launch("*/*") })
    }
}
