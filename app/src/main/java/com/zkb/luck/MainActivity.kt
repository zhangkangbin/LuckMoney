package com.zkb.luck

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity()  {
    var openBtn:Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onResume() {
        super.onResume()
        if(isServiceEnabled()){
            openBtn?.text = "已开启"
        }
    }
    private fun  initView(){

        openBtn=  findViewById<Button>(R.id.open)

        if(isServiceEnabled()){
            openBtn?.text = "已开启"
        }
        openBtn?.setOnClickListener {
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

    /**
     * 服务是否启用状态
     *
     * @return
     */
    private fun isServiceEnabled(): Boolean {
        val accessibilityManager =  getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager;
        val accessibilityServices: List<AccessibilityServiceInfo> =
            accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (info in accessibilityServices) {
           // Log.d("kang",info.id)
            if (info.id == "$packageName/.service.FireworksAccessibilityService") {
                return true
            }
        }
        return false
    }
}