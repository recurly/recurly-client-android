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
import com.recurly.android.network.request.TaxRequest;

public class TaxRequestTest extends UnitTest {
    public void testTaxRequestBuilder() {

        TaxRequest request = new TaxRequest.Builder()
                .setCountryCode("ES")
                .setPostalCode("94131")
                .build();

        assertEquals(request.getCountryCode(), "ES");
        assertEquals(request.getPostalCode(), "94131");

        assertNull(request.validate());

    }

    public void testTaxRequestManual() {

        TaxRequest request = new TaxRequest("94131", "ES");

        assertEquals(request.getCountryCode(), "ES");
        assertEquals(request.getPostalCode(), "94131");

        assertNull(request.validate());
    }

    public void testMissingPlanCode() {
        TaxRequest request = new TaxRequest.Builder().setCountryCode("ES").build();

        RecurlyError error = request.validate();
        assertNotNull(error);
        assertEquals(error.getErrorMessage(), RecurlyError.getValidationError("postal code"));

        request.setPostalCode("94131");
        request.setCountryCode("");

        error = request.validate();
        assertNotNull(error);
        assertEquals(error.getErrorMessage(), RecurlyError.getValidationError("country code"));

    }

}
