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

package com.recurly.android.network.request;

import com.recurly.android.network.RecurlyError;

/**
 * Common superclass for outgoing Recurly API requests
 */
public abstract class BaseRequest {

    public boolean mFinished = false;

    /**
     * Used internally by parser.  Whether or not the implementing request returns a list of Models or a single Model
     *
     * @return true if request is a list request
     */
    public abstract boolean isListRequest();

    /**
     * Used internally by API client.  Gets the destination endpoint for the implementing request.
     *
     * @return the endpoint of this API call
     */
    public abstract String getEndpoint();

    /**
     * Used internally by API client.  Checks to see if the request is valid before sending it off.
     *
     * @return null if validated, RecurlyError otherwise
     */
    public abstract RecurlyError validate();

    public boolean isFinished() {
        return mFinished;
    }

    public void setFinished(boolean finished) {
        mFinished = finished;
    }
}
