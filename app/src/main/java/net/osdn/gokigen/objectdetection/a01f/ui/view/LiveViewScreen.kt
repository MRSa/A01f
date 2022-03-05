package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.LiveImageView
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import net.osdn.gokigen.objectdetection.a01f.R

@Composable
fun LiveViewScreen(navController: NavHostController, cameraControl: ICameraControl, vibrator : IVibrator)
{
    var liveView0 : LiveImageView? = null
    var isGrid by remember { mutableStateOf(false) }

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
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_screenshot_24),
                        contentDescription = "ScreenShot"
                    )
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
                liveView.invalidate()
                liveView.apply { }
            },
            update = { view ->
                liveView0 = view
            }
        )
    }
}
