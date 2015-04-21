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

import com.recurly.android.RecurlyValidator;

public class ValidationTest extends UnitTest {

  public void testValidCardNumber() throws Exception {

    assertTrue(RecurlyValidator.validateCreditCard("4111111111111111"));
    assertTrue(RecurlyValidator.validateCreditCard("4111-1111-1111-1111"));
    assertTrue(RecurlyValidator.validateCreditCard("41 11 1111 1111 1111"));

  }

  public void testInvalidCardNumber() throws Exception {
    assertFalse(RecurlyValidator.validateCreditCard(null));
    assertFalse(RecurlyValidator.validateCreditCard("4111"));
    assertFalse(RecurlyValidator.validateCreditCard("4111111111111112"));
    assertFalse(RecurlyValidator.validateCreditCard("4111-1111-1111-1112"));
    assertFalse(RecurlyValidator.validateCreditCard("41111111111111111"));
    assertFalse(RecurlyValidator.validateCreditCard("4aa11111111111a1"));
  }

  public void testValidExpirationDate() throws Exception {
    assertTrue(RecurlyValidator.validateExpirationDate(1, 2020));
    assertTrue(RecurlyValidator.validateExpirationDate(9, 20));
  }

  public void testInvalidExpirationDate() throws Exception {
    assertFalse(RecurlyValidator.validateExpirationDate(1, 2013));
    assertFalse(RecurlyValidator.validateExpirationDate(1, 13));
  }


  public void testInvalidExpirationMonth() throws Exception {
    assertFalse(RecurlyValidator.validateExpirationDate(0, 20));
    assertFalse(RecurlyValidator.validateExpirationDate(-1, 20));
    assertFalse(RecurlyValidator.validateExpirationDate(13, 20));
  }

  public void testInvalidExpirationYear() {
    assertFalse(RecurlyValidator.validateExpirationDate(5, 0));
  }

  public void testValidCvv() {
    assertTrue(RecurlyValidator.validateCvv("000"));
    assertTrue(RecurlyValidator.validateCvv("123"));
    assertTrue(RecurlyValidator.validateCvv("9999"));
  }

  public void testValidCvvWithWhitespaces() {
    assertTrue(RecurlyValidator.validateCvv(" 000 "));
    assertTrue(RecurlyValidator.validateCvv("       123"));
    assertTrue(RecurlyValidator.validateCvv("     9999     "));
  }


  public void testInvalidCvv() {
    assertFalse(RecurlyValidator.validateCvv(null));
    assertFalse(RecurlyValidator.validateCvv(""));
    assertFalse(RecurlyValidator.validateCvv("11"));
    assertFalse(RecurlyValidator.validateCvv("11111"));
    assertFalse(RecurlyValidator.validateCvv("111a"));
  }

  public void testValidCountryCode() {
    assertTrue(RecurlyValidator.validateCountryCode("US"));
    assertTrue(RecurlyValidator.validateCountryCode("ES"));
    assertTrue(RecurlyValidator.validateCountryCode("fr"));
  }

  public void testInvalidCountryCode() {
    assertFalse(RecurlyValidator.validateCountryCode(null));
    assertFalse(RecurlyValidator.validateCountryCode("A"));
    assertFalse(RecurlyValidator.validateCountryCode("AAA"));
    assertFalse(RecurlyValidator.validateCountryCode("AK"));
  }

  public void testValidCurrency() {
    assertTrue(RecurlyValidator.validateCurrency("USD"));
  }

  public void testInvalidCurrency() {
    assertFalse(RecurlyValidator.validateCurrency("Monopoly"));
    assertFalse(RecurlyValidator.validateCurrency(null));
  }


}
