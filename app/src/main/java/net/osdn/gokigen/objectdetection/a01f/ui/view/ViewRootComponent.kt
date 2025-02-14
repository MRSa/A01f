package net.osdn.gokigen.objectdetection.a01f.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.liveview.LiveViewOnTouchListener
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import net.osdn.gokigen.objectdetection.a01f.preference.A01fPrefsModel
import net.osdn.gokigen.objectdetection.a01f.liaison.CameraLiaison
import net.osdn.gokigen.objectdetection.a01f.ui.theme.GokigenComposeAppsTheme

class ViewRootComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AbstractComposeView(context, attrs, defStyleAttr)
{
    private lateinit var liaison: CameraLiaison
    private lateinit var prefsModel : A01fPrefsModel

    fun setLiaisons(liaison: CameraLiaison, prefsModel : A01fPrefsModel)
    {
        this.liaison = liaison
        this.prefsModel = prefsModel
        Log.v(TAG, " ...setLiaisons...")
    }

    @Composable
    override fun Content()
    {
        val navController = rememberNavController()

        GokigenComposeAppsTheme {
            //Surface(color = MaterialTheme.colors.background) {
                NavigationMain(navController, liaison.getCameraControl(), liaison.getVibrator(), prefsModel, liaison.getAnotherDrawer())
            //}
        }
        Log.v(TAG, " ...NavigationRootComponent...")
    }

    companion object
    {
        private val TAG = ViewRootComponent::class.java.simpleName
    }
}

@Composable
fun NavigationMain(navController: NavHostController, cameraControl: ICameraControl, vibrator: IVibrator, prefsModel : A01fPrefsModel, anotherDrawer: IAnotherDrawer)
{
    GokigenComposeAppsTheme {
        NavHost(
            modifier = Modifier.systemBarsPadding(),
            navController = navController,
            startDestination = "LiveViewScreen"
        ) {
            composable("LiveViewScreen") { LiveViewScreen(navController = navController, cameraControl, prefsModel, vibrator, LiveViewOnTouchListener(cameraControl), anotherDrawer) }
            composable("PreferenceScreen") { PreferenceScreen(navController = navController, prefsModel, vibrator) }
        }
    }
}
