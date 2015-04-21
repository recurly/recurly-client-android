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

package com.recurly.androidsdk;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.recurly.androidsdk.fragment.BaseFragment;

import java.lang.reflect.Constructor;

/**
 * Basic activity class with fragment manager.  Abstracted out from Recurly specific calls for
 * developer clarity.
 *
 */
public class BaseActivity extends FragmentActivity implements FragmentManager.OnBackStackChangedListener {


  public BaseFragment pushFragment(Class fragmentClass) {

    FragmentManager fragmentManager = getSupportFragmentManager();

    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    BaseFragment currentFragment = (BaseFragment) fragmentManager.findFragmentByTag("" + fragmentManager.getBackStackEntryCount());

    BaseFragment fragment = makeInstance(fragmentClass);

    if (currentFragment != null) {
      fragmentTransaction.hide(currentFragment);
    }
    fragmentTransaction.add(R.id.container, fragment, "" + (fragmentManager.getBackStackEntryCount() + 1));

    // add new fragment to the back stack
    fragmentTransaction.addToBackStack(fragmentClass.getName());

    fragmentTransaction.commit();

    return fragment;
  }

  protected BaseFragment makeInstance(Class<? extends BaseFragment> className) {
    try {
      Constructor<? extends BaseFragment> constructor = className.getConstructor(new Class[] {});


      BaseFragment fragment = constructor.newInstance(new Object[] {});
      return fragment;

    } catch (Exception e) {
      throw new IllegalStateException("Error instantiating class", e);
    }
  }

  @Override
  public void onBackPressed() {
    popFragment();
  }


  protected void popFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();

    int backStackEntryCount = fragmentManager.getBackStackEntryCount();

    if (backStackEntryCount >= 1) {
      // remove existing fragment
      fragmentManager.executePendingTransactions();

      FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
      BaseFragment currentFragment = (BaseFragment) fragmentManager.findFragmentByTag("" + fragmentManager.getBackStackEntryCount());

      BaseFragment nextFragment = (BaseFragment) fragmentManager.findFragmentByTag("" + (fragmentManager.getBackStackEntryCount() - 1));

      if (nextFragment == null) {
        // no more fragments, so exit
        finish();
        return;
      }

      fragmentTransaction.remove(currentFragment);

      fragmentTransaction.show(nextFragment);

      fragmentManager.popBackStack();

      fragmentTransaction.commit();


    } else {
      // going to exit..
      super.onBackPressed();
    }
  }


  @Override
  public void onBackStackChanged() {

  }
}
