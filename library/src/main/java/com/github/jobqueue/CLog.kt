package com.github.jobqueue

import android.text.TextUtils
import android.util.Log

/**
 * C Log
 * @author Robin
 * @since 2015-12-28 09:31:51
 */
object CLog {
    const val LEVEL_VERBOSE: Int = 0
    const val LEVEL_DEBUG: Int = 1
    const val LEVEL_INFO: Int = 2
    const val LEVEL_WARNING: Int = 3
    const val LEVEL_ERROR: Int = 4
    const val LEVEL_FATAL: Int = 5

    private var sLevel = LEVEL_VERBOSE

    var tagPrefix: String = "system.out"

    var printTagPrefixOnly: Boolean = false

    var allowD: Boolean = true
    var allowE: Boolean = true
    var allowI: Boolean = true
    var allowV: Boolean = true
    var allowW: Boolean = true
    var allowWtf: Boolean = true

    private fun generateTag(caller: StackTraceElement): String {
        var tag = "%s.%s(L:%d)"
        var callerClazzName = caller.className
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        tag = String.format(tag, callerClazzName, caller.methodName, caller.lineNumber)
        tag = if (TextUtils.isEmpty(tagPrefix)) tag else tagPrefix + ":" + tag
        tag = if (printTagPrefixOnly) tagPrefix else tagPrefix + ":" + tag
        return tag
    }

    val callerStackTraceElement: StackTraceElement
        get() = Thread.currentThread().stackTrace[4]

    /**
     * set log level, the level lower than this level will not be logged
     *
     * @param level
     */
    fun setLogLevel(level: Int) {
        sLevel = level
    }

    fun openLog() {
        allowD = true
        allowE = true
        allowI = true
        allowV = true
        allowW = true
        allowWtf = true
    }

    fun closeLog() {
        allowD = false
        allowE = false
        allowI = false
        allowV = false
        allowW = false
        allowWtf = false
    }

    /*=========================================================================
     * Verbose
     *========================================================================= 
     */
    /**
     * Send a VERBOSE log message.
     *
     * @param msg
     */
    fun v(msg: String) {
        if (!allowV) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_VERBOSE) {
            return
        }
        Log.v(tag, msg)
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param msg
     * @param throwable
     */
    fun v(msg: String, throwable: Throwable?) {
        if (!allowV) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_VERBOSE) {
            return
        }
        Log.v(tag, msg, throwable)
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param msg
     * @param args
     */
    fun v(msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowV) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_VERBOSE) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.v(tag, msg)
    }

    fun vTag(tag: String?, msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowV) return

        if (sLevel > LEVEL_VERBOSE) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.v(tag, msg)
    }

    /*public static void v(String tag, String msg) {
     	if (!allowV)
			return;
    	
        if (sLevel > LEVEL_VERBOSE) {
            return;
        }
        Log.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable throwable) {
     	if (!allowV)
			return;
     	
        if (sLevel > LEVEL_VERBOSE) {
            return;
        }
        Log.v(tag, msg, throwable);
    }

    public static void v(String tag, String msg, Object... args) {
     	if (!allowV)
			return;
     	
        if (sLevel > LEVEL_VERBOSE) {
            return;
        }
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        Log.v(tag, msg);
    }*/
    /*=========================================================================
     * Debug
     *========================================================================= 
     */
    /**
     * Send a DEBUG log message
     *
     * @param msg
     */
    fun d(msg: String) {
        if (!allowD) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_DEBUG) {
            return
        }
        Log.d(tag, msg)
    }

    /**
     * Send a DEBUG log message
     *
     * @param msg
     * @param args
     */
    fun d(msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowD) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_DEBUG) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.d(tag, msg)
    }

    /**
     * Send a DEBUG log message
     *
     * @param msg
     * @param throwable
     */
    fun d(msg: String?, throwable: Throwable?) {
        if (!allowD) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_DEBUG) {
            return
        }
        Log.d(tag, msg, throwable)
    }

    fun dTag(tag: String?, msg: String?, throwable: Throwable?) {
        if (!allowD) return

        if (sLevel > LEVEL_DEBUG) {
            return
        }
        Log.d(tag, msg, throwable)
    }

    /*public static void d(String tag, String msg) {
    	if (!allowD)
			return;
    	
        if (sLevel > LEVEL_DEBUG) {
            return;
        }
        Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Object... args) {
    	if (!allowD)
			return;
    	
        if (sLevel > LEVEL_DEBUG) {
            return;
        }
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable throwable) {
    	if (!allowD)
			return;
    	
        if (sLevel > LEVEL_DEBUG) {
            return;
        }
        Log.d(tag, msg, throwable);
    }*/
    /*=========================================================================
     * Info
     *========================================================================= 
     */
    /**
     * Send an INFO log message
     *
     * @param msg
     */
    fun i(msg: String) {
        if (!allowI) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_INFO) {
            return
        }
        Log.i(tag, msg)
    }

    /**
     * Send an INFO log message
     *
     * @param msg
     * @param args
     */
    fun i(msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowI) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_INFO) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.i(tag, msg)
    }

    /**
     * Send an INFO log message
     *
     * @param msg
     */
    fun i(msg: String?, throwable: Throwable?) {
        if (!allowI) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_INFO) {
            return
        }
        Log.i(tag, msg, throwable)
    }

    fun iTag(tag: String?, msg: String?, throwable: Throwable?) {
        if (!allowI) return

        if (sLevel > LEVEL_INFO) {
            return
        }
        Log.i(tag, msg, throwable)
    }

    /*public static void i(String tag, String msg) {
      	if (!allowI)
    			return;
      	
        if (sLevel > LEVEL_INFO) {
            return;
        }
        Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Object... args) {
      	if (!allowI)
    			return;
      	
        if (sLevel > LEVEL_INFO) {
            return;
        }
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable throwable) {
      	if (!allowI)
    			return;
      	
        if (sLevel > LEVEL_INFO) {
            return;
        }
        Log.i(tag, msg, throwable);
    }*/
    /*=========================================================================
     * Warn
     *========================================================================= 
     */
    /**
     * Send a WARNING log message
     *
     * @param msg
     */
    fun w(msg: String) {
        if (!allowW) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_WARNING) {
            return
        }
        Log.w(tag, msg)
    }

    /**
     * Send a WARNING log message
     *
     * @param msg
     * @param args
     */
    fun w(msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowW) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_WARNING) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.w(tag, msg)
    }

    fun wTag(tag: String?, msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowW) return

        if (sLevel > LEVEL_WARNING) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.w(tag, msg)
    }

    /**
     * Send a WARNING log message
     *
     * @param msg
     * @param throwable
     */
    fun w(msg: String?, throwable: Throwable?) {
        if (!allowW) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_WARNING) {
            return
        }
        Log.w(tag, msg, throwable)
    }

    /*public static void w(String tag, String msg) {
     	if (!allowW)
			return;
     	
        if (sLevel > LEVEL_WARNING) {
            return;
        }
        Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Object... args) {
     	if (!allowW)
			return;
     	
        if (sLevel > LEVEL_WARNING) {
            return;
        }
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable throwable) {
     	if (!allowW)
			return;
     	
        if (sLevel > LEVEL_WARNING) {
            return;
        }
        Log.w(tag, msg, throwable);
    }*/
    /*=========================================================================
     * Error
     *========================================================================= 
     */
    /**
     * Send an ERROR log message
     *
     * @param msg
     */
    fun e(msg: String) {
        if (!allowE) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_ERROR) {
            return
        }
        Log.e(tag, msg)
    }

    /**
     * Send an ERROR log message
     *
     * @param msg
     * @param args
     */
    fun e(msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowE) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_ERROR) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.e(tag, msg)
    }

    fun eTag(tag: String?, msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowE) return

        if (sLevel > LEVEL_ERROR) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.e(tag, msg)
    }

    /**
     * Send an ERROR log message
     *
     * @param msg
     * @param throwable
     */
    fun e(msg: String?, throwable: Throwable?) {
        if (!allowE) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_ERROR) {
            return
        }
        Log.e(tag, msg, throwable)
    }

    /*public static void e(String tag, String msg) {
     	if (!allowE)
			return;
     	
        if (sLevel > LEVEL_ERROR) {
            return;
        }
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Object... args) {
     	if (!allowE)
			return;
     	
        if (sLevel > LEVEL_ERROR) {
            return;
        }
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable throwable) {
     	if (!allowE)
			return;
     	
        if (sLevel > LEVEL_ERROR) {
            return;
        }
        Log.e(tag, msg, throwable);
    }*/
    /*=========================================================================
     * What  a Terrible Failure
     *========================================================================= 
     */
    /**
     * Send a FATAL ERROR log message
     *
     * @param msg
     */
    fun f(msg: String?) {
        if (!allowWtf) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_FATAL) {
            return
        }
        Log.wtf(tag, msg)
    }

    /**
     * Send a FATAL ERROR log message
     *
     * @param msg
     * @param args
     */
    fun f(msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowWtf) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_FATAL) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.wtf(tag, msg)
    }

    fun fTag(tag: String?, msg: String, vararg args: Any?) {
        var msg = msg
        if (!allowWtf) return

        if (sLevel > LEVEL_FATAL) {
            return
        }
        if (args.size > 0) {
            msg = String.format(msg, *args)
        }
        Log.wtf(tag, msg)
    }


    /**
     * Send a FATAL ERROR log message
     *
     * @param msg
     * @param throwable
     */
    fun f(msg: String?, throwable: Throwable?) {
        if (!allowWtf) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)

        if (sLevel > LEVEL_FATAL) {
            return
        }
        Log.wtf(tag, msg, throwable)
    } /*public static void f(String tag, String msg) {
     	if (!allowWtf)
			return;
     	
        if (sLevel > LEVEL_FATAL) {
            return;
        }
        Log.wtf(tag, msg);
    }

    public static void f(String tag, String msg, Object... args) {
     	if (!allowWtf)
			return;
     	
        if (sLevel > LEVEL_FATAL) {
            return;
        }
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        Log.wtf(tag, msg);
    }

    public static void f(String tag, String msg, Throwable throwable) {
     	if (!allowWtf)
			return;
     	
        if (sLevel > LEVEL_FATAL) {
            return;
        }
        Log.wtf(tag, msg, throwable);
    }*/
}