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

import com.google.gson.Gson;
import com.recurly.android.model.Tax;

public class TaxModelTest extends UnitTest {

  private static String validTaxJson = "{\"type\":\"us\",\"region\":\"CA\",\"rate\":\"0.0875\"}";

  public void testParseTax() {

    Tax tax = new Gson().fromJson(validTaxJson, Tax.class);

    assertEquals(tax.getRate(), 0.0875f);
    assertEquals(tax.getType(), "us");

  }

  public void testNoTax() {

    Tax tax = Tax.NO_TAX;

    assertEquals(tax.getRate(), 0.0f);
    assertEquals(tax.getType(), "all");

  }

}