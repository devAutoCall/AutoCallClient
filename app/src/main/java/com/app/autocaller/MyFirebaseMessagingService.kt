package com.app.autocaller

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseTest"

    // 메세지가 수신되면 호출
    @SuppressLint("InvalidWakeLockTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("MyFirebaseMessagingService", "MyFirebaseMessagingService onMessageReceived")
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK
                    or PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG"
        )
        wakeLock.acquire(3000)

        callPhone(remoteMessage.data["message"].toString())

        //sendNotification(remoteMessage.data["message"].toString(), remoteMessage.data["message"].toString())
    }

    private fun callPhone(message: String) {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                System.out.println("callPhone onPermissionGranted")
                val myUri = Uri.parse("tel:${message}")
                val myIntent = Intent(Intent.ACTION_CALL, myUri)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.addFlags(Intent.FLAG_FROM_BACKGROUND)
                startActivity(myIntent)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                //Toast.makeText(this@MainActivity, "전화 연결 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("[설정] 에서 권한을 열어줘야 전화 연결이 가능합니다.")
            .setPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.INTERNET,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            )
            .check()

    }

    // Firebase Cloud Messaging Server 가 대기중인 메세지를 삭제 시 호출
    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    // 메세지가 서버로 전송 성공 했을때 호출
    override fun onMessageSent(p0: String) {

        super.onMessageSent(p0)
    }

    // 메세지가 서버로 전송 실패 했을때 호출
    override fun onSendError(p0: String, p1: Exception) {
        super.onSendError(p0, p1)
    }

    // 새로운 토큰이 생성 될 때 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendNotification(title: String?, body: String) {
        Log.d("MyFirebaseMessagingService", "MyFirebaseMessagingService sendNotification")
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 액티비티 중복 생성 방지
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        ) // 일회성

        val channelId = "com.app.autocaller" // 채널 아이디
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 소리
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title) // 제목
            .setContentText(body) // 내용
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 예외처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "com.app.autocaller",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build()) // 알림 생성
    }

    // 받은 토큰을 서버로 전송
    private fun sendRegistrationToServer(token: String) {

    }
}