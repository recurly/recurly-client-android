/*
 * The MIT License
 * Copyright (c) 2014-2015 Recurly, Inc.

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.recurly.android.util;

import android.util.Log;

public class RecurlyLog {
    private static final String LOG_TAG = "RecurlyApi";
    private static final String NO_MESSAGE = "";

    private static int sLogLevel = Log.INFO;

    public static int getLogLevel() {
        return sLogLevel;
    }

    public static void setLogLevel(int logLevel) {
        sLogLevel = logLevel;
    }

    public static void d(String message) {
        if (sLogLevel > Log.DEBUG) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.d(LOG_TAG, message);
    }

    public static void d(String message, Throwable t, Object... args) {
        if (sLogLevel > Log.DEBUG) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.d(LOG_TAG, String.format(message, args), t);
    }

    public static void d(String message, Object... args) {
        if (sLogLevel > Log.DEBUG) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.d(LOG_TAG, String.format(message, args));
    }

    public static void i(String message) {
        if (sLogLevel > Log.INFO) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.i(LOG_TAG, message);
    }

    public static void i(String message, Object... args) {
        if (sLogLevel > Log.INFO) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.i(LOG_TAG, String.format(message, args));
    }

    public static void w(String message) {
        if (sLogLevel > Log.WARN) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.w(LOG_TAG, message);
    }

    public static void w(String message, Object... args) {
        if (sLogLevel > Log.WARN) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.w(LOG_TAG, String.format(message, args));
    }

    public static void e(String message) {
        if (sLogLevel > Log.ERROR) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.e(LOG_TAG, message);
    }

    public static void e(String message, Object... args) {
        if (sLogLevel > Log.ERROR) {
            return;
        }
        if (message == null) {
            message = NO_MESSAGE;
        }
        Log.e(LOG_TAG, String.format(message, args));
    }


}