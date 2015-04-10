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

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.recurly.android.RecurlyApi;
import com.recurly.androidsdk.MainActivity;

public abstract class BaseFragment extends android.support.v4.app.Fragment {

  protected RecurlyApi getRecurlyApi() {
    return ((MainActivity)getActivity()).getRecurlyApi();
  }

  public void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager)
        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

    if (imm != null) {
      imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
  }

}
