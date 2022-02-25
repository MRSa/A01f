package net.osdn.gokigen.objectdetection.a01f.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.LiveImageView
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import net.osdn.gokigen.objectdetection.a01f.R

@Composable
fun LiveViewScreen(navController: NavHostController, cameraControl: ICameraControl, vibrator : IVibrator)
{
    Column()
    {
/**/
        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.height(140.dp)
        )
        {
            IconButton(onClick = { cameraControl.getCameraShutter()?.doShutter() }, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_camera_24), contentDescription = "Capture" )
            }
            IconButton(onClick = {
                vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
                navController.navigate("PreferenceScreen")
            }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_settings_24), contentDescription = "Preferences" )
            }
        }
/**/
/*
        Row()
        {
            IconButton(onClick = { cameraControl.getCameraShutter()?.doShutter() }) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_camera_24), contentDescription = "Capture" )
            }
            Spacer(modifier = Modifier.width(5.dp))
            IconButton(onClick = {
                vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
                navController.navigate("PreferenceScreen")
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_settings_24), contentDescription = "Preferences" )
            }
        }
*/
        // Adds view to Compose
        AndroidView(
            //modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
            factory = { context ->
                // Creates live-view screen
                val liveView = LiveImageView(context)
                cameraControl.setRefresher(0, liveView, liveView, liveView)
                liveView.injectDisplay(cameraControl)
                //liveView.showGridFrame(true, Color.WHITE)
                liveView.invalidate()
                liveView.apply {
                    // Sets up listeners for View -> Compose communication
                    //myView.setOnClickListener {
                    //    selectedItem.value = 1
                    //}
                }
            }
        )
/*
        Box(
            modifier = Modifier
                .fillMaxWidth()
            //.height(140.dp)
        )
        {
            IconButton(onClick = { cameraControl.getCameraShutter()?.doShutter() }, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_camera_24), contentDescription = "Capture" )
            }
            IconButton(onClick = {
                vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
                navController.navigate("PreferenceScreen")
            }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_settings_24), contentDescription = "Preferences" )
            }
        }
 */
/*
        IconButton(onClick = { cameraControl.getCameraShutter()?.doShutter() }) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_camera_24), contentDescription = null )
        }
        Spacer(modifier = Modifier.width(2.dp))
*/
    }

}

