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
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.recurly.android.Constants;
import com.recurly.android.RecurlyApi;
import com.recurly.android.RecurlyValidator;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CardPaymentRequest;
import com.recurly.androidsdk.R;

import java.util.HashMap;
import java.util.HashSet;

public class KaleBasicFragment extends BaseFragment {



  private enum Field {
    FIELD_CREDIT_CARD,
    FIELD_FIRST_NAME,
    FIELD_LAST_NAME,
    FIELD_EXPIRATION,
    FIELD_CVV,
    FIELD_POSTAL_CODE,
  }
  private HashMap<Field, FieldDescriptor> mFieldDescriptors = new HashMap<Field, FieldDescriptor>();

  private EditText mInputFirstName;
  private EditText mInputLastName;
  private EditText mInputCard;
  private EditText mInputCvv;
  private EditText mInputPostalCode;
  private EditText mInputMonth;
  private EditText mInputYear;

  private TextView mLabelFirstName;
  private TextView mLabelLastName;
  private TextView mLabelCard;
  private TextView mLabelCvv;
  private TextView mLabelPostalCode;
  private TextView mLabelExpiration;

  private TextView mTextViewErrorTitle;
  private TextView mTextViewErrorDetails;

  private View mButtonSubmit;

  private View mContainerCreditCard;

  private ViewGroup mLayoutError;
  private ViewGroup mLayoutInput;
  private ViewGroup mLayoutSuccess;

  private ImageView mImageViewCreditCard;

  private TextWatcher mCreditCardWatcher = new TextWatcher() {
    public boolean mFormatting;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public synchronized void afterTextChanged(Editable s) {
      if (!mFormatting) {
        mFormatting = true;

        int selection = mInputCard.getSelectionEnd();

        String cardNumber = mInputCard.getText().toString();
        if (selection == mInputCard.getText().toString().length()) {
          String displayVal = formatCard(cardNumber);
          mInputCard.setText(displayVal);
          mInputCard.setSelection(mInputCard.getText().toString().length());
        }
        updateCardImage(cardNumber);

        mFormatting = false;
      }

    }

    private String formatCard(String s) {
      s = s.replaceAll(" ", "").replaceAll("-", "");
      // For now, this just splits into groups of 4, but in future, do it card specifically?
      int digitCount = 0;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < s.length(); ++i) {
        sb.append(s.charAt(i));
        if (++digitCount % 4 == 0) {
          sb.append(" ");
        }
      }
      return sb.toString().trim();
    }
  };

  private void updateCardImage(String cardNumber) {

    Constants.CreditCardType type = RecurlyValidator.getCreditCardType(cardNumber);

    switch (type) {
      case CREDIT_CARD_TYPE_AMEX:
        mImageViewCreditCard.setImageResource(R.drawable.card_amex);
        break;
      case CREDIT_CARD_TYPE_DINERS:
        mImageViewCreditCard.setImageResource(R.drawable.card_diners);
        break;
      case CREDIT_CARD_TYPE_DISCOVER:
        mImageViewCreditCard.setImageResource(R.drawable.card_discover);
        break;
      case CREDIT_CARD_TYPE_MASTERCARD:
        mImageViewCreditCard.setImageResource(R.drawable.card_mastercard);
        break;
      case CREDIT_CARD_TYPE_VISA:
        mImageViewCreditCard.setImageResource(R.drawable.card_visa);
        break;
      case CREDIT_CARD_TYPE_UNKNOWN:
        mImageViewCreditCard.setImageResource(R.drawable.card_generic);
        break;
    }
  }


  private HashSet<Field> mInvalidFields = new HashSet<Field>();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_kale_basic, container, false);

    return view;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mInputFirstName = (EditText) view.findViewById(R.id.input_first_name);
    mInputLastName = (EditText) view.findViewById(R.id.input_last_name);
    mInputCvv = (EditText) view.findViewById(R.id.input_cvv);
    mInputCard = (EditText) view.findViewById(R.id.input_credit_card);

    mContainerCreditCard = view.findViewById(R.id.container_credit_card);

    mInputPostalCode = (EditText) view.findViewById(R.id.input_postal_code);

    mInputMonth = (EditText) view.findViewById(R.id.input_expiration_month);
    mInputYear = (EditText) view.findViewById(R.id.input_expiration_year);

    mButtonSubmit = view.findViewById(R.id.button_submit);

    mLabelCard = (TextView) view.findViewById(R.id.label_credit_card);
    mLabelFirstName = (TextView) view.findViewById(R.id.label_first_name);
    mLabelLastName = (TextView) view.findViewById(R.id.label_last_name);
    mLabelCvv = (TextView) view.findViewById(R.id.label_cvv);
    mLabelPostalCode = (TextView) view.findViewById(R.id.label_zip);
    mLabelExpiration = (TextView) view.findViewById(R.id.label_expiration);

    mLayoutError = (ViewGroup) view.findViewById(R.id.layout_errors);
    mLayoutInput = (ViewGroup) view.findViewById(R.id.layout_input);
    mLayoutSuccess = (ViewGroup) view.findViewById(R.id.layout_success);

    mTextViewErrorTitle = (TextView) view.findViewById(R.id.text_view_error_title);
    mTextViewErrorDetails = (TextView) view.findViewById(R.id.text_view_error_details);

    mImageViewCreditCard = (ImageView) view.findViewById(R.id.image_view_card_type);

    mButtonSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onSubmit();

      }
    });

    mFieldDescriptors.put(Field.FIELD_CREDIT_CARD, new FieldDescriptor(
        Field.FIELD_CREDIT_CARD, mContainerCreditCard, mLabelCard, "Credit Card"));
    mFieldDescriptors.put(Field.FIELD_FIRST_NAME, new FieldDescriptor(
        Field.FIELD_FIRST_NAME, mInputFirstName, mLabelFirstName, "First Name"));
    mFieldDescriptors.put(Field.FIELD_LAST_NAME, new FieldDescriptor(
        Field.FIELD_LAST_NAME, mInputLastName, mLabelLastName, "Last Name"));
    mFieldDescriptors.put(Field.FIELD_EXPIRATION, new FieldDescriptor(
        Field.FIELD_EXPIRATION, mInputMonth, mLabelExpiration, "Expiration Date"));
    mFieldDescriptors.put(Field.FIELD_CVV, new FieldDescriptor(
        Field.FIELD_CVV, mInputCvv, mLabelCvv, "CVV"));
    mFieldDescriptors.put(Field.FIELD_POSTAL_CODE, new FieldDescriptor(
        Field.FIELD_POSTAL_CODE, mInputPostalCode, mLabelPostalCode, "Postal Code"));

    mInputCard.addTextChangedListener(mCreditCardWatcher);
  }

  private void onSubmit() {
    hideKeyboard();

    mInvalidFields.clear();
    // reset values..
    for (Field field : Field.values()) {
      FieldDescriptor fieldDescriptor = mFieldDescriptors.get(field);

      fieldDescriptor.input.setBackgroundResource(R.drawable.rounded_white_black_border);

      if (fieldDescriptor.input == mInputMonth) {
        mInputYear.setBackgroundResource(R.drawable.rounded_white_black_border);
      }

      fieldDescriptor.label.setTextColor(Color.BLACK);
    }



    if (!RecurlyValidator.validateCreditCard(mInputCard.getText().toString())) {
      mInvalidFields.add(Field.FIELD_CREDIT_CARD);
    }
    if (!RecurlyValidator.validateExpirationDate(mInputMonth.getText().toString(), mInputYear.getText().toString())) {
      mInvalidFields.add(Field.FIELD_EXPIRATION);
    }
    if (!RecurlyValidator.validateCvv(mInputCvv.getText().toString())) {
      mInvalidFields.add(Field.FIELD_CVV);
    }

    if (!RecurlyValidator.validateName(mInputFirstName.getText().toString())) {
      mInvalidFields.add(Field.FIELD_FIRST_NAME);
    }

    if (!RecurlyValidator.validateName(mInputLastName.getText().toString())) {
      mInvalidFields.add(Field.FIELD_LAST_NAME);
    }

    if (mInputPostalCode.getText().toString().isEmpty()) {
      mInvalidFields.add(Field.FIELD_POSTAL_CODE);
    }

    if (mInvalidFields.isEmpty()) {

      int expMonth = Integer.parseInt(mInputMonth.getText().toString());
      int expYear = Integer.parseInt(mInputYear.getText().toString());

      getRecurlyApi().getPaymentToken(new CardPaymentRequest.Builder()
              .setFirstName(mInputFirstName.getText().toString())
              .setLastName(mInputLastName.getText().toString())
              .setNumber(mInputCard.getText().toString())
              .setCvv(mInputCvv.getText().toString())
              .setCountry("US")
              .setExpirationMonth(expMonth)
              .setExpirationYear(expYear)
              .build(),
          new RecurlyApi.TokenResponseHandler() {
            @Override
            public void onTokenSuccess(String response) {
              mLayoutError.setVisibility(View.GONE);
              mLayoutInput.setVisibility(View.GONE);
              mLayoutSuccess.setVisibility(View.VISIBLE);

            }

            @Override
            public void onTokenFailure(RecurlyError exception) {
              mLayoutError.setVisibility(View.VISIBLE);
              mTextViewErrorTitle.setText("There was an error connecting to the server.");
              mTextViewErrorDetails.setText(null);
            }
          });
    } else {
      mLayoutError.setVisibility(View.VISIBLE);
      mTextViewErrorTitle.setText("Oops! The following fields appear to be invalid:");

      StringBuilder sb = new StringBuilder();

      for (Field field : mInvalidFields) {
        FieldDescriptor fieldDescriptor = mFieldDescriptors.get(field);

        fieldDescriptor.input.setBackgroundResource(R.drawable.rounded_white_red_border);

        if (fieldDescriptor.input == mInputMonth) {
          mInputYear.setBackgroundResource(R.drawable.rounded_white_red_border);
        }

        fieldDescriptor.label.setTextColor(Color.RED);

        sb.append(" \u2022 ");
        sb.append(fieldDescriptor.displayName);
        sb.append("\n");
      }

      mTextViewErrorDetails.setText(sb);

    }



  }

  private class FieldDescriptor {
    private FieldDescriptor(Field type, View input, TextView label, String displayName) {
      this.type = type;
      this.input = input;
      this.label = label;
      this.displayName = displayName;
    }

    Field type;
    View input;
    TextView label;
    String displayName;
  }
}
