package com.example.bluetoothexample.serialservice

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.bluetoothexample.R
import com.example.bluetoothexample.ui.dashboard.DashboardFragment.Companion.ACTION_DISABLE_DEVICE
import com.example.bluetoothexample.ui.dashboard.DashboardFragment.Companion.ACTION_SENT_DATA_TO_SERVER
import com.example.bluetoothexample.ui.dashboard.DashboardFragment.Companion.ACTION_START_FOREGROUND_SERVICE
import com.example.bluetoothexample.ui.dashboard.DashboardFragment.Companion.ACTION_STOP_FOREGROUND_SERVICE
import com.example.bluetoothexample.ui.dashboard.DashboardFragment.Companion.SERVICE_COMMAND
import com.example.bluetoothexample.ui.dashboard.DeviceConnect
import java.io.IOException


class SerialService : Service() , SerialListener {

    lateinit var  mac : String
    lateinit var  name : String

    private var socket: SerialSocket?       = null
    private var connected                   = false


    //тестовый обработчик action notification
    private fun showSuccessfulBroadcast(text:String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
    //notification action: отправка данных на сервер...
    private val sentDataToServerReceiver: SentDataToServerReceiver by lazy { SentDataToServerReceiver() }
    inner class SentDataToServerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.let{
                    ACTION_SENT_DATA_TO_SERVER-> showSuccessfulBroadcast("Отправка данных на сервер...")
            }
        }
    }
    //notification action: отключение устройства (остановка сервиса)...
    private val disableDeviceReceiver: DisableDeviceReceiver by lazy { DisableDeviceReceiver() }
    inner class DisableDeviceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.let{
                ACTION_DISABLE_DEVICE->stopForegroundService()
             }
        }
    }

    //1.
    override fun onCreate() {
        super.onCreate()
        registerReceiver(sentDataToServerReceiver, IntentFilter(ACTION_SENT_DATA_TO_SERVER))
        registerReceiver(disableDeviceReceiver, IntentFilter(ACTION_DISABLE_DEVICE))
    }

    //2.onDestroy
    override fun onDestroy() {
        unregisterReceiver(sentDataToServerReceiver)
        unregisterReceiver(disableDeviceReceiver)
        disconnect()
        super.onDestroy()
    }
    //3.биндер - не используем
    override fun onBind(intent: Intent): IBinder? = null

    /**
     * Api
     */
    //4.
    @Throws(IOException::class)
    open fun connect(socket: SerialSocket) {
        socket.connect(this)
        this.socket = socket
        connected = true
    }
    //5.
    fun disconnect() {
        connected = false // ignore data,errors while disconnecting
        if (socket != null) {
            socket!!.disconnect()
            socket = null
        }
    }

    private fun startForegroundService(mac:String,name:String) {
        this.mac=mac
        this.name=name
        //0.
        //Android O: Как использовать каналы уведомлений
        //https://code.tutsplus.com/ru/tutorials/android-o-how-to-use-notification-channels--cms-28616
        //Урок 190. Notifications. Каналы
        //https://startandroid.ru/ru/uroki/vse-uroki-spiskom/515-urok-190-notifications-kanaly.html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel(Constants.NOTIFICATION_CHANNEL,"Background service",NotificationManager.IMPORTANCE_HIGH)
            nc.setShowBadge(false)
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(nc)
        }
        //1.
        //Всё о PendingIntents
        //https://habr.com/ru/company/otus/blog/560492/
        val sentIntent        = Intent().setAction(ACTION_SENT_DATA_TO_SERVER)
        val sentPendingIntent = PendingIntent.getBroadcast(this, 1, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val disableIntent        = Intent().setAction(ACTION_DISABLE_DEVICE)
        val disablePendingIntent = PendingIntent.getBroadcast(this, 1, disableIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val restartIntent = Intent()
            .setClassName(this, Constants.INTENT_CLASS_MAIN_ACTIVITY)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val restartPendingIntent =
            PendingIntent.getActivity(this, 1, restartIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        //2.
        val notification: Notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
            .setSmallIcon(R.mipmap.barcode_scanner)
            .setColor(resources.getColor(R.color.colorPrimary))
            .setContentTitle(name)
            .setContentText(mac)
            .setContentIntent(restartPendingIntent)
            .addAction(
                NotificationCompat.Action(
                    R.mipmap.barcode_scanner,
                    "Отправить данные на сервер",
                    sentPendingIntent
                )
            )
            .addAction(
                NotificationCompat.Action(
                    R.mipmap.barcode_scanner,
                    "Отключить устройство",
                    disablePendingIntent
                )
            )
            .build()

        //3.старт сервиса
        startForeground(1, notification)

        //4.connect BT Device (в исходнике этот код находится в TerminalFragment)
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(mac)
            val socket = SerialSocket(this, device)
            connect(socket)
        } catch (e: Exception) {
            onSerialConnectError(e)
        }
   }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val deviceConnect = intent?.getParcelableExtra<DeviceConnect>(SERVICE_COMMAND)

        when (deviceConnect?.action) {
            ACTION_START_FOREGROUND_SERVICE  -> startForegroundService(deviceConnect.mac,deviceConnect.name)
            ACTION_STOP_FOREGROUND_SERVICE   -> stopForegroundService()
            else -> return START_NOT_STICKY
        }
        return START_STICKY
    }

    /**
         * SerialListener
    */
    override fun onSerialConnect() {
        //if (connected) {
            //@Synchronized {
            //synchronized (this) {
                //Toast.makeText(this, "connected", Toast.LENGTH_LONG).show()
             //тут установить статус в ROOM
            //}
        //}
    }

    override fun onSerialConnectError(e: java.lang.Exception?) {
        Toast.makeText(this, e?.message, Toast.LENGTH_LONG).show()
        //TODO("Not yet implemented")
    }

    override fun onSerialRead(data: ByteArray?) {
 //       if (connected) {
 //           synchronized(this) {
 //               if (listener != null)
//                {
//                    mainLooper.post(Runnable {
//                        if (listener != null) {
//                            listener.onSerialRead(data)
//                        } else {
//                            queue1.add(QueueItem(SerialService.QueueType.Read, data, null))
//                        }
//                    })
//                } else {
//                    queue2.add(QueueItem(SerialService.QueueType.Read, data, null))
//                }
//            }
//        }
    }

    override fun onSerialIoError(e: java.lang.Exception?) {
        //TODO("Not yet implemented")
    }

    inner class  TestException(message:String): Exception(message)

}