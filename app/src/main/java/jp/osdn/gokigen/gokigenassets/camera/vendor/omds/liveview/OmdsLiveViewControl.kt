package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.liveview

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ILiveViewController
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.status.OmdsCameraStatusWatcher
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageDataReceiver
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.*

class OmdsLiveViewControl(private val imageDataReceiver: IImageDataReceiver,
                          private val statusWatcher: OmdsCameraStatusWatcher,
                          userAgent: String = "OlympusCameraKit",
                          private val executeUrl : String = "http://192.168.0.10",
                          ) : ILiveViewController, IOmdsLiveViewControl
{
    private val headerMap: MutableMap<String, String> = HashMap()
    private val http = SimpleHttpClient()
    private val receivedByteStream = ByteArrayOutputStream(RECEIVE_BUFFER_SIZE)

    private var receiveSocket: DatagramSocket? = null
    private var whileStreamReceive = false

    override fun startLiveView(isCameraScreen: Boolean)
    {
        Log.v(TAG, "startLiveView()")
        try
        {
            val thread = Thread(Runnable {
                try
                {
                    startReceiveStream()
                    if (!whileStreamReceive) {
                        Log.v(TAG, "CANNOT OPEN : UDP RECEIVE SOCKET")
                        return@Runnable
                    }
                    val requestUrl = executeUrl + LIVEVIEW_START_REQUEST
                    val reply: String = http.httpGetWithHeader(requestUrl, headerMap, null, TIMEOUT_MS) ?: ""
                    Log.v(TAG, "   ----- START LIVEVIEW ----- : $requestUrl $reply")
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            })
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun stopLiveView()
    {
        Log.v(TAG, "stopLiveView()")
        try
        {
            val thread = Thread {
                try
                {
                    val requestUrl = executeUrl + LIVEVIEW_STOP_REQUEST
                    val reply: String = http.httpGetWithHeader(requestUrl, headerMap, null, TIMEOUT_MS) ?: ""
                    Log.v(TAG, "stopLiveview() is issued.  $reply")

                    //  ライブビューウォッチャーを止める
                    whileStreamReceive = false
                    closeReceiveSocket()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
            statusWatcher.stopStatusWatch()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun startReceiveStream()
    {
        if (whileStreamReceive)
        {
            Log.v(TAG, "startReceiveStream() : already starting.")
            return
        }

        // ソケットをあける (UDP)
        try
        {
            receiveSocket = DatagramSocket(LIVEVIEW_PORT)
            whileStreamReceive = true
            receivedByteStream.flush()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            whileStreamReceive = false
            receiveSocket = null
        }

        // 受信スレッドを動かす
        val thread = Thread { receiverThread() }
        try
        {
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkReceiveImage(packet: DatagramPacket)
    {
        try
        {
            val dataLength = packet.length
            val receivedData = packet.data
            if (receivedData == null)
            {
                // 受信データが取れなかったのでいったん終了する
                Log.v(TAG, "RECEIVED DATA IS NULL...")
                return
            }
            val position = 12
            var extensionLength = 0
            var isFinished = false
            if (receivedData[0] == 0x90.toByte())
            {
                // 先頭パケット (RTPヘッダは 12bytes + 拡張ヘッダ...)
                extensionLength = 16
                extensionLength = checkJpegStartPosition(receivedData, extensionLength) - position
                statusWatcher.setRtpHeader(Arrays.copyOf(receivedData, extensionLength))
                System.gc()
            } else if (receivedData[1] == 0xe0.toByte())
            {
                // 末尾パケット (RTPヘッダは 12bytes)
                isFinished = true
            }
            val offset = position + extensionLength
            receivedByteStream.write(receivedData, position + extensionLength, dataLength - offset)
            if (isFinished)
            {
                val dataArray = receivedByteStream.toByteArray()
                receivedByteStream.flush()
                imageDataReceiver.onUpdateLiveView(Arrays.copyOf(dataArray, dataArray.size), null)
                receivedByteStream.reset()
                //System.gc();
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkJpegStartPosition(bytes: ByteArray, offset: Int): Int
    {
        try
        {
            var position = offset
            val maxLength = bytes.size - 1
            while (position < maxLength)
            {
                if (bytes[position] == 0xff.toByte())
                {
                    if (bytes[position + 1] == 0xd8.toByte())
                    {
                        return position
                    }
                }
                position++
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return 0
    }

    private fun receiverThread()
    {
        var exceptionCount = 0
        val buffer = ByteArray(RECEIVE_BUFFER_SIZE)
        while (whileStreamReceive)
        {
            try
            {
                val receivePacket = DatagramPacket(buffer, buffer.size)
                if (receiveSocket != null)
                {
                    receiveSocket?.soTimeout = TIMEOUT_MS
                    receiveSocket?.receive(receivePacket)
                    checkReceiveImage(receivePacket)
                    exceptionCount = 0
                }
                else
                {
                    Log.v(TAG, "receiveSocket is NULL...")
                }
            }
            catch (e: Exception)
            {
                exceptionCount++
                e.printStackTrace()
                if (exceptionCount > TIMEOUT_MAX)
                {
                    try
                    {
                        //  ライブビューの送信が来なくなった... それも回数が超えた...
                        Log.v(TAG, "LV : RETRY REQUEST")
                        exceptionCount = 0
                    }
                    catch (ee: Exception)
                    {
                        ee.printStackTrace()
                    }
                }
            }
        }
        closeReceiveSocket()
        Log.v(TAG, "  ----- startReceiveStream() : Finished.")
        System.gc()
    }

    private fun closeReceiveSocket()
    {
        Log.v(TAG, "closeReceiveSocket()")
        try
        {
            if (receiveSocket != null)
            {
                Log.v(TAG, "  ----- SOCKET CLOSE -----  ")
                receiveSocket?.close()
                receiveSocket = null
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startOmdsLiveView()
    {
        startLiveView()
    }

    override fun stopOmdsLiveView()
    {
        stopLiveView()
    }

    init
    {
        headerMap["User-Agent"] = userAgent // "OlympusCameraKit" // "OI.Share"
        headerMap["X-Protocol"] = userAgent // "OlympusCameraKit" // "OI.Share"
    }

    companion object
    {
        private val TAG = OmdsLiveViewControl::class.java.simpleName

        private const val LIVEVIEW_START_REQUEST = "/exec_takemisc.cgi?com=startliveview&port=49152"
        private const val LIVEVIEW_STOP_REQUEST = "/exec_takemisc.cgi?com=stopliveview"

        private const val TIMEOUT_MAX = 3
        private const val RECEIVE_BUFFER_SIZE = 1024 * 1024 * 4
        private const val TIMEOUT_MS = 1500
        private const val LIVEVIEW_PORT = 49152
    }

}
