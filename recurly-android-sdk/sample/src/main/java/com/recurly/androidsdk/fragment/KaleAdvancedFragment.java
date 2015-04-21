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

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.recurly.android.Constants;
import com.recurly.android.RecurlyApi;
import com.recurly.android.RecurlyValidator;
import com.recurly.android.network.dto.AddonDTO;
import com.recurly.android.network.dto.CouponDTO;
import com.recurly.android.network.dto.PlanDTO;
import com.recurly.android.network.dto.TaxDTO;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CardPaymentRequest;
import com.recurly.android.network.request.CouponRequest;
import com.recurly.android.network.request.PlanRequest;
import com.recurly.android.network.request.TaxRequest;
import com.recurly.androidsdk.R;
import com.recurly.androidsdk.util.WrapContentListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class KaleAdvancedFragment extends BaseFragment {


  private int mPlanQuantity;
  private PlanDTO mActivePlan;
  private CouponDTO mActiveCoupon;
  private TaxDTO mTax;

  private enum Field {
    FIELD_CREDIT_CARD,
    FIELD_FIRST_NAME,
    FIELD_LAST_NAME,
    FIELD_EXPIRATION,
    FIELD_CVV,
    FIELD_POSTAL_CODE,
  }
  private HashMap<Field, FieldDescriptor> mFieldDescriptors = new HashMap<Field, FieldDescriptor>();

  private HashMap<String, Integer> mAddonQuantities = new HashMap<String, Integer>();


  private AddonAdapter mAddonAdapter;
  private AddonCartAdapter mAddonCartAdapter;

  private ProgressDialog mProgressDialog;

  private PlanDTO mPlanKaleFan = null;
  private PlanDTO mPlanKaleKrazy = null;

  private Spinner mSpinnerPlan;

  private TextView mTextViewPricing;

  private WrapContentListView mListViewAddons;
  private WrapContentListView mListViewAddonsCart;

  private Spinner mInputPlanQuantity;
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

  private EditText mInputCoupon;
  private View mButtonCoupon;
  private View mRowCoupon;
  private View mRowDiscount;
  private TextView mTextViewDiscountLabel;
  private TextView mTextViewDiscount;
  private TextView mTextViewTax;
  private TextView mTextViewTotal;

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
    View view = inflater.inflate(R.layout.fragment_kale_advanced, container, false);

    return view;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mSpinnerPlan = (Spinner) view.findViewById(R.id.spinner_plan);

    List<String> list = new ArrayList<String>();
    list.add("Kale Fan");
    list.add("Kale Krazy");
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mSpinnerPlan.setAdapter(dataAdapter);

    mSpinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onPlanSelected(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    mListViewAddons = (WrapContentListView) view.findViewById(R.id.list_view_addons);

    mTextViewPricing = (TextView) view.findViewById(R.id.text_pricing);

    mInputPlanQuantity = (Spinner) view.findViewById(R.id.input_plan_quantity);
    mInputFirstName = (EditText) view.findViewById(R.id.input_first_name);
    mInputLastName = (EditText) view.findViewById(R.id.input_last_name);
    mInputCvv = (EditText) view.findViewById(R.id.input_cvv);
    mInputCard = (EditText) view.findViewById(R.id.input_credit_card);

    mContainerCreditCard = view.findViewById(R.id.container_credit_card);

    mInputPostalCode = (EditText) view.findViewById(R.id.input_postal_code);

    mInputPostalCode.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        fetchNewTaxAfterDelay();
      }
    });

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

    mListViewAddonsCart = (WrapContentListView) view.findViewById(R.id.list_view_addons_cart);
    mInputCoupon = (EditText) view.findViewById(R.id.input_coupon);
    mButtonCoupon = view.findViewById(R.id.button_coupon);
    mRowCoupon = view.findViewById(R.id.row_coupon);
    mRowDiscount = view.findViewById(R.id.row_discount);
    mTextViewDiscountLabel = (TextView) view.findViewById(R.id.text_view_discount_label);
    mTextViewDiscount = (TextView) view.findViewById(R.id.text_view_discount);
    mTextViewTax = (TextView) view.findViewById(R.id.text_view_tax);
    mTextViewTotal = (TextView) view.findViewById(R.id.text_view_total);

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

    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setTitle("Loading plans...");
    mProgressDialog.setMessage("Please wait.");
    mProgressDialog.setCancelable(false);
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.show();

    getRecurlyApi().getPlan(new PlanRequest.Builder().setPlanCode("kale_fan").build(), new RecurlyApi.PlanResponseHandler() {
      @Override
      public void onPlanSuccess(PlanDTO plan) {
        mPlanKaleFan = plan;

        if (mPlanKaleKrazy != null) {
          onPlanSelected(0);
          mProgressDialog.hide();
        }
      }

      @Override
      public void onPlanFailure(RecurlyError exception) {
        mProgressDialog.hide();
        Toast.makeText(getActivity(), "Error contacting server.. please try again later", Toast.LENGTH_SHORT).show();
      }
    });

    getRecurlyApi().getPlan(new PlanRequest.Builder().setPlanCode("kale_krazy").build(), new RecurlyApi.PlanResponseHandler() {
      @Override
      public void onPlanSuccess(PlanDTO plan) {
        mPlanKaleKrazy = plan;
        if (mPlanKaleFan != null) {
          onPlanSelected(0);
          mProgressDialog.hide();
        }
      }

      @Override
      public void onPlanFailure(RecurlyError exception) {
        mProgressDialog.hide();
        Toast.makeText(getActivity(), "Error contacting server.. please try again later", Toast.LENGTH_SHORT).show();
      }
    });

    List<String> numberList = new ArrayList<>();
    for (int i=1; i<10; i++) {
      numberList.add(""+i);
    }
    ArrayAdapter<String> numbersAdapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.simple_spinner_item, numberList);
    numbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mInputPlanQuantity.setAdapter(numbersAdapter);

    mInputPlanQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mPlanQuantity = position + 1;
        updateValues();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });


    mButtonCoupon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();

        String coupon = mInputCoupon.getText().toString();
        mInputCoupon.setText(null);

        getRecurlyApi().getCoupon(new CouponRequest.Builder()
                .setPlanCode(mActivePlan.getCode())
                .setCouponCode(coupon).build(),
            new RecurlyApi.CouponResponseHandler() {
              @Override
              public void onCouponSuccess(CouponDTO coupon) {
                mActiveCoupon = coupon;
                updateValues();
              }

              @Override
              public void onCouponFailure(RecurlyError exception) {

                Toast.makeText(getActivity(), "Invalid coupon code", Toast.LENGTH_SHORT);
                mActiveCoupon = null;
                updateValues();
              }
            });
      }
    });

  }

  private void fetchNewTaxAfterDelay() {

    final String string = mInputPostalCode.getText().toString();

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (string.equals(mInputPostalCode.getText().toString())) {
          // string hasn't changed in 1 second
          fetchNewTax();
        }
      }
    }, 1000);
  }

  private void fetchNewTax() {
    getRecurlyApi().getPostalTax(new TaxRequest.Builder()
            .setCountryCode("US").setPostalCode(mInputPostalCode.getText().toString()).build(),
        new RecurlyApi.TaxResponseHandler() {
          @Override
          public void onTaxSuccess(TaxDTO tax) {
            mTax = tax;
            updateValues();
          }

          @Override
          public void onTaxFailure(RecurlyError exception) {

          }
        });

  }

  private void onPlanSelected(int position) {

    PlanDTO oldPlan = mActivePlan;

    if (position == 0) {
      // kale fan
      mActivePlan = mPlanKaleFan;
    } else {
      // kale crazy
      mActivePlan = mPlanKaleKrazy;
    }

    if (mActivePlan == null) {
      return;
    }

    if (oldPlan == mActivePlan) {
      return;
    }


    mActiveCoupon = null;
    mAddonQuantities.clear();

    mTextViewPricing.setText(String.format("%s%.2f / month", mActivePlan.getPricing().getSymbol(),
        mActivePlan.getPricing().getUnitAmount()));

    if (mAddonAdapter == null) {
      mAddonAdapter = new AddonAdapter(mActivePlan.getAddons());
      mListViewAddons.setAdapter(mAddonAdapter);
    } else {
      mAddonAdapter.setAddons(mActivePlan.getAddons());
      mAddonAdapter.notifyDataSetChanged();
    }

    mListViewAddons.setNeedsResize();
    updateValues();
  }

  private void updateValues() {


    if (mActivePlan == null) {
      return;
    }

    if (mAddonCartAdapter == null) {
      mAddonCartAdapter = new AddonCartAdapter(mAddonQuantities);
      mListViewAddonsCart.setAdapter(mAddonCartAdapter);
    }

    mAddonCartAdapter.notifyDataSetChanged();
    mListViewAddonsCart.setNeedsResize();
    mInputCoupon.setText(null);

    float total = 0;

    total += mActivePlan.getPricing().getUnitAmount() * mPlanQuantity;

    for (String addonCode : mAddonQuantities.keySet()) {
      int quantity = mAddonQuantities.get(addonCode);

      total += mActivePlan.getAddon(addonCode).getPricing().getUnitAmount() * quantity;
    }

    float discount = 0;
    if (mActiveCoupon != null) {
      discount = mActiveCoupon.getDiscount(total);
    }

    total -= discount;


    float tax = 0;

    if (!mActivePlan.isTaxExempt()) {
      if (mTax != null) {
        tax = mTax.getRate() * total;
      }
    }

    total += tax;


    if (discount > 0) {
      mTextViewDiscount.setText(String.format("-%s%.2f", mActivePlan.getPricing().getSymbol(),
          discount));
      mTextViewDiscountLabel.setText(String.format("Discount (%s)", mActiveCoupon.getCode()));
    } else {
      mTextViewDiscount.setText(String.format("%s%.2f", mActivePlan.getPricing().getSymbol(),
          discount));
      mTextViewDiscountLabel.setText("Discount");
    }
    mTextViewPricing.setText(String.format("%s%.2f / month", mActivePlan.getPricing().getSymbol(),
        mActivePlan.getPricing().getUnitAmount()));

    mTextViewTax.setText(String.format("%s%.2f", mActivePlan.getPricing().getSymbol(),
        tax));
    mTextViewTotal.setText(String.format("%s%.2f", mActivePlan.getPricing().getSymbol(),
        total));

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


  private class AddonAdapter extends BaseAdapter {

    private List<AddonDTO> mAddons;

    public AddonAdapter(List<AddonDTO> addons) {
      super();

      mAddons = addons;
    }


    @Override
    public int getCount() {

      if (mAddons == null || mAddons.isEmpty()) {
        return 0;
      }

      return mAddons.size();
    }


    public AddonDTO getItem(int position) {
      return mAddons.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

      if (convertView == null) {
        convertView = LayoutInflater.from(getActivity()).inflate(R.layout.row_kale_addon, parent, false);
      }

      final AddonDTO addon = getItem(position);

      if (addon != null) {
        TextView nameTextView = (TextView) convertView.findViewById(R.id.text_addon_name);
        TextView priceTextView = (TextView) convertView.findViewById(R.id.text_addon_price);
        final Spinner qtyInput = (Spinner) convertView.findViewById(R.id.input_addon_quantity);


        List<String> numberList = new ArrayList<>();
        for (int i=0; i<10; i++) {
          numberList.add(""+i);
        }
        ArrayAdapter<String> numbersAdapter = new ArrayAdapter<String>(getActivity(),
            android.R.layout.simple_spinner_item, numberList);
        numbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qtyInput.setAdapter(numbersAdapter);

        qtyInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
              mAddonQuantities.remove(addon.getCode());
            } else {
              mAddonQuantities.put(addon.getCode(), position);
            }
            updateValues();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
        });

        nameTextView.setText(addon.getName());
        priceTextView.setText(String.format("%s%.2f", mActivePlan.getPricing().getSymbol(), addon.getPricing().getUnitAmount()));

      } else {

      }


      return convertView;
    }

    public void setAddons(List<AddonDTO> addons) {
      mAddons = addons;
    }
  }


  private class AddonCartAdapter extends BaseAdapter {

    private HashMap<String, Integer> mAddonQuantities;

    public AddonCartAdapter(HashMap<String, Integer> addonQuantities) {
      super();

      mAddonQuantities = addonQuantities;
    }


    @Override
    public int getCount() {

      if (mAddonQuantities == null || mAddonQuantities.isEmpty()) {
        return 1;
      }

      return mAddonQuantities.size() + 1;
    }


    public AddonDTO getItem(int position) {
      position = position - 1;
      ArrayList<String> codes = new ArrayList(mAddonQuantities.keySet());
      Collections.sort(codes);

      AddonDTO addon = mActivePlan.getAddon(codes.get(position));
      return addon;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

      if (convertView == null) {
        convertView = LayoutInflater.from(getActivity()).inflate(R.layout.row_kale_addon_cart, parent, false);
      }

      TextView nameTextView = (TextView) convertView.findViewById(R.id.text_addon_name);
      TextView priceTextView = (TextView) convertView.findViewById(R.id.text_addon_price);
      TextView quantityTextView = (TextView) convertView.findViewById(R.id.text_addon_quantity);


      if (position == 0) {
        nameTextView.setText(mActivePlan.getName());
        priceTextView.setText(String.format("%s%.2f", mActivePlan.getPricing().getSymbol(),
            mActivePlan.getPricing().getUnitAmount() * mPlanQuantity));

        quantityTextView.setText(String.format("%dx", mPlanQuantity));

      } else {
        final AddonDTO addon = getItem(position);

        int quantity = mAddonQuantities.get(addon.getCode());

        nameTextView.setText(addon.getName());
        priceTextView.setText(String.format("%s%.2f", mActivePlan.getPricing().getSymbol(),
            addon.getPricing().getUnitAmount() * quantity));

        quantityTextView.setText(String.format("%dx", quantity));
      }



      return convertView;
    }

    public void setAddonQuantities(HashMap<String, Integer> addonQuantities) {
      mAddonQuantities = addonQuantities;
    }
  }
}
