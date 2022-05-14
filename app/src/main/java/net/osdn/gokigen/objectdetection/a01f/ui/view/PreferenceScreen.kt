package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
            PreferenceScreenTitle()
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            ShowWifiSetting()
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            CaptureBothLiveViewAndCamera(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            FilePickerForObjectDetectionModel(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            FilePickerForObjectDetectionModel2(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            CameraConnectionMethodDropdown(prefsModel, vibrator)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
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
    val density = LocalDensity.current
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
        Text(text = stringResource(R.string.pref_capture_both_camera_and_live_view),
            fontSize = with(density) { 18.dp.toSp() },
            modifier = Modifier.clickable( onClick = {
                scope.launch { prefsModel.setCaptureBothLvAndCamera(!captureBothLiveViewAndCamera.value) }
            })
        )
    }
}

@Composable
fun CameraConnectionMethodDropdown(prefsModel: A01fPrefsModel, vibrator : IVibrator)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val cameraConnectionMethodExpanded = prefsModel.isCameraConnectionMethodExpanded.observeAsState(initial = prefsModel.isCameraConnectionMethodExpanded.value ?: false)
    val cameraConnectionMethodIndex = prefsModel.cameraConnectionMethodSelectionIndex.observeAsState(initial = prefsModel.cameraConnectionMethodSelectionIndex.value ?: 1)

    var expanded = cameraConnectionMethodExpanded.value
    var selectedIndex = cameraConnectionMethodIndex.value
    val itemLabels = stringArrayResource(id = R.array.connection_method)

    Box(modifier = Modifier
        .wrapContentSize(Alignment.TopStart)
        .padding(all = 10.dp)) {
        Row {
            Text(
                text = " " + stringResource(id = R.string.pref_connection_method) + " : " + itemLabels[selectedIndex],
                modifier = Modifier
                    .clickable(onClick = {
                        expanded = true
                        prefsModel.setIsCameraConnectionMethodExpanded(true)
                    }),
                fontSize = with(density) { 18.dp.toSp() }
            )
            Icon(imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "",
                modifier = Modifier
                .clickable(onClick = {
                    expanded = true
                    prefsModel.setIsCameraConnectionMethodExpanded(true)
                }))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
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
                    Text(text = s, fontSize = with(density) { 18.dp.toSp() })
                }
            }
        }
    }
}

@Composable
fun FilePickerForObjectDetectionModel(prefsModel: A01fPrefsModel)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    //val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { modelUri ->
    val filePickerLauncher = rememberLauncherForActivityResult(GetPickFilePermission()) { modelUri ->
        if (modelUri != null)
        {
            Log.v("File Pick", "Picked file  URI: $modelUri")
            scope.launch {
                prefsModel.setObjectDetectionFileModel(modelUri, 0)
            }
        }
    }

    Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(all = 10.dp)) {
        Text(stringResource(id = R.string.pref_for_object_detection_model_file) + " " + prefsModel.getObjectDetectionFileName(0), modifier = Modifier.clickable { filePickerLauncher.launch("*/*") }, fontSize = with(density) { 18.dp.toSp() })
    }
}

@Composable
fun FilePickerForObjectDetectionModel2(prefsModel: A01fPrefsModel)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val useSecondObjectDetection = prefsModel.useSecondObjectDetection.observeAsState(initial = prefsModel.useSecondObjectDetection.value ?: false)

    val filePickerLauncher = rememberLauncherForActivityResult(GetPickFilePermission()) { modelUri ->
        if (modelUri != null)
        {
            Log.v("File Pick", "Picked file (2nd) URI: $modelUri")
            scope.launch {
                prefsModel.setObjectDetectionFileModel(modelUri, 1)
            }
        }
    }

    Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(all = 10.dp)) {
            Checkbox(
                checked = useSecondObjectDetection.value,
                onCheckedChange = {
                    scope.launch {
                        prefsModel.setUseSecondObjectDetection(!useSecondObjectDetection.value)
                    }
                })
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(id = R.string.pref_for_object_detection_model_file2) + " " + prefsModel.getObjectDetectionFileName(1), modifier = Modifier.clickable { filePickerLauncher.launch("*/*") }, fontSize = with(density) { 18.dp.toSp() })
    }
}

@Composable
fun PreferenceScreenTitle()
{
    val density = LocalDensity.current
    TopAppBar()
    {
        Text(text = stringResource(id = R.string.pref_cat_application_settings),
            fontSize = with(density) { 24.dp.toSp() },
            modifier = Modifier.padding(all = 6.dp))
    }
}

@Composable
fun ShowWifiSetting()
{
    val context = LocalContext.current
    val density = LocalDensity.current
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.width(8.dp))
        Icon(imageVector = Icons.Default.Wifi, contentDescription = "Wifi",
            modifier = Modifier.clickable( onClick = {
                    context.startActivity(Intent(Intent(Settings.ACTION_WIFI_SETTINGS)))
                })
        )
        Text(
            text = stringResource(R.string.pref_wifi_settings),
            fontSize = with(density) { 18.dp.toSp() },
            modifier = Modifier.padding(all = 4.dp)
                .clickable( onClick = {
                    context.startActivity(Intent(Intent(Settings.ACTION_WIFI_SETTINGS)))
                })
        )
    }
}


@Composable
fun ShowAboutGokigen()
{
    val density = LocalDensity.current
    val uriHandler = LocalUriHandler.current
    val openUri = stringResource(R.string.pref_instruction_manual_url)
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = stringResource(R.string.pref_instruction_manual),
                color = MaterialTheme.colors.primaryVariant,
                fontSize = with(density) { 18.dp.toSp() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = openUri,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.clickable(onClick = { uriHandler.openUri(openUri) }),
                fontSize = with(density) { 14.dp.toSp() }
            )
        }
    }
}

@Composable
fun ShowGokigenPrivacyPolicy()
{
    val density = LocalDensity.current
    val uriHandler = LocalUriHandler.current
    val openUri = stringResource(R.string.pref_privacy_policy_url)
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = stringResource(R.string.pref_privacy_policy),
                color = MaterialTheme.colors.primaryVariant,
                fontSize = with(density) { 18.dp.toSp() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = openUri,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.clickable(onClick = { uriHandler.openUri(openUri) }),
                fontSize = with(density) { 14.dp.toSp() }
            )
        }
    }
}
