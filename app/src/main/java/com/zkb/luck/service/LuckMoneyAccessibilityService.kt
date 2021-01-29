package com.zkb.luck.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class LuckMoneyAccessibilityService : AccessibilityService() {

    private val TAG="kang"
    private var rootNodeInfo: AccessibilityNodeInfo? = null
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                find()
                Log.d(TAG,"AccessibilityEvent WINDOW_STATE change:")
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Log.d(TAG,"AccessibilityEvent 内容change:")
                find()
            }
        }


    }

    private fun find(){
        this.rootNodeInfo = rootInActiveWindow

        val open= rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/f42")
        if(open.isNotEmpty()){
            Log.d(TAG,"opened fail")
            findOpenButton(rootInActiveWindow)
        }else{
            findOpenButton(rootInActiveWindow)
        }

        val nodeInfo= rootNodeInfo?.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/auf")
        //   val nodeInfo= rootNodeInfo?.findAccessibilityNodeInfosByText("微信红包");
        if(!nodeInfo.isNullOrEmpty()){
            //  Log.d(TAG,"found luck money!")
            nodeInfo.forEachIndexed { index, accessibilityNodeInfo ->

                val luckInfo= accessibilityNodeInfo.findAccessibilityNodeInfosByText("微信红包")
                if(luckInfo.isNullOrEmpty()){
                    Log.d(TAG, "no found luck$index")
                }else{
                    val readyOpen= accessibilityNodeInfo.findAccessibilityNodeInfosByText("已领取")
                    if(readyOpen.isNullOrEmpty()){
                        Log.d(TAG,"opening....$index")
                        val openSuccess=nodeInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        if(openSuccess){
                            Log.d(TAG,"opened luck money successful：$index")
                            //  findOpenButton(rootNodeInfo)
                            //  findOpenButton(rootInActiveWindow)
                            val open= rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/f4f")
                            if(open.isNotEmpty()){
                                Log.d(TAG,"opened fail$index")
                            }
                            findOpenButton(rootInActiveWindow)

                        }else{
                            Log.d(TAG,"opened fail$index")
                        }
                    }else{
                        Log.d(TAG,"already opened$index")
                    }

                }

            }



        }else{
            Log.d(TAG,"no found luck money!~~")
        }
    }

    private fun  findOpenButton(node: AccessibilityNodeInfo?):AccessibilityNodeInfo?{

        if (node == null) return null

        Log.d(TAG,"1:findOpenButton contentDescription:"+node.contentDescription)
        Log.d(TAG,"1:findOpenButton contentDescription:"+node.text)
        //非layout元素
        if (node.childCount == 0&&"android.widget.Button"==node.className) {

            if("开"==node.contentDescription){
                return node
            }

            Log.d(TAG,"findOpenButton contentDescription:"+node.contentDescription)

        }else{
            //layout元素，遍历找button
            Log.d(TAG,"findOpenButton layout元素:"+node.contentDescription)
            var button: AccessibilityNodeInfo?
            for (i in 0 until node.childCount) {
                button = findOpenButton(node.getChild(i))
                if (button != null) return button
            }

        }


        return null
    }
    override fun onInterrupt() {

    }
}