package net.osdn.gokigen.objectdetection.a01f

import android.Manifest
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.gokigenassets.scene.ShowMessage
import net.osdn.gokigen.objectdetection.a01f.preference.A01fPrefsModel
import net.osdn.gokigen.objectdetection.a01f.preference.PreferenceValueInitializer
import net.osdn.gokigen.objectdetection.a01f.liaison.CameraLiaison
import net.osdn.gokigen.objectdetection.a01f.ui.view.ViewRootComponent

class MainActivity : AppCompatActivity(), IVibrator, ICameraStatusReceiver
{
    //private val accessPermission : IScopedStorageAccessPermission? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { StorageOperationWithPermission(this) } else { null }
    private val showMessage : ShowMessage = ShowMessage(this)
    private lateinit var cameraLiaison : CameraLiaison
    private lateinit var rootComponent : ViewRootComponent
    private lateinit var a01fPrefs : A01fPrefsModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        try
        {
            ///////// SHOW SPLASH SCREEN /////////
            installSplashScreen()

            ///////// INITIALIZATION /////////
            cameraLiaison  = CameraLiaison(this, showMessage, this, this)
            PreferenceValueInitializer().initializePreferences(this)
            a01fPrefs = ViewModelProvider(this)[A01fPrefsModel::class.java]
            a01fPrefs.initializePreferences(this)

            ///////// SET ROOT VIEW /////////
            rootComponent = ViewRootComponent(applicationContext)
            rootComponent.setLiaisons(cameraLiaison, a01fPrefs)
            setContent {
                rootComponent.Content()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        Log.v(TAG, "...MainActivity...")

        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        try
        {
            ///////// SET PERMISSIONS /////////
            if (!allPermissionsGranted())
            {
                val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                    if(!allPermissionsGranted())
                    {
                        // Abort launch application because required permissions was rejected.
                        Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                        Log.v(TAG, "----- APPLICATION LAUNCH ABORTED -----")
                        finish()
                    }
                    else
                    {
                        checkMediaWritePermission()
                        cameraLiaison.initialize()
                    }
                }
                requestPermission.launch(REQUIRED_PERMISSIONS)
            }
            else
            {
                checkMediaWritePermission()
                cameraLiaison.initialize()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        cameraLiaison.finish()
    }

    private fun allPermissionsGranted() : Boolean
    {
        var result = true
        for (param in REQUIRED_PERMISSIONS)
        {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    param
                ) != PackageManager.PERMISSION_GRANTED
            )
            {
                // Permission Denied...
                if ((param == Manifest.permission.ACCESS_MEDIA_LOCATION)&&(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q))
                {
                    //　この場合は権限付与の判断を除外 (デバイスが (10) よりも古く、ACCESS_MEDIA_LOCATION がない場合）
                }
                else if ((param == Manifest.permission.READ_EXTERNAL_STORAGE)&&(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))
                {
                    // この場合は、権限付与の判断を除外 (SDK: 33以上はエラーになる...)
                }
                else if ((param == Manifest.permission.WRITE_EXTERNAL_STORAGE)&&(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))
                {
                    // この場合は、権限付与の判断を除外 (SDK: 33以上はエラーになる...)
                }
                else
                {
                    // ----- 権限が得られなかった場合...
                    Log.v(TAG, " Permission: $param : ${Build.VERSION.SDK_INT}")
                    result = false
                }
            }
        }
        return (result)
    }

    private fun checkMediaWritePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            StorageOperationWithPermission(this).requestStorageAccessFrameworkLocation()
        }
    }

/*
    override fun onResume()
    {
        super.onResume()
    }

    override fun onPause()
    {
        super.onPause()
    }
*/

/*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS)
        {
            if (allPermissionsGranted())
            {
                checkMediaWritePermission()
                cameraLiaison.initialize()
            }
            else
            {
                Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
*/
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MEDIA_EDIT)
        {
            accessPermission?.responseAccessPermission(resultCode, data)
        }
        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT_TREE)
        {
            accessPermission?.responseStorageAccessFrameworkLocation(resultCode, data)
        }
    }
*/

    override fun vibrate(vibratePattern: IVibrator.VibratePattern)
    {
        try
        {
            // バイブレータをつかまえる
            val vibrator  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                val vibratorManager =  this.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            }
            else
            {
                @Suppress("DEPRECATION")
                getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
            if (!vibrator.hasVibrator())
            {
                Log.v(TAG, " not have Vibrator...")
                return
            }
            @Suppress("DEPRECATION") val thread = Thread {
                try
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    else
                    {
                        when (vibratePattern)
                        {
                            IVibrator.VibratePattern.SIMPLE_SHORT_SHORT -> vibrator.vibrate(30)
                            IVibrator.VibratePattern.SIMPLE_SHORT ->  vibrator.vibrate(50)
                            IVibrator.VibratePattern.SIMPLE_MIDDLE -> vibrator.vibrate(100)
                            IVibrator.VibratePattern.SIMPLE_LONG ->  vibrator.vibrate(150)
                            else -> { }
                        }
                    }
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onStatusNotify(message: String?)
    {
        Log.v(TAG, " onStatusNotify() $message ")
        updateConnectionIcon(ICameraConnectionStatus.CameraConnectionStatus.CONNECTING)
    }

    override fun onCameraConnected()
    {
        Log.v(TAG, " onCameraConnected() ")
        updateConnectionIcon(ICameraConnectionStatus.CameraConnectionStatus.CONNECTED)
    }

    override fun onCameraDisconnected()
    {
        Log.v(TAG, " onCameraDisconnected() ")
        updateConnectionIcon(ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED)
    }

    override fun onCameraConnectError(msg: String?)
    {
        Log.v(TAG, " onCameraConnectError() $msg ")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        try
        {
            if (event.action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_CAMERA))
            {
                Log.v(TAG, "onKeyDown() $keyCode")
                if (::cameraLiaison.isInitialized)
                {
                    return (cameraLiaison.handleKeyDown(keyCode, event))
                }
            }
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun updateConnectionIcon(connectionStatus : ICameraConnectionStatus.CameraConnectionStatus)
    {
        Log.v(TAG, " updateConnectionIcon() $connectionStatus")
        try
        {
            a01fPrefs.setCameraConnectionStatus(connectionStatus)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = MainActivity::class.java.simpleName

        private const val REQUEST_CODE_PERMISSIONS = 10
        //const val REQUEST_CODE_MEDIA_EDIT = 12
        //const val REQUEST_CODE_OPEN_DOCUMENT_TREE = 20

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
        )
    }
}
