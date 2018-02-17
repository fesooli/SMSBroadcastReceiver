package br.com.fellipe.smsbroadcastreceiver

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.Manifest.permission
import android.Manifest.permission.RECEIVE_SMS
import android.content.Context
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_sms.*


class SMSActivity : AppCompatActivity() {

    private var mReceiver: BroadcastReceiver? = null

    //private var tvMessage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        if (intent != null) {
            val i = intent
            val remetente = i.getStringExtra("remetente")
            val mensagem = i.getStringExtra("mensagem")
            tvMessage!!.text = if (remetente == null && mensagem == null) "" else remetente + " : " + mensagem
        }
        requestSmsPermission()
    }

    private fun requestSmsPermission() {
        val permission = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(1)
            permission_list[0] = permission
            ActivityCompat.requestPermissions(this, permission_list, 1)
        }
    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter(
                "android.intent.action.SMSRECEBIDO")

        mReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                //extract our message from intent
                val remetente = intent.getStringExtra("remetente")
                val mensagem = intent.getStringExtra("mensagem")
                tvMessage!!.text = remetente + " : " + mensagem

            }
        }
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        //unregister our receiver
        this.unregisterReceiver(this.mReceiver)
    }
}