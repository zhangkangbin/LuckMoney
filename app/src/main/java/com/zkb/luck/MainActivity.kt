package com.zkb.luck

import android.app.backup.SharedPreferencesBackupHelper
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun  initView(){
        findViewById<View>(R.id.open).setOnClickListener {
            val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(accessibleIntent)
        }
        val edtSecond=findViewById<EditText>(R.id.edtSecond)
        val edtMsgCount=findViewById<EditText>(R.id.edtMsgCount)

        val edit = getSharedPreferences("info",Context.MODE_PRIVATE).edit()

        findViewById<View>(R.id.saveInfo).setOnClickListener {

            if(edtSecond.text.isNullOrEmpty()) return@setOnClickListener
            if(edtMsgCount.text.isNullOrEmpty()) return@setOnClickListener
            edit.putInt("edtSecond",edtSecond.text.toString().toInt())
            edit.putInt("edtMsgCount",edtMsgCount.text.toString().toInt())
            edit.apply()

            Toast.makeText(this,"保存成功！",Toast.LENGTH_SHORT).show()

       }
    }
}