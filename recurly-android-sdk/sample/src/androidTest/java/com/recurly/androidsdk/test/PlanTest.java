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

import com.recurly.android.RecurlyApi;
import com.recurly.android.network.dto.PlanDTO;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.PlanRequest;
import com.recurly.androidsdk.MainActivity;

import java.util.concurrent.TimeoutException;

public class PlanTest extends ApiTest {
  public PlanTest() {
    super(MainActivity.class);
  }

  public void testInvalidRequest() {

    PlanRequest request = new PlanRequest.Builder()
        .setPlanCode("invalid_plan")
        .build();

    mApi.getPlan(request, new RecurlyApi.PlanResponseHandler() {
      @Override
      public void onPlanSuccess(PlanDTO plan) {
        assertTrue(false);
      }

      @Override
      public void onPlanFailure(RecurlyError exception) {
        assertTrue("Unexpected status code.. expected 400, received " + exception.getStatusCode(),
            exception.getStatusCode() == 400);
      }
    });


    try {
      waitForResult(request);
    } catch (TimeoutException e) {
      assertFalse("Network call timed out", true);
    }
  }


  public void testValidPlan() {

    PlanRequest request = new PlanRequest.Builder()
        .setPlanCode("kale_fan")
        .build();

    mApi.getPlan(request, new RecurlyApi.PlanResponseHandler() {
      @Override
      public void onPlanSuccess(PlanDTO plan) {
        assertTrue(plan != null);
        assertTrue(plan.getCode() != null);
      }

      @Override
      public void onPlanFailure(RecurlyError exception) {
        assertTrue(false);
      }
    });


    try {
      waitForResult(request);
    } catch (TimeoutException e) {
      assertFalse("Network call timed out", true);
    }
  }



}
