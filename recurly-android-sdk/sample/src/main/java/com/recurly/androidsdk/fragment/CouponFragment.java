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

package com.recurly.androidsdk.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.recurly.android.RecurlyApi;
import com.recurly.android.model.Coupon;
import com.recurly.android.model.Tax;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CouponRequest;
import com.recurly.android.network.request.TaxRequest;
import com.recurly.androidsdk.R;

public class CouponFragment extends BaseFragment {
  private EditText mInputCouponCode;
  private EditText mInputPlanCode;
  private TextView mTextResult;

  private View mButtonSubmit;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_coupon, container, false);

    return view;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mInputCouponCode = (EditText) view.findViewById(R.id.input_coupon);
    mInputPlanCode = (EditText) view.findViewById(R.id.input_plan);

    mTextResult = (TextView) view.findViewById(R.id.text_result);

    mButtonSubmit = view.findViewById(R.id.button_submit);

    mButtonSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
        getRecurlyApi().getCoupon(new CouponRequest.Builder()
                .setCouponCode(mInputCouponCode.getText().toString())
                .setPlanCode(mInputPlanCode.getText().toString())
                .build(),
            new RecurlyApi.CouponResponseHandler() {
              @Override
              public void onCouponSuccess(Coupon coupon) {

                String response;

                switch (coupon.getDiscountType()) {
                  case DISCOUNT_TYPE_FIXED_AMOUNT: {
                    response = String.format("%.2f %s", coupon.getDiscountAmount(), coupon.getCurrency());
                    break;
                  }
                  case DISCOUNT_TYPE_PERCENT: {
                    response = String.format("%.2f%%", (coupon.getDiscountRate() * 100));
                    break;
                  }
                  case DISCOUNT_TYPE_NONE:
                  default: {
                    // unsupported coupon type?
                    response = "Unsupported";
                    break;
                  }
                }

                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("Discount:\n");
                sb.append(response);

                sb.setSpan(new ForegroundColorSpan(Color.BLUE), sb.length() - response.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextResult.setText(sb);
                mTextResult.setTextColor(Color.BLACK);
              }

              @Override
              public void onCouponFailure(RecurlyError exception) {
                mTextResult.setText(exception.getErrorMessage());
                mTextResult.setTextColor(Color.RED);
              }
            });
      }
    });
  }
}
