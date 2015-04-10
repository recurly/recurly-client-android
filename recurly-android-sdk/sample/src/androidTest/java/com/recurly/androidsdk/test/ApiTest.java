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

package com.recurly.androidsdk.test;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;

import com.recurly.android.RecurlyApi;
import com.recurly.android.network.request.BaseRequest;
import com.recurly.androidsdk.MainActivity;

import java.util.concurrent.TimeoutException;


public abstract class ApiTest extends ActivityInstrumentationTestCase2<MainActivity> {

  protected RecurlyApi mApi;

  public ApiTest(Class<MainActivity> activityClass) {
    super(activityClass);
  }

  @Override
  protected void setUp() throws Exception {
    // TODO: get a dedicated API key for tests
    Activity activity = getActivity();
    assertNotNull(activity);
    mApi = RecurlyApi.getInstance(activity, "sc-30WYXJUzQ852w0kHEYQ7Rw");


  }


  /**
   * Wait for the network call to finish and throw assertion failure on timeout
   * Uses 500ms interval and 10s timeout
   * @param request
   * @throws Exception
   */
  protected void waitForResult(BaseRequest request) throws TimeoutException {
    waitForResult(request, 500, 20000);
  }

  /**
   * Wait for the network call to finish and throw assertion failure on timeout
   * @param request
   * @param interval
   * @param timeout
   * @throws Exception
   */
  protected void waitForResult(BaseRequest request, long interval, long timeout) throws TimeoutException {
    int count = 0;

    int iterations = (int) (timeout/interval);
    assertTrue(interval > 0);
    assertTrue(iterations > 0);

    for (int i=0; i<iterations; i++) {
      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (request.isFinished()) {
        break;
      }
    }

    if (!request.isFinished()) {
      throw new TimeoutException();
    }
  }


}
