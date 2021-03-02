package com.zkb.luck.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 豆瓣回帖
 * @author:zhangkb
 * Date:2021/3/2
 */
class DouBanAccessibilityService : AccessibilityService() {
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
    private var maxSendMessageCount = 3000

    /**
     * 默认最多发送条数
     */
    @Volatile
    private var defaultMaxSendMessageCount = 30000
    @Volatile
    private var sleepTime = 1000f
    private var isStart = false;
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {

                listenerInputCmd();
                if (!isStart) return
                if (runningType != 0) return
                Log.d(TAG, "AccessibilityEvent WINDOW_STATE change:")
                startFireworks();

            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {

                listenerInputCmd()
                if (!isStart) return
                if (runningType != 0) return
                Log.d(TAG, "AccessibilityEvent 内容change:")
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

        isStart = true
    }


    /**
     * 开始放烟火
     */
    @Synchronized
    private fun startFireworks() {

  /*      if (count > maxSendMessageCount) {
            Log.d(TAG, "---------------stop-----:")
            return
        }*/


        runningType = 1
        //寻找输入框
        val editText = findInput()

        if (editText == null) {
            runningType = 0
            return
        }

        Log.d(TAG, "---------------found input-----:$count")


        //点击弹起键盘
        val isSuccess = editText.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if(!isSuccess){
            //流程回归
            runningType = 0
        }


        try {
            //一秒
            Thread.sleep(1000)
        }catch (e:Exception){

        }


        val input=findEditText()
        if (input == null) {
            runningType = 0
            return
        }
        count++
        val arguments = Bundle()
        //输入框中输入礼花
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            "up count:$count"
        )
        input.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)


        val sendView=findSendText()

        if (sendView == null) {
            runningType = 0
            return
        }

        val isSendSuccess = sendView.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        if(!isSendSuccess){
            runningType = 0
        }


        try {
            //30秒
            Thread.sleep(1000*60)
        }catch (e:Exception){

        }
        runningType = 1

        startFireworks()

    }

    /**
     * findEditText
     */
    private fun findEditText(): AccessibilityNodeInfo? {
        //消息发送按钮的节点。
        val sendButton =
            rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.douban.frodo:id/reply_content")
        if (sendButton.isNullOrEmpty()) {
            return null
        }
        sendButton.forEach {
            //匹配button
            if (it.className == "android.widget.MultiAutoCompleteTextView") {
                Log.d(TAG, "found MultiAutoCompleteTextView !")
                return it
            }
        }
        return null

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
    private fun findSendText(): AccessibilityNodeInfo? {
        //消息发送按钮的节点。
        val sendButton =
            rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.douban.frodo:id/btn_send")
        if (sendButton.isNullOrEmpty()) {
            return null
        }
        sendButton.forEach {
            //匹配button
            if (it.className == "android.widget.TextView") {
                Log.d(TAG, "found send TextView!")
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
        val input = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.douban.frodo:id/input_comment_fake_bg")
        if (input.isNullOrEmpty()) {
            return null
        }

        if (input[input.size - 1].className != "android.view.View") {
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

}