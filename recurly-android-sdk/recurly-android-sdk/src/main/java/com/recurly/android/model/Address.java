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

public class Address extends BaseModel {
  private String address1;
  private String address2;
  private String city;
  private String postalCode;
  private String country;

  public Address(String address1, String address2, String city, String postalCode, String country) {
    this.address1 = address1;
    this.address2 = address2;
    this.city = city;
    this.postalCode = postalCode;
    this.country = country;
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

  public static class Builder {
    private String mAddress1;
    private String mAddress2;
    private String mCity;
    private String mPostalCode;
    private String mCountry;

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

    public Builder setPostalCode(String postalCode) {
      mPostalCode = postalCode;
      return this;
    }

    public Builder setCountry(String country) {
      mCountry = country;
      return this;
    }

    public Address createAddress() {
      return new Address(mAddress1, mAddress2, mCity, mPostalCode, mCountry);
    }
  }
}
