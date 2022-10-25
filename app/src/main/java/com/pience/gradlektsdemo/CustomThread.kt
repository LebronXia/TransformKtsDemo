package com.pience.gradlektsdemo

import android.util.Log

/**
 * Created by zxb in 2022/7/28
 */
class CustomThread: Thread {
    private val TAG = "CustomThread"
    constructor() : super()
    constructor(target: Runnable?) : super(target)
    constructor(group: ThreadGroup?, target: Runnable?) : super(group, target)
    constructor(name: String?) : super(name)
    constructor(group: ThreadGroup?, name: String?) : super(group, name)
    constructor(target: Runnable?, name: String?) : super(target, name)
    constructor(group: ThreadGroup?, target: Runnable?, name: String?) : super(group, target, name)
    constructor(group: ThreadGroup?, target: Runnable?, name: String?, stackSize: Long) : super(
        group,
        target,
        name,
        stackSize
    )

    override fun run() {
        val start = System.currentTimeMillis()
        super.run()
        Log.e(TAG, "thread name:$name" + ", r")
    }
}