package com.zkb.luck.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.zkb.luck.R

/**
 * fireworks
 * 许你一世烟火
 * @author:zhangkb
 * Date:2021/1/29
 */
class FireworksAccessibilityService : AccessibilityService() {
    private val TAG="kang"
    @Volatile private var count=0
    /**
     * 0 默认
     * 1 处理中
     * 2 发消息中
     */
    @Volatile private var runningType = 0
    @Volatile private var maxSendMessageCount=3
    @Volatile private var sleepTime=1000
    private var isStart=false;
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if(runningType!=0) return
                startFireworks();
                Log.d(TAG,"AccessibilityEvent WINDOW_STATE change:")
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if(runningType!=0) return
                Log.d(TAG,"AccessibilityEvent 内容change:")
                startFireworks();
            }
        }

    }


    /**
     * 奇怪拿不到聊天记录文本，难道text是绘制出来的？
     */
    private fun listenerCmd(){
       // if(running) return
        val list= rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/awv")
            ?: return

        if(list.isEmpty()) return

        val last=list.last();

        if(last!=null){
            isStart=true;
            //last
            Log.d(TAG,"last:"+last.text)
        }
    }

    @Synchronized  private fun  startFireworks(){

        if(count>maxSendMessageCount) {
            Log.d(TAG,"---------------stop-----:")
            return
        }


        runningType=1
        //寻找输入框
        val  editText= findInput()

        if(editText == null){
            runningType=0
            return
        }


        val arguments = Bundle()
        //输入框中输入礼花
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            "[庆祝]"
        )
        editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

        //寻找发送按钮
        val sendBtn=findSendButton()
        if(sendBtn == null){
            runningType=0
            return
        }

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
     private  fun sendMsg(sendBtn : AccessibilityNodeInfo?){

        if(runningType==2) return

        if(count>=maxSendMessageCount) {
            Log.d(TAG,"---------------stop-----:")
            return
        }

        Log.d(TAG,"startFireworks: //进入循环"+count)
        runningType=2
        //进入循环
        while (count<maxSendMessageCount){
            Thread.sleep(sleepTime.toLong())

            val isSuccess=sendBtn?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d(TAG, "startFireworks: //ing$count--- $isSuccess")

            if(isSuccess!!){
                count++
            }
            val arguments = Bundle()
            //输入框中输入礼花
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                "[庆祝]"
            )
            val  editText= findInput()

            if(editText == null){
                runningType=0
                return
            }

            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)


        }
        Log.d(TAG,"startFireworks: //结束"+count)
      //  count=0//可以用注释这里，到达次数就自动结束。
        runningType=0;
    }

    /**
     * 查找发送消息按钮
     */
    private fun findSendButton(): AccessibilityNodeInfo? {
        val sendButton=  rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ay5")
        if(sendButton.isNullOrEmpty()){
            return null
        }
        sendButton.forEach {
            if(it.className=="android.widget.Button"){
                Log.d(TAG,"found send button!")
                return it
            }
        }
        return null

    }

    /**
     * 查找输入框
     */
    private fun  findInput():AccessibilityNodeInfo?{
       val input=  rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/auj")
        if(input.isNullOrEmpty()){
            return null
        }

        if(input[input.size-1].className != "android.widget.EditText"){
            return null
        }

        return  input[input.size-1]

    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        /**
         * 读取配置
         */
        val info = getSharedPreferences("info",Context.MODE_PRIVATE)

        sleepTime= info.getInt("edtSecond",2)*1000
        maxSendMessageCount=info.getInt("edtMsgCount",10)
        Log.d(TAG, "onServiceConnected:$maxSendMessageCount")

        }

    override fun onInterrupt() {

    }
}