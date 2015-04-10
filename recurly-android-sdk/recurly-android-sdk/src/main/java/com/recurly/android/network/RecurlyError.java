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

package com.recurly.android.network;

import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

public class RecurlyError extends Exception {

  public static final int STATUS_CODE_VALIDATION      = 401;

  private int mStatusCode;
  private String mErrorCode;
  private String mErrorMessage;

  /**
   * Create a RecurlyNetworkError from a NetworkResponseError response.  NetworkResponseError is
   * a class that indicates server responded with 200, but is actually passed back error information
   * @param error
   */
  public RecurlyError(NetworkResponseError error) {
    // made up status code, 400 = bad request
    mStatusCode = 400;
    mErrorCode = error.getErrorCode();
    mErrorMessage = error.getErrorMessage();
  }

  public RecurlyError(int statusCode, String errorCode, String errorMessage) {
    mStatusCode = statusCode;
    mErrorCode = errorCode;
    mErrorMessage = errorMessage;
  }

  public RecurlyError() {

  }

  public static String getValidationError(String fieldName) {
    return "Input validation for '" + fieldName + "' failed";
  }

  public static RecurlyError validationError(String fieldName) {
    RecurlyError error = new RecurlyError();

    error.setStatusCode(400); // bad request
    error.setErrorCode("validation error");
    error.setErrorMessage(getValidationError(fieldName));

    return error;
  }

  public static RecurlyError genericServerError(String errorString) {
    RecurlyError error = new RecurlyError();

    error.setStatusCode(500); // bad request
    error.setErrorCode("Unexpected error");
    error.setErrorMessage("Unexpected error: " + errorString);

    return error;
  }


  public static RecurlyError errorFromVolley(VolleyError volleyError) {
    if (volleyError instanceof NetworkResponseError) {
      return new RecurlyError((NetworkResponseError) volleyError);
    }

    RecurlyError error = new RecurlyError();

    String responseBody = null;
    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
      responseBody = new String(volleyError.networkResponse.data);
    }
    if (!(volleyError instanceof NoConnectionError)) {
      volleyError.printStackTrace();
    }

    if (volleyError.networkResponse != null) {
      error.mStatusCode = volleyError.networkResponse.statusCode;
    }
    error.mErrorMessage = volleyError.getLocalizedMessage();
    error.mErrorCode = volleyError.getMessage();

    return error;
  }

  public String getErrorCode() {
    return mErrorCode;
  }

  public void setErrorCode(String errorCode) {
    mErrorCode = errorCode;
  }

  public String getErrorMessage() {
    return mErrorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    mErrorMessage = errorMessage;
  }

  public int getStatusCode() {
    return mStatusCode;
  }

  public void setStatusCode(int statusCode) {
    mStatusCode = statusCode;
  }

  @Override
  public String toString() {
    return "RecurlyNetworkError{" +
        "mStatusCode=" + mStatusCode +
        ", mErrorCode='" + mErrorCode + '\'' +
        ", mErrorMessage='" + mErrorMessage + '\'' +
        '}';
  }
}
