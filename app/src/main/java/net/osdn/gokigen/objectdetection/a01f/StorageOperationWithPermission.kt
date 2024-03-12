package net.osdn.gokigen.objectdetection.a01f

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.utils.imagefile.IImageStoreGrant
import jp.osdn.gokigen.gokigenassets.utils.IScopedStorageAccessPermission

@RequiresApi(api = Build.VERSION_CODES.R)
class StorageOperationWithPermission(private val activity: AppCompatActivity) : IScopedStorageAccessPermission
{
    override fun requestStorageAccessFrameworkLocation()
    {

    }

    override fun responseStorageAccessFrameworkLocation(resultCode: Int, data: Intent?)
    {

    }

    override fun requestAccessPermission(requestUri : Uri, grantResponse : IImageStoreGrant)
    {

    }

    override fun responseAccessPermission(resultCode: Int, data: Intent?)
    {

    }
}
