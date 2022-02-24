package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.LiveImageView
import net.osdn.gokigen.objectdetection.a01f.R
import net.osdn.gokigen.objectdetection.a01f.scene.SceneChanger
import net.osdn.gokigen.objectdetection.a01f.ui.theme.GokigenComposeAppsTheme

class ViewRootComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AbstractComposeView(context, attrs, defStyleAttr)
{
    private lateinit var changeScene: SceneChanger

    fun setSceneChanger(changeScene: SceneChanger) { this.changeScene = changeScene }

    @Composable
    override fun Content()
    {
        val navController = rememberNavController()
        GokigenComposeAppsTheme {
            Surface(color = MaterialTheme.colors.background) {
                NavigationMain(navController, changeScene.getCameraControl())
            }
        }
        Log.v(TAG, " ...NavigationRootComponent...")
    }

    companion object
    {
        private val TAG = ViewRootComponent::class.java.simpleName
    }
}

@Composable
fun NavigationMain(navController: NavHostController, cameraControl: ICameraControl)
{
    GokigenComposeAppsTheme {
        NavHost(
            navController = navController,
            startDestination = "LiveViewScreen"
        ) {
            composable("LiveViewScreen") {
                LiveViewScreen(navController = navController, cameraControl)
            }
            composable("ConfigScreen") {
                ConfigScreen(navController = navController)
            }
        }
    }
}

@Composable
fun LiveViewScreen(navController: NavHostController, cameraControl: ICameraControl)
{
    Column()
    {
        //Button(onClick = {cameraControl.getCameraShutter()?.doShutter() } ) { Text(text = "Capture") }
        IconButton(onClick = { cameraControl.getCameraShutter()?.doShutter() }) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_camera_24), contentDescription = null )
        }
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
        IconButton(onClick = { cameraControl.getCameraShutter()?.doShutter() }) {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_camera_24), contentDescription = null )
        }
        Spacer(modifier = Modifier.width(2.dp))
*/
    }

}


@Composable
fun ConfigScreen(navController: NavHostController)
{
    Row(verticalAlignment = Alignment.CenterVertically,)
    {
        Button(onClick = { navController.navigate("LiveViewScreen") }) {
            Text(text = "Navigate next")
        }

        Common(
            text = "this is Config View",
            action = { navController.navigate("LiveViewScreen") }
        )

        Spacer(modifier = Modifier.width(12.dp))

    }
}

@Composable
fun Common(text: String, action: () -> Unit)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "TopAppBar") },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = action) {
                        Image(
                            painter = painterResource(R.drawable.a01f),
                            contentDescription = "sample0 picture",
                            modifier = Modifier
                                .clickable {
                                    Log.v(
                                        ViewRootComponent::class.java.simpleName,
                                        "onClick!"
                                    )
                                }
                                .size(80.dp, 80.dp)
                                .border(1.5.dp, MaterialTheme.colors.secondary)
                                .padding(4.dp)
                        )
                    }
                }
            )
        },
        content = {
            ShowText(text = text)
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = action,
                content = { Icon(Icons.Filled.Add, contentDescription = "") }
            )
        },
        bottomBar = {
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.background,
                cutoutShape = CircleShape
            ) {

            }
        }
    )
}

@Composable
fun ShowText(text: String) {
    Box(Modifier.fillMaxSize()) {
        Text(
            text = text,
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    start = 36.dp,
                    end = 36.dp
                )
        )
    }
}
