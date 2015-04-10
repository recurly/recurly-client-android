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

import com.recurly.android.network.NetworkResponseError;
import com.recurly.android.network.RecurlyError;

public class RecurlyErrorTest extends UnitTest {

  public void testInitializeError() {
    RecurlyError error = new RecurlyError(500, "code", "message");

    assertEquals(error.getStatusCode(), 500);
    assertEquals(error.getErrorCode(), "code");
    assertEquals(error.getErrorMessage(), "message");
  }

  public void testNetworkError() {
    NetworkResponseError networkError = new NetworkResponseError();

    RecurlyError error = new RecurlyError(networkError);

    assertEquals(error.getStatusCode(), 400);

  }

  public void testValidationError() {
    RecurlyError error = RecurlyError.validationError("field");

    assertEquals(error.getStatusCode(), 400);
    assertEquals(error.getErrorCode(), "validation error");
    assertEquals(error.getErrorMessage(), RecurlyError.getValidationError("field"));

  }

  public void testServerError() {
    RecurlyError error = RecurlyError.genericServerError("message");

    assertEquals(error.getStatusCode(), 500);
    assertEquals(error.getErrorCode(), "Unexpected error");
    assertEquals(error.getErrorMessage(), "Unexpected error: message");
  }

}
