package br.com.fellipe.smsbroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import android.widget.Toast
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log


/**
 * Created by logonrm on 17/02/2018.
 */
class SMSBroadcastReceiver : BroadcastReceiver() {

    internal val sms = SmsManager.getDefault()

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras

        try {
            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<Any>
                for (i in pdusObj.indices) {

                    val currentMessage: SmsMessage

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val format = intent.getStringExtra("format")
                        currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray, format)
                    } else {
                        currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    }

                    val numeroTelefone = currentMessage.getDisplayOriginatingAddress()

                    val mensagem = currentMessage.getDisplayMessageBody()
                    Log.i("SmsReceiver", "senderNum: $numeroTelefone; message: $mensagem")

                    val i2 = Intent("android.intent.action.SMSRECEBIDO")
                            .putExtra("remetente", numeroTelefone)
                            .putExtra("mensagem", mensagem)
                    context.sendBroadcast(i2)

                    showNotification(context, numeroTelefone, mensagem)
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e)
        }

    }

    private fun showToast(context: Context, numeroTelefone: String, mensagem: String) {
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(context,
                "senderNum: $numeroTelefone, message: $mensagem", duration)
        toast.show()
    }

    private fun showNotification(context: Context, numeroTelefone: String, mensagem: String) {
        val mBuilder = NotificationCompat.Builder(context)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
        mBuilder.setContentTitle("Mensagem de: " + numeroTelefone)
        mBuilder.setContentText(mensagem)

        val resultIntent = Intent(context, MainActivity::class.java)

        resultIntent
                .putExtra("remetente", numeroTelefone)
                .putExtra("mensagem", mensagem)

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)


        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(resultPendingIntent)

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build())
    }
}