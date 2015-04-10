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

package com.recurly.android.test;

import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CouponRequest;

public class CouponRequestTest extends UnitTest {

  public void testCouponRequestBuilder() {

    CouponRequest request = new CouponRequest.Builder()
        .setPlanCode("plan_code")
        .setCouponCode("12345").build();

    assertEquals(request.getPlanCode(), "plan_code");
    assertEquals(request.getCouponCode(), "12345");

    assertNull(request.validate());

  }

  public void testCouponRequestManual() {

    CouponRequest request = new CouponRequest("plan_code", "12345");

    assertEquals(request.getPlanCode(), "plan_code");
    assertEquals(request.getCouponCode(), "12345");

    assertNull(request.validate());
  }

  public void testMissingPlanCode() {
    CouponRequest request = new CouponRequest.Builder()
        .setCouponCode("12345").build();

    RecurlyError error = request.validate();
    assertNotNull(error);
    assertEquals(error.getErrorMessage(), RecurlyError.getValidationError("plan code"));

    request.setPlanCode("");

    error = request.validate();
    assertNotNull(error);
    assertEquals(error.getErrorMessage(), RecurlyError.getValidationError("plan code"));

  }

  public void testMissingCouponCode() {
    CouponRequest request = new CouponRequest.Builder()
        .setPlanCode("plan_code").build();

    RecurlyError error = request.validate();
    assertNotNull(error);
    assertEquals(error.getErrorMessage(), RecurlyError.getValidationError("coupon code"));

    request.setCouponCode("");

    error = request.validate();
    assertNotNull(error);
    assertEquals(error.getErrorMessage(), RecurlyError.getValidationError("coupon code"));

  }
}
