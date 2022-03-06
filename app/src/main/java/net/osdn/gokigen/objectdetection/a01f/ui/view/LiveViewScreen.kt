package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.LiveImageView
import jp.osdn.gokigen.gokigenassets.liveview.LiveViewOnTouchListener
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import net.osdn.gokigen.objectdetection.a01f.R
import net.osdn.gokigen.objectdetection.a01f.preference.A01fPrefsModel

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LiveViewScreen(navController: NavHostController, cameraControl: ICameraControl, prefsModel: A01fPrefsModel, vibrator : IVibrator, onTouchListener: LiveViewOnTouchListener)
{
    var liveView0 : LiveImageView? = null
    var isGrid by remember { mutableStateOf(false) }
    val connectionStatus = prefsModel.cameraConnectionStatus.observeAsState(initial = prefsModel.cameraConnectionStatus.value ?: ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN)

    Column()
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
            //.height(140.dp)
        )
        {
            Row(modifier = Modifier.align(Alignment.TopStart)) {
                IconButton(
                    onClick = { cameraControl.getCameraShutter()?.doShutter() },
                    enabled = true
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_camera_24),
                        contentDescription = "Capture"
                    )
                }
                IconToggleButton(checked = isGrid, onCheckedChange = {
                    isGrid = it
                    Log.v("LiveViewScreen", "isGrid: $isGrid  $liveView0")
                    liveView0?.showGridFrame(isGrid, Color.WHITE)
                }) {
                    if (isGrid) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_grid_on_24),
                            contentDescription = "Grid On"
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_grid_off_24),
                            contentDescription = "Grid Off"
                        )
                    }
                }
            }
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = { }, enabled = false) {
                    val iconId = when (connectionStatus.value) {
                        ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED -> { R.drawable.ic_baseline_cloud_off_24 }
                        ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN -> { R.drawable.ic_baseline_cloud_off_24 }
                        ICameraConnectionStatus.CameraConnectionStatus.CONNECTING -> { R.drawable.ic_baseline_cloud_queue_24 }
                        ICameraConnectionStatus.CameraConnectionStatus.CONNECTED -> { R.drawable.ic_baseline_cloud_done_24 }
                    }
                    Icon(painter = painterResource(id = iconId), contentDescription = "ConnectionStatus")
                }
                IconButton(onClick = {
                    vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
                    navController.navigate("PreferenceScreen")
                }, enabled = true) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                        contentDescription = "Preferences"
                    )
                }
            }
        }

        AndroidView(
            factory = { context ->
                // Creates live-view screen
                Log.v("", "$isGrid")
                val liveView = LiveImageView(context)
                liveView0 = liveView
                cameraControl.setRefresher(0, liveView, liveView, liveView)
                liveView.injectDisplay(cameraControl)
                //liveView.setOnTouchListener(onTouchListener)
                liveView.invalidate()
                liveView.apply { }
            },
            update = { view ->
                liveView0 = view
            },
            modifier = Modifier.pointerInteropFilter {
                if (liveView0 == null)
                {
                    Log.v("LiveView", "liveView0 is null...")
                    false
                }
                else
                {
                    onTouchListener.onTouch(liveView0, it)
                }
            }
        )
    }
}
