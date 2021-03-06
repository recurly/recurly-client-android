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
import com.recurly.android.network.RecurlyError;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the parameters used to make a getCoupon request.  Use of this class should be
 * limited to using the Builder to create the request.
 *
 * @see com.recurly.android.RecurlyApi#getCoupon(CouponRequest, RecurlyApi.CouponResponseHandler)
 */

public class CouponRequest extends GetRequest {

    /**
     * The unique plan code for the request
     */
    protected String planCode;
    /**
     * The coupon code for this request
     */
    protected String couponCode;

    /**
     * Constructor for CouponRequest.  Use Builder instead
     *
     * @param planCode
     * @param couponCode
     * @see com.recurly.android.network.request.CouponRequest.Builder
     */
    public CouponRequest(String planCode, String couponCode) {
        this.planCode = planCode;
        this.couponCode = couponCode;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    @Override
    public Map<String, String> getParams() {
        return new HashMap<String, String>();
    }

    @Override
    public boolean isListRequest() {
        return false;
    }

    @Override
    public String getEndpoint() {
        return "plans/" + planCode + "/coupons/" + couponCode;
    }

    @Override
    /**
     * Validate the request.  Will be called automatically, but can be called by user directly as well
     */
    public RecurlyError validate() {
        if (planCode == null || planCode.isEmpty()) {
            return RecurlyError.validationError("plan code");
        }
        if (couponCode == null || couponCode.isEmpty()) {
            return RecurlyError.validationError("coupon code");
        }
        return null;
    }

    /**
     * Builder helper class to create a CouponRequest instance
     */
    public static class Builder {

        private String mPlanCode;
        private String mCouponCode;

        public Builder setPlanCode(String planCode) {
            mPlanCode = planCode;
            return this;
        }

        public Builder setCouponCode(String couponCode) {
            mCouponCode = couponCode;
            return this;
        }

        public CouponRequest build() {
            return new CouponRequest(mPlanCode, mCouponCode);
        }
    }
}
