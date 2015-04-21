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
import com.recurly.android.network.dto.TaxDTO;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.TaxRequest;
import com.recurly.androidsdk.R;

public class TaxesFragment extends BaseFragment {
  private EditText mInputPostalCode;
  private EditText mInputCountryCode;
  private TextView mTextResult;

  private View mButtonSubmit;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_taxes, container, false);

    return view;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mInputCountryCode = (EditText) view.findViewById(R.id.input_country);
    mInputPostalCode = (EditText) view.findViewById(R.id.input_postal_code);

    mTextResult = (TextView) view.findViewById(R.id.text_result);

    mButtonSubmit = view.findViewById(R.id.button_submit);

    mButtonSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
        getRecurlyApi().getPostalTax(new TaxRequest.Builder()
                .setCountryCode(mInputCountryCode.getText().toString())
                .setPostalCode(mInputPostalCode.getText().toString())
                .build(),
            new RecurlyApi.TaxResponseHandler() {
              @Override
              public void onTaxSuccess(TaxDTO tax) {

                String response = String.format("%.2f%%", (tax.getRate() * 100));

                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("Tax rate:\n");
                sb.append(response);

                sb.setSpan(new ForegroundColorSpan(Color.BLUE), sb.length() - response.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextResult.setText(sb);
                mTextResult.setTextColor(Color.BLACK);
              }

              @Override
              public void onTaxFailure(RecurlyError exception) {
                mTextResult.setText(exception.getErrorMessage());
                mTextResult.setTextColor(Color.RED);
              }
            });
      }
    });
  }
}
