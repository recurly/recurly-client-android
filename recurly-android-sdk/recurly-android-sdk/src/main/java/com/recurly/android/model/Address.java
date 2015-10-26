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

package com.recurly.android.model;

import com.recurly.android.network.RecurlyError;

import java.util.HashMap;

public class Address extends BaseModel {
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String vatNumber;

    /**
     * Constructor for Address Object. Pass in empty string for fields which are not applicable.
     *
     * @param address1
     * @param address2
     * @param city
     * @param state
     * @param postalCode
     * @param country
     * @param phone
     * @param vatNumber
     */
    public Address(String address1, String address2, String city, String state, String postalCode, String country, String phone, String vatNumber) {
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.phone = phone;
        this.vatNumber = vatNumber;
    }

    @Deprecated
    public Address(String address1, String address2, String city, String postalCode, String country) {
        this(address1, address2, city, "", postalCode, country, "", "");
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("address1", address1);
        params.put("address2", address2);
        params.put("city", city);
        params.put("state", state);
        params.put("postal_code", postalCode);
        params.put("country", country);
        params.put("phone", phone);
        params.put("vat_number", vatNumber);

        return params;
    }

    public RecurlyError validate() {
        if (country == null || country.isEmpty()) {
            return RecurlyError.validationError("country");
        }

        return null;
    }

    public static class Builder {
        private String mAddress1 = "";
        private String mAddress2 = "";
        private String mCity = "";
        private String mState = "";
        private String mPostalCode = "";
        private String mCountry = "";
        private String mPhone = "";
        private String mVatNumber = "";

        public Builder setAddress1(String address1) {
            mAddress1 = address1;
            return this;
        }

        public Builder setAddress2(String address2) {
            mAddress2 = address2;
            return this;
        }

        public Builder setCity(String city) {
            mCity = city;
            return this;
        }

        public Builder setState(String state) {
            mState = state;
            return this;
        }

        public Builder setPostalCode(String postalCode) {
            mPostalCode = postalCode;
            return this;
        }

        public Builder setCountry(String country) {
            mCountry = country;
            return this;
        }

        public Builder setPhone(String phone) {
            mPhone = phone;
            return this;
        }

        public Builder setVatNumber(String vatNumber) {
            mVatNumber = vatNumber;
            return this;
        }

        @Deprecated
        public Address createAddress() {
            return build();
        }

        public Address build() {
            return new Address(mAddress1, mAddress2, mCity, mState, mPostalCode, mCountry, mPhone, mVatNumber);
        }
    }
}
