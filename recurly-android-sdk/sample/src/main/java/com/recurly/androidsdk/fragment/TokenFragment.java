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
import android.widget.NumberPicker;
import android.widget.TextView;

import com.recurly.android.RecurlyApi;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CardPaymentRequest;
import com.recurly.androidsdk.R;

public class TokenFragment extends BaseFragment {

  private EditText mInputFirstName;
  private EditText mInputLastName;
  private EditText mInputCard;
  private EditText mInputCvv;
  private EditText mInputCountry;
  private NumberPicker mInputMonth;
  private NumberPicker mInputYear;

  private TextView mTextResult;

  private View mButtonSubmit;

  private void setupDebugValues() {

    mInputFirstName.setText("first");
    mInputLastName.setText("last");
    mInputCard.setText("4111111111111111");
    mInputCvv.setText("123");
    mInputCountry.setText("US");
    mInputMonth.setValue(12);
    mInputYear.setValue(15);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_token, container, false);

    return view;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mInputFirstName = (EditText) view.findViewById(R.id.input_first_name);
    mInputLastName = (EditText) view.findViewById(R.id.input_last_name);
    mInputCvv = (EditText) view.findViewById(R.id.input_cvv);
    mInputCard = (EditText) view.findViewById(R.id.input_number);
    mInputCountry = (EditText) view.findViewById(R.id.input_country);

    mInputMonth = (NumberPicker) view.findViewById(R.id.input_month);
    mInputYear = (NumberPicker) view.findViewById(R.id.input_year);

    mTextResult = (TextView) view.findViewById(R.id.text_result);

    mButtonSubmit = view.findViewById(R.id.button_submit);

    mInputMonth.setMinValue(1);
    mInputMonth.setMaxValue(12);

    mInputMonth.setDisplayedValues( new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" } );

    mInputYear.setMinValue(14);
    mInputYear.setMaxValue(19);

    mInputYear.setDisplayedValues(new String[]{"14", "15", "16", "17", "18", "19"});

    mButtonSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
        getRecurlyApi().getPaymentToken(new CardPaymentRequest.Builder()
                .setFirstName(mInputFirstName.getText().toString())
                .setLastName(mInputLastName.getText().toString())
                .setNumber(mInputCard.getText().toString())
                .setCvv(mInputCvv.getText().toString())
                .setCountry(mInputCountry.getText().toString())
                .setExpirationMonth(mInputMonth.getValue())
                .setExpirationYear(mInputYear.getValue())
                .build(),
            new RecurlyApi.TokenResponseHandler() {
              @Override
              public void onTokenSuccess(String response) {
                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("Token:\n");
                sb.append(response);

                sb.setSpan(new ForegroundColorSpan(Color.BLUE), sb.length()-response.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextResult.setText(sb);
                mTextResult.setTextColor(Color.BLUE);
              }

              @Override
              public void onTokenFailure(RecurlyError exception) {
                mTextResult.setText(exception.getErrorMessage());
                mTextResult.setTextColor(Color.RED);
              }
            });
      }
    });


  }


}
