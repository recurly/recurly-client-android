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

package com.recurly.android.network;

/**
 * Configuration for Recurly API Client.  Not to be accessed directly.
 */
public class RecurlyConfig {

    private String mBaseUrl;
    private String mApiPath;
    private String mPublicKey;
    private String mCurrency;
    private int mDefaultTimeout;
    private String mApiVersion;

    public RecurlyConfig(String baseUrl, String apiPath, String publicKey, String currency, int defaultTimeout, String apiVersion) {
        mBaseUrl = baseUrl;
        mApiPath = apiPath;
        mPublicKey = publicKey;
        mCurrency = currency;
        mDefaultTimeout = defaultTimeout;
        mApiVersion = apiVersion;
    }

    public static RecurlyConfig getDefaultConfiguration(String publicKey) {
        return new Builder()
                .setBaseUrl("https://api.recurly.com/")
                .setApiPath("js/v1")
                .setDefaultTimeout(10000)
                .setPublicKey(publicKey)
                .setCurrency("USD")
                .setApiVersion("3.0.9")
                .build();
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public String getApiPath() {
        return mApiPath;
    }

    public void setApiPath(String apiPath) {
        mApiPath = apiPath;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public void setPublicKey(String publicKey) {
        mPublicKey = publicKey;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public int getDefaultTimeout() {
        return mDefaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        mDefaultTimeout = defaultTimeout;
    }

    public String getApiVersion() {
        return mApiVersion;
    }

    public void setApiVersion(String apiVersion) {
        mApiVersion = apiVersion;
    }

    public static class Builder {

        private String mBaseUrl;
        private String mApiPath;
        private String mPublicKey;
        private String mCurrency;
        private int mDefaultTimeout;
        private String mApiVersion;

        public Builder setBaseUrl(String baseUrl) {
            mBaseUrl = baseUrl;
            return this;
        }

        public Builder setApiPath(String apiPath) {
            mApiPath = apiPath;
            return this;
        }

        public Builder setPublicKey(String publicKey) {
            mPublicKey = publicKey;
            return this;
        }

        public Builder setCurrency(String currency) {
            mCurrency = currency;
            return this;
        }

        public Builder setDefaultTimeout(int defaultTimeout) {
            mDefaultTimeout = defaultTimeout;
            return this;
        }

        public Builder setApiVersion(String apiVersion) {
            mApiVersion = apiVersion;
            return this;
        }

        public RecurlyConfig build() {
            return new RecurlyConfig(mBaseUrl, mApiPath, mPublicKey, mCurrency, mDefaultTimeout, mApiVersion);
        }
    }

}
