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

import com.recurly.android.model.Address;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CardPaymentRequest;

import java.util.Map;

public class CardPaymentRequestTest extends UnitTest {

    private String VALID_FIRST_NAME = "John";
    private String VALID_LAST_NAME = "Doe";
    private String VALID_NUMBER = "4111 1111 1111 1111";
    private String INVALID_NUMBER = "4111-1111-1111-1112";
    private String VALID_CVV = "123";
    private String INVALID_CVV_1 = "12345";
    private String INVALID_CVV_2 = "!@#";
    private String VALID_COUNTRY = "US";
    private int VALID_EXPIRATION_MONTH = 06;
    private int INVALID_EXPIRATION_MONTH_1 = -5;
    private int INVALID_EXPIRATION_MONTH_2 = 35;
    private int VALID_EXPIRATION_YEAR = 2020;
    private int INVALID_EXPIRATION_YEAR_1 = 0;
    private int INVALID_EXPIRATION_YEAR_2 = 0;
    private String BLANK = "";

    private Address VALID_ADDRESS = new Address("400 Arkansas", "Suite #200", "San Fernando", "Kansas", "01149", VALID_COUNTRY, null, null);
    private Address INVALID_ADDRESS = new Address(null, null, null, null, null, null, null, null); // missing country, therefore invalid

    public void testCardPaymentRequestBuilder() {

        CardPaymentRequest request = new CardPaymentRequest.Builder()
                .setFirstName(VALID_FIRST_NAME)
                .setLastName(VALID_LAST_NAME)
                .setNumber(VALID_NUMBER)
                .setCvv(VALID_CVV)
                .setCountry(VALID_COUNTRY)
                .setExpirationMonth(VALID_EXPIRATION_MONTH)
                .setExpirationYear(VALID_EXPIRATION_YEAR)
                .build();

        assertEquals(request.getFirstName(), VALID_FIRST_NAME);
        assertEquals(request.getLastName(), VALID_LAST_NAME);
        assertEquals(request.getNumber(), VALID_NUMBER);
        assertEquals(request.getCvv(), VALID_CVV);
        assertEquals(request.getCountry(), VALID_COUNTRY);
        assertEquals(request.getExpirationMonth(), VALID_EXPIRATION_MONTH);
        assertEquals(request.getExpirationYear(), VALID_EXPIRATION_YEAR);

        assertNull(request.validate());

    }

    public void testCardPaymentRequestBuilderWithBillingAddress() {

        CardPaymentRequest request = new CardPaymentRequest.Builder()
                .setFirstName(VALID_FIRST_NAME)
                .setLastName(VALID_LAST_NAME)
                .setNumber(VALID_NUMBER)
                .setCvv(VALID_CVV)
                .setExpirationMonth(VALID_EXPIRATION_MONTH)
                .setExpirationYear(VALID_EXPIRATION_YEAR)
                .setBillingAddress(VALID_ADDRESS)
                .build();

        assertEquals(request.getFirstName(), VALID_FIRST_NAME);
        assertEquals(request.getLastName(), VALID_LAST_NAME);
        assertEquals(request.getNumber(), VALID_NUMBER);
        assertEquals(request.getCvv(), VALID_CVV);
        assertEquals(request.getCountry(), VALID_COUNTRY);
        assertEquals(request.getExpirationMonth(), VALID_EXPIRATION_MONTH);
        assertEquals(request.getExpirationYear(), VALID_EXPIRATION_YEAR);
        assertSame(request.getBillingAddress(), VALID_ADDRESS);

    }

    public void testCardPaymentRequestConstructed() {

        CardPaymentRequest request = new CardPaymentRequest(VALID_NUMBER, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_CVV, VALID_EXPIRATION_MONTH, VALID_EXPIRATION_YEAR, VALID_ADDRESS);

        assertEquals(request.getFirstName(), VALID_FIRST_NAME);
        assertEquals(request.getLastName(), VALID_LAST_NAME);
        assertEquals(request.getNumber(), VALID_NUMBER);
        assertEquals(request.getCvv(), VALID_CVV);
        assertEquals(request.getExpirationMonth(), VALID_EXPIRATION_MONTH);
        assertEquals(request.getExpirationYear(), VALID_EXPIRATION_YEAR);
        assertEquals(request.getBillingAddress(), VALID_ADDRESS);

    }

    public void testValidation() {

        CardPaymentRequest request = new CardPaymentRequest.Builder()
                .setFirstName(VALID_FIRST_NAME)
                .setLastName(VALID_LAST_NAME)
                .setNumber(VALID_NUMBER)
                .setCvv(VALID_CVV)
                .setExpirationMonth(VALID_EXPIRATION_MONTH)
                .setExpirationYear(VALID_EXPIRATION_YEAR)
                .setBillingAddress(VALID_ADDRESS)
                .build();

        RecurlyError error = request.validate();
        assertNull(error);

        request.setFirstName(null);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("first name"));
        request.setFirstName(BLANK);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("first name"));
        request.setFirstName(VALID_FIRST_NAME);

        request.setLastName(null);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("last name"));
        request.setLastName(BLANK);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("last name"));
        request.setLastName(VALID_LAST_NAME);

        request.setNumber(null);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("credit card"));
        request.setNumber(INVALID_NUMBER);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("credit card"));
        request.setNumber(null);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("credit card"));
        request.setNumber(VALID_NUMBER);

        request.setCvv(INVALID_CVV_1);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("cvv"));
        request.setCvv(INVALID_CVV_2);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("cvv"));
        request.setCvv(null);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("cvv"));
        request.setCvv(VALID_CVV);

        request.setExpirationMonth(INVALID_EXPIRATION_MONTH_1);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("expiration date"));
        request.setExpirationMonth(INVALID_EXPIRATION_MONTH_2);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("expiration date"));
        request.setExpirationMonth(VALID_EXPIRATION_MONTH);

        request.setExpirationYear(INVALID_EXPIRATION_YEAR_1);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("expiration date"));
        request.setExpirationYear(INVALID_EXPIRATION_YEAR_2);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("expiration date"));
        request.setExpirationYear(VALID_EXPIRATION_YEAR);

        request.setBillingAddress(null);
        assertEquals(request.validate().getErrorMessage(), RecurlyError.getValidationError("billing address"));
        request.setBillingAddress(INVALID_ADDRESS);
        assertEquals(request.validate().getErrorMessage(), INVALID_ADDRESS.validate().getErrorMessage()); // request should call (and return if exists) billingAddress.validate() before validating its own fields
    }

    public void testGetParams() {

        CardPaymentRequest request = new CardPaymentRequest.Builder()
                .setFirstName(VALID_FIRST_NAME)
                .setLastName(VALID_LAST_NAME)
                .setNumber(VALID_NUMBER)
                .setCvv(VALID_CVV)
                .setExpirationMonth(VALID_EXPIRATION_MONTH)
                .setExpirationYear(VALID_EXPIRATION_YEAR)
                .setBillingAddress(VALID_ADDRESS)
                .build();

        Map<String, String> params = request.getParams();

        assertEquals(params.get("first_name"), VALID_FIRST_NAME);
        assertEquals(params.get("last_name"), VALID_LAST_NAME);
        assertEquals(params.get("number"), VALID_NUMBER);
        assertEquals(params.get("month"), "" + VALID_EXPIRATION_MONTH);
        assertEquals(params.get("year"), "" + VALID_EXPIRATION_YEAR);
        assertEquals(params.get("cvv"), VALID_CVV);

    }
}
