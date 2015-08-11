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

import com.recurly.android.RecurlyApi;
import com.recurly.android.RecurlyValidator;
import com.recurly.android.model.Address;
import com.recurly.android.network.RecurlyError;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the parameters used to make a getPaymentToken request.  Use of this class should be
 * limited to using the Builder to create the request.
 *
 * @see com.recurly.android.RecurlyApi#getPaymentToken(CardPaymentRequest, RecurlyApi.TokenResponseHandler)
 */
public class CardPaymentRequest extends GetRequest {

    /**
     * The credit card number
     */
    protected String number;
    /**
     * The first name on the credit card
     */
    protected String firstName;
    /**
     * The last name on the credit card
     */
    protected String lastName;
    /**
     * The cvv of the credit card
     */
    protected String cvv;
    /**
     * The month the credit card expires
     */
    protected int expirationMonth;
    /**
     * The year the credit card expires
     */
    protected int expirationYear;
    /**
     * The billing address associated with the credit card
     */
    protected Address billingAddress;

    /**
     * Constructor to generate a CardPaymentRequest without a billing address.  Builder preferred
     *
     * @param number
     * @param firstName
     * @param lastName
     * @param cvv
     * @param country         (deprecated)
     * @param expirationMonth
     * @param expirationYear
     * @see com.recurly.android.network.request.CardPaymentRequest.Builder
     * @deprecated
     */
    public CardPaymentRequest(String number, String firstName, String lastName, String cvv,
                              String country, int expirationMonth, int expirationYear) {
        this(number, firstName, lastName, cvv, expirationMonth, expirationYear, new Address(null, null, null, null, null, country, null, null));
    }

    /**
     * Constructor to generate a CardPaymentRequest.  Builder preferred
     *
     * @param number
     * @param firstName
     * @param lastName
     * @param cvv
     * @param expirationMonth
     * @param expirationYear
     * @param billingAddress
     * @see com.recurly.android.network.request.CardPaymentRequest.Builder
     */
    public CardPaymentRequest(String number, String firstName, String lastName, String cvv,
                              int expirationMonth, int expirationYear, Address billingAddress) {
        this.number = number;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cvv = cvv;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.billingAddress = billingAddress;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Deprecated
    public String getCountry() {
        if (billingAddress == null) {
            return null;
        }

        return billingAddress.getCountry();
    }

    @Deprecated
    public void setCountry(String country) {
        if (billingAddress != null) {
            billingAddress.setCountry(country);
        } else {
            billingAddress = new Address(null, null, null, null, null, country, null, null);
        }
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Override
    public Map<String, String> getParams() {
        HashMap<String, String> params = billingAddress.getParams();

        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("number", number);
        params.put("month", "" + expirationMonth);
        params.put("year", "" + expirationYear);
        params.put("cvv", cvv);

        return params;
    }

    @Override
    public boolean isListRequest() {
        return false;
    }

    @Override
    public String getEndpoint() {
        return "token";
    }

    @Override
    /**
     * Validate the request.  Will be called automatically, but can be called by user directly as well
     */
    public RecurlyError validate() {
        if (billingAddress == null) {
            return RecurlyError.validationError("billing address");
        }
        RecurlyError billingAddressError = billingAddress.validate();
        if (billingAddressError != null) {
            return billingAddressError;
        }
        if (firstName == null || firstName.isEmpty()) {
            return RecurlyError.validationError("first name");
        }
        if (lastName == null || lastName.isEmpty()) {
            return RecurlyError.validationError("last name");
        }
        if (!RecurlyValidator.validateCreditCard(number)) {
            return RecurlyError.validationError("credit card");
        }
        if (!RecurlyValidator.validateCvv(cvv)) {
            return RecurlyError.validationError("cvv");
        }
        if (!RecurlyValidator.validateExpirationDate(expirationMonth, expirationYear)) {
            return RecurlyError.validationError("expiration date");
        }
        return null;
    }

    /**
     * Helper builder class to instantiate a CardPaymentRequest
     */
    public static class Builder {

        private String mNumber;
        private String mFirstName;
        private String mLastName;
        private String mCvv;
        private int mExpirationMonth;
        private int mExpirationYear;
        private Address mBillingAddress;

        public Builder setNumber(String number) {
            mNumber = number;
            return this;
        }

        public Builder setFirstName(String firstName) {
            mFirstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            mLastName = lastName;
            return this;
        }

        public Builder setCvv(String cvv) {
            mCvv = cvv;
            return this;
        }

        /**
         * Sets the country of the billing address (overrides Builder#setBillingAddress). (deprecated)
         *
         * @see com.recurly.android.model.Address#country
         * @see com.recurly.android.network.request.CardPaymentRequest.Builder#setBillingAddress(Address)
         * @deprecated please use the `country` field of the billing address instead
         */
        public Builder setCountry(String country) {
            if (mBillingAddress != null) {
                mBillingAddress.setCountry(country);
            } else {
                mBillingAddress = new Address(null, null, null, null, null, country, null, null);
            }
            return this;
        }

        public Builder setExpirationMonth(int expirationMonth) {
            mExpirationMonth = expirationMonth;
            return this;
        }

        public Builder setExpirationYear(int expirationYear) {
            mExpirationYear = expirationYear;
            return this;
        }

        public Builder setBillingAddress(Address billingAddress) {
            mBillingAddress = billingAddress;
            return this;
        }

        public CardPaymentRequest build() {
            return new CardPaymentRequest(mNumber, mFirstName, mLastName, mCvv, mExpirationMonth, mExpirationYear, mBillingAddress);
        }
    }
}
