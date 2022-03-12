package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.platform.LocalUriHandler
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
    val padding = 2.dp

    MaterialTheme {
        Column {
            Spacer(Modifier.size(padding))
            CaptureBothLiveViewAndCamera(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            FilePickerForObjectDetectionModel(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            CameraConnectionMethodDropdown(prefsModel, vibrator)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            Spacer(Modifier.size(padding))
            ShowAboutGokigen()
            Spacer(Modifier.size(padding))
            ShowGokigenPrivacyPolicy()
            Spacer(Modifier.size(padding))
        }
    }
}

@Composable
fun CaptureBothLiveViewAndCamera(prefsModel: A01fPrefsModel)
{
    val scope = rememberCoroutineScope()
    val captureBothLiveViewAndCamera = prefsModel.captureBothLvAndCamera.observeAsState(initial = prefsModel.captureBothLvAndCamera.value ?: false)
    Row (verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = captureBothLiveViewAndCamera.value,
            onCheckedChange = {
                scope.launch {
                    prefsModel.setCaptureBothLvAndCamera(!captureBothLiveViewAndCamera.value)
                }
            })
        Text(stringResource(R.string.pref_capture_both_camera_and_live_view))
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
fun ShowAboutGokigen()
{
    val uriHandler = LocalUriHandler.current
    val openUri = stringResource(R.string.pref_instruction_manual_url)
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = stringResource(R.string.pref_instruction_manual),
                color = MaterialTheme.colors.primaryVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = openUri,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.clickable(onClick = { uriHandler.openUri(openUri) })
            )
        }
    }
}

@Composable
fun ShowGokigenPrivacyPolicy()
{
    val uriHandler = LocalUriHandler.current
    val openUri = stringResource(R.string.pref_privacy_policy_url)
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = stringResource(R.string.pref_privacy_policy),
                color = MaterialTheme.colors.primaryVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = openUri,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.clickable(onClick = { uriHandler.openUri(openUri) })
            )
        }
    }
}
