package com.zkb.luck.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
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
    private val TAG = "kang"
    @Volatile
    private var count = 1

    /**
     * 0 默认
     * 1 处理中
     * 2 发消息中
     */
    @Volatile
    private var runningType = 0
    @Volatile
    private var maxSendMessageCount = 3

    /**
     * 默认最多发送条数
     */
    @Volatile
    private var defaultMaxSendMessageCount = 5
    @Volatile
    private var sleepTime = 1000f
    private var isStart = false;
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.d(TAG, "AccessibilityEvent WINDOW_STATE change:")
                listenerInputCmd();
                if (!isStart) return
                if (runningType != 0) return

                startFireworks();

            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Log.d(TAG, "AccessibilityEvent 内容change:")
                listenerInputCmd()
                if (!isStart) return
                if (runningType != 0) return

                startFireworks();
            }
        }

    }

    /**
     * 监听输入框，把输入框文字作为命令。
     */
    @Synchronized
    private fun listenerInputCmd() {

        if (isStart) return

        val editText = findInput() ?: return

        if (editText.text.isNullOrEmpty()) return

        if ("开始放烟花啦" != editText.text.toString()) {
            Log.d(TAG, "AccessibilityEvent editText1:" + editText.text)

            return
        }

        Log.d(TAG, "AccessibilityEvent editText2:" + editText.text)
        //寻找发送按钮
        val sendBtn = findSendButton() ?: return

        //把消息发出去，然后开始！
        val isSuccess = sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        if (!isSuccess) return

        reSet()

        isStart = true
        // Log.d(TAG,"AccessibilityEvent editText2:"+editText.text)
    }


    /**
     * 开始放烟火
     */
    @Synchronized
    private fun startFireworks() {

        if (count > maxSendMessageCount) {
            Log.d(TAG, "---------------stop-----:")
            return
        }


        runningType = 1
        //寻找输入框
        val editText = findInput()

        if (editText == null) {
            runningType = 0
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
        val sendBtn = findSendButton()
        if (sendBtn == null) {
            runningType = 0
            return
        }
        if (!isStart) return
        //判断是否发送成功！
        val isSendSuccess = sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if (isSendSuccess) {
            //成功就进入循环发送消息
            try {
                sendMsg(sendBtn)
            } catch (e: Exception) {
                Log.d(TAG, "startFireworks: //进入循环Exception:" + e.localizedMessage)
            }

            return
        }

    }

    /**
     * 发送消息
     */
    private fun sendMsg(sendBtn: AccessibilityNodeInfo?) {

        if (runningType == 2) return

        //最大次数
        if (count >= maxSendMessageCount) {
            Log.d(TAG, "---------------stop-----:")
            return
        }

        Log.d(TAG, "startFireworks: //进入循环" + count)
        runningType = 2
        //进入循环
        while (count < maxSendMessageCount) {

            if (!isStart) return
            Thread.sleep(sleepTime.toLong())

            val isSuccess = sendBtn?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d(TAG, "startFireworks: //ing$count--- $isSuccess")

            if (isSuccess!!) {
                count++
            }
            val arguments = Bundle()
            //输入框中输入礼花
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                "[庆祝]"
            )
            val editText = findInput()

            if (editText == null) {
                runningType = 0
                return
            }

            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)


        }
        Log.d(TAG, "startFireworks: //结束" + count)
        //重置参数
        reSet()

    }

    /**
     * 重置参数
     */
    private fun reSet() {
        count = 0//可以用注释这里，到达次数就自动结束。
        runningType = 0;
        isStart = false//停止,接着继续监控命令
    }

    /**
     * 查找发送消息按钮
     */
    private fun findSendButton(): AccessibilityNodeInfo? {
        //消息发送按钮的节点。
        val sendButton =
            rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ay5")
        if (sendButton.isNullOrEmpty()) {
            return null
        }
        sendButton.forEach {
            //匹配button
            if (it.className == "android.widget.Button") {
                Log.d(TAG, "found send button!")
                return it
            }
        }
        return null

    }

    /**
     * 查找输入框
     */
    private fun findInput(): AccessibilityNodeInfo? {
        //查找节点
        val input = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/auj")
        if (input.isNullOrEmpty()) {
            return null
        }

        if (input[input.size - 1].className != "android.widget.EditText") {
            return null
        }

        return input[input.size - 1]

    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        /**
         * 读取配置
         */
        val info = getSharedPreferences("info", Context.MODE_PRIVATE)

        sleepTime = info.getFloat("edtSecond", 2f) * 1000
        maxSendMessageCount = info.getInt("edtMsgCount", defaultMaxSendMessageCount)
        Log.d(TAG, "onServiceConnected:$maxSendMessageCount")

    }

    override fun onInterrupt() {

    }

    /*    */
    /**
     * 奇怪拿不到聊天记录文本，难道text是绘制出来的？求解一下
     *//*
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
    }*/
}