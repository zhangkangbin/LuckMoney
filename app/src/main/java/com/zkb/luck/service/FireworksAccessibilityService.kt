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
    @Volatile private var count=0
    @Volatile private var running = false
    @Volatile private var maxSendMessageCount=3
    private var isStart=false;
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
               // find()
                if(running) return
                listenerCmd()
                Log.d(TAG,"AccessibilityEvent WINDOW_STATE change:")
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Log.d(TAG,"AccessibilityEvent 内容change:")
                if(running) return
                listenerCmd()
                if(!isStart) return
                startFireworks();
            }
        }

    }


    /**
     * 奇怪拿不到聊天记录文本，难道text是绘制出来的？
     */
    private fun listenerCmd(){
        if(running) return
        val list= rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/awv")
            ?: return

        val last=list.last();

        if(last!=null){
            isStart=true;
            //last
            Log.d(TAG,"last:"+last.text)
        }
    }

    private fun  startFireworks(){
        if(running) return
        if(count>maxSendMessageCount) return
        Log.d(TAG,"startFireworks:")

        //寻找输入框
        val  editText= findInput() ?: return

        val arguments = Bundle()
        //输入框中输入礼花
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            "[庆祝]"
        )
        editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

        //寻找发送按钮
        val sendBtn=findSendButton() ?: return

        //判断是否发送成功！
        val isSendSuccess= sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if(isSendSuccess){
            //成功就进入循环发送消息
            sendMsg(sendBtn)
           return
        }

    }

    /**
     * 发送消息
     */
    @Synchronized  private  fun sendMsg(sendBtn : AccessibilityNodeInfo?){
        if(running) return
        Log.d(TAG,"startFireworks: //进入循环"+count)
        running=true
        //进入循环
        while (count<maxSendMessageCount){
            Thread.sleep(2000)
            sendBtn?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            count++
        }
        Log.d(TAG,"startFireworks: //结束"+count)
        count=0
        running=false;
    }

    /**
     * 查找发送消息按钮
     */
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

    /**
     * 查找输入框
     */
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