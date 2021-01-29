package com.zkb.luck.service

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * fireworks
 * 许你一世烟火
 * @author:zhangkb
 * Date:2021/1/29
 */
class FireworksAccessibilityService : AccessibilityService() {

    private val TAG="kang"
    private var count=0
    private var maxSendMessageCount=10

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
               // find()
                Log.d(TAG,"AccessibilityEvent WINDOW_STATE change:")
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Log.d(TAG,"AccessibilityEvent 内容change:")
                startFireworks();
            }
        }

    }


    private fun listenerCmd(){

    }

    private fun  startFireworks(){

        if(count>maxSendMessageCount) return
        Log.d(TAG,"startFireworks:")

        val  editText= findInput() ?: return

        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            "[庆祝]"
        )
        editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

        val sendBtn=findSendButton() ?: return

        val isSendSuccess= sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if(!isSendSuccess){
           return
        }
        //进入循环
        while (count>maxSendMessageCount){
            Thread.sleep(2000)
            sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            count++
        }
    }

    private fun findSendButton(): AccessibilityNodeInfo? {

        val sendButton=  rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ay5")
        if(sendButton.isNotEmpty()){

            sendButton.forEach {
                if(it.className=="android.widget.Button"){
                    Log.d(TAG,"found send button!")
                    return it
                }
            }

        }

        return null

    }
    private fun  findInput():AccessibilityNodeInfo?{
       val input=  rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/auj")
        if(input.isNotEmpty()){

            if(input[input.size-1].className != "android.widget.EditText"){
                return null
            }

            return  input[input.size-1]
        }
        return null
    }


    override fun onInterrupt() {

    }
}