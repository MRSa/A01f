package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
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
                NavigationMain(navController, changeScene.getCameraControl(), changeScene.getVibrator())
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
fun NavigationMain(navController: NavHostController, cameraControl: ICameraControl, vibrator : IVibrator)
{
    GokigenComposeAppsTheme {
        NavHost(navController = navController, startDestination = "LiveViewScreen") {
            composable("LiveViewScreen") { LiveViewScreen(navController = navController, cameraControl, vibrator) }
            composable("PreferenceScreen") { PreferenceScreen(navController = navController) }
        }
    }
}
