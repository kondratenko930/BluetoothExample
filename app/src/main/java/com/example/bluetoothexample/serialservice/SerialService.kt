package com.example.bluetoothexample.serialservice

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bluetoothexample.R

/**
 * create notification and queue serial data while activity is not in the foreground
 * use listener chain: SerialSocket -> SerialService -> UI fragment
 */
class SerialService : Service() {
    //, SerialListener {

    private val TAG_FOREGROUND_SERVICE  = "FOREGROUND_SERVICE"
    val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
    val ACTION_STOP_FOREGROUND_SERVICE  = "ACTION_STOP_FOREGROUND_SERVICE"
    val ACTION_PAUSE                    = "ACTION_PAUSE"
    val ACTION_PLAY                     = "ACTION_PLAY"

    override fun onTaskRemoved(rootIntent: Intent?) {
        println("onTaskRemoved called")
        super.onTaskRemoved(rootIntent)
        //do something you want
        //stop service
        this.stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onDestroy().")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra(Constants.INTENT_ACTION_START_SERVICE)
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
        val sentIntent        = Intent().setAction(Constants.INTENT_ACTION_SENT)
        val sentPendingIntent = PendingIntent.getBroadcast(this, 1, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

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
            .setContentTitle(getText(R.string.app_name))
            .setContentText(input.toString())
            .setContentIntent(restartPendingIntent)
            .setTicker(getText(R.string.title_devices))
            .setOngoing(true)
           .addAction(
                NotificationCompat.Action(
                    R.mipmap.barcode_scanner,
                    "Отправить",
                    sentPendingIntent
                )
            )
            .build()

        //3.
        startForeground(1, notification)
        //4.
        /*Service.START_STICKY перезапустится, если по какой-либо причине работа системы Android завершится.
        Service.START_NOT_STICKY будет работать до тех пор, пока не появятся незавершенные работы.
        Service.START_REDELIVER_INTENT похож на Service.START_STICKY, но исходное намерение повторно доставляется методу onStartCommand.
        */
        return START_STICKY
    }


    //    internal inner class SerialBinder : Binder() {
//        val service: SerialService
//            get() = this@SerialService
//    }
//
//    private enum class QueueType {
//        Connect, ConnectError, Read, IoError
//    }
//
//    private class QueueItem internal constructor(
//        var type: QueueType,
//        var data: ByteArray?,
//        var e: Exception?
//    )
//
//    private val mainLooper: Handler
//    private val binder: IBinder
//    private val queue1: Queue<QueueItem>
//    private val queue2: Queue<QueueItem>
//    private var socket: SerialSocket? = null
//    private var listener: SerialListener? = null
//    private var connected = false
//    override fun onDestroy() {
//        cancelNotification()
//        disconnect()
//        super.onDestroy()
//    }
//
//    override fun onBind(intent: Intent): IBinder? {
//        return binder
//    }
//
//    /**
//     * Api
//     */
//    @Throws(IOException::class)
//    fun connect(socket: SerialSocket) {
//        socket.connect(this)
//        this.socket = socket
//        connected = true
//    }
//
//    fun disconnect() {
//        connected = false // ignore data,errors while disconnecting
//        cancelNotification()
//        if (socket != null) {
//            socket!!.disconnect()
//            socket = null
//        }
//    }
//
//    @Throws(IOException::class)
//    fun write(data: ByteArray?) {
//        if (!connected) throw IOException("not connected")
//        socket!!.write(data)
//    }
//
//    fun attach(listener: SerialListener) {
//        require(!(Looper.getMainLooper().thread !== Thread.currentThread())) { "not in main thread" }
//        cancelNotification()
//        // use synchronized() to prevent new items in queue2
//        // new items will not be added to queue1 because mainLooper.post and attach() run in main thread
//        synchronized(this) { this.listener = listener }
//        for (item in queue1) {
//            when (item.type) {
//                QueueType.Connect -> listener.onSerialConnect()
//                QueueType.ConnectError -> listener.onSerialConnectError(item.e)
//                QueueType.Read -> listener.onSerialRead(item.data)
//                QueueType.IoError -> listener.onSerialIoError(item.e)
//            }
//        }
//        for (item in queue2) {
//            when (item.type) {
//                QueueType.Connect -> listener.onSerialConnect()
//                QueueType.ConnectError -> listener.onSerialConnectError(item.e)
//                QueueType.Read -> listener.onSerialRead(item.data)
//                QueueType.IoError -> listener.onSerialIoError(item.e)
//            }
//        }
//        queue1.clear()
//        queue2.clear()
//    }
//
//    fun detach() {
//        if (connected) createNotification()
//        // items already in event queue (posted before detach() to mainLooper) will end up in queue1
//        // items occurring later, will be moved directly to queue2
//        // detach() and mainLooper.post run in the main thread, so all items are caught
//        listener = null
//    }
//
//    private fun createNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val nc = NotificationChannel(
//                Constants.NOTIFICATION_CHANNEL,
//                "Background service",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            nc.setShowBadge(false)
//            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            nm.createNotificationChannel(nc)
//        }
//        val disconnectIntent = Intent()
//            .setAction(Constants.INTENT_ACTION_DISCONNECT)
//        val restartIntent = Intent()
//            .setClassName(this, Constants.INTENT_CLASS_MAIN_ACTIVITY)
//            .setAction(Intent.ACTION_MAIN)
//            .addCategory(Intent.CATEGORY_LAUNCHER)
//        val disconnectPendingIntent =
//            PendingIntent.getBroadcast(this, 1, disconnectIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val restartPendingIntent =
//            PendingIntent.getActivity(this, 1, restartIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setColor(resources.getColor(R.color.colorPrimary))
//            .setContentTitle(resources.getString(R.string.app_name))
//            .setContentText(if (socket != null) "Connected to " + socket!!.name else "Background Service")
//            .setContentIntent(restartPendingIntent)
//            .setOngoing(true)
//            .addAction(
//                NotificationCompat.Action(
//                    R.drawable.ic_clear_white_24dp,
//                    "Disconnect",
//                    disconnectPendingIntent
//                )
//            )
//        // @drawable/ic_notification created with Android Studio -> New -> Image Asset using @color/colorPrimaryDark as background color
//        // Android < API 21 does not support vectorDrawables in notifications, so both drawables used here, are created as .png instead of .xml
//        val notification = builder.build()
//        startForeground(Constants.NOTIFY_MANAGER_START_FOREGROUND_SERVICE, notification)
//    }
//
//    private fun cancelNotification() {
//        stopForeground(true)
//    }
//
//    /**
//     * SerialListener
//     */
//    override fun onSerialConnect() {
//        if (connected) {
//            synchronized(this) {
//                if (listener != null) {
//                    mainLooper.post {
//                        if (listener != null) {
//                            listener!!.onSerialConnect()
//                        } else {
//                            queue1.add(QueueItem(QueueType.Connect, null, null))
//                        }
//                    }
//                } else {
//                    queue2.add(QueueItem(QueueType.Connect, null, null))
//                }
//            }
//        }
//    }
//
//    override fun onSerialConnectError(e: Exception?) {
//        if (connected) {
//            synchronized(this) {
//                if (listener != null) {
//                    mainLooper.post {
//                        if (listener != null) {
//                            listener!!.onSerialConnectError(e)
//                        } else {
//                            queue1.add(QueueItem(QueueType.ConnectError, null, e))
//                            cancelNotification()
//                            disconnect()
//                        }
//                    }
//                } else {
//                    queue2.add(QueueItem(QueueType.ConnectError, null, e))
//                    cancelNotification()
//                    disconnect()
//                }
//            }
//        }
//    }
//
//    override fun onSerialRead(data: ByteArray?) {
//        if (connected) {
//            synchronized(this) {
//                if (listener != null) {
//                    mainLooper.post {
//                        if (listener != null) {
//                            listener!!.onSerialRead(data)
//                        } else {
//                            queue1.add(QueueItem(QueueType.Read, data, null))
//                        }
//                    }
//                } else {
//                    queue2.add(QueueItem(QueueType.Read, data, null))
//                }
//            }
//        }
//    }
//
//    override fun onSerialIoError(e: Exception?) {
//        if (connected) {
//            synchronized(this) {
//                if (listener != null) {
//                    mainLooper.post {
//                        if (listener != null) {
//                            listener!!.onSerialIoError(e)
//                        } else {
//                            queue1.add(QueueItem(QueueType.IoError, null, e))
//                            cancelNotification()
//                            disconnect()
//                        }
//                    }
//                } else {
//                    queue2.add(QueueItem(QueueType.IoError, null, e))
//                    cancelNotification()
//                    disconnect()
//                }
//            }
//        }
//    }
//
//    /**
//     * Lifecylce
//     */
//    init {
//        mainLooper = Handler(Looper.getMainLooper())
//        binder = SerialBinder()
//        queue1 = LinkedList()
//        queue2 = LinkedList()
//    }

}