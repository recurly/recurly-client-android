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

package com.recurly.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class RecurlyValidator {

  public enum CreditCardType {
    CREDIT_CARD_TYPE_UNKNOWN,
    CREDIT_CARD_TYPE_VISA,
    CREDIT_CARD_TYPE_MASTERCARD,
    CREDIT_CARD_TYPE_AMEX,
    CREDIT_CARD_TYPE_DISCOVER,
    CREDIT_CARD_TYPE_DINERS,
  }

  private static HashMap<CreditCardType, String> sCardRegexes = new HashMap<CreditCardType, String>();

  private static HashSet<String> sValidCountryCodes = new HashSet<>();

  static {
    sCardRegexes.put(CreditCardType.CREDIT_CARD_TYPE_VISA, "^4[0-9]{3}[0-9]+?");
    sCardRegexes.put(CreditCardType.CREDIT_CARD_TYPE_MASTERCARD, "^5[1-5][0-9]{2}[0-9]+");
    sCardRegexes.put(CreditCardType.CREDIT_CARD_TYPE_AMEX, "^3[47][0-9]{2}[0-9]+");
    sCardRegexes.put(CreditCardType.CREDIT_CARD_TYPE_DISCOVER, "^6(?:011|5[0-9]{2})[0-9]+");
    sCardRegexes.put(CreditCardType.CREDIT_CARD_TYPE_DINERS, "^3(?:0[0-5]|[68][0-9])[0-9][0-9]+");

    String[] countries = Locale.getISOCountries();
    for (String country : countries) {
      sValidCountryCodes.add(country.toLowerCase());
    }
  }

  public static boolean validateName(String name) {
    if (name == null || name.isEmpty()) {
      return false;
    }

    return true;
  }

  public static CreditCardType getCreditCardType(String creditCard) {

    if (creditCard == null) {
      return CreditCardType.CREDIT_CARD_TYPE_UNKNOWN;
    }

    creditCard = stripCardNumber(creditCard);

    for (CreditCardType type : sCardRegexes.keySet()) {
      String regex = sCardRegexes.get(type);
      if (creditCard.matches(regex)) {
        return type;
      }
    }

    return CreditCardType.CREDIT_CARD_TYPE_UNKNOWN;
  }

  public static boolean validateCreditCard(String creditCard) {
    if (creditCard == null || creditCard.isEmpty()) {
      return false;
    }

    String cardNumber = stripCardNumber(creditCard);
    return luhnValidate(cardNumber);
  }

  private static boolean luhnValidate(String cardNumber) {

    int sum = 0;
    boolean alternate = false;
    for (int i = cardNumber.length() - 1; i >= 0; i--) {

      char c = cardNumber.charAt(i);
      int n = c - '0';
      if (n < 0 || n > 9) {
        // not numeric
        return false;
      }
      if (alternate) {
        n *= 2;
        if (n > 9) {
          n = (n % 10) + 1;
        }
      }
      sum += n;
      alternate = !alternate;
    }
    return (sum % 10 == 0);
  }

  private static String stripCardNumber(String card) {

    if (card == null) {
      return "";
    }

    return card.replaceAll(" ", "").replaceAll("-", "");
  }


  public static boolean validateCvv(String cvv) {
    if (cvv == null) {
      return false;
    }

    cvv = cvv.trim();

    if (cvv.length() < 3 || cvv.length() > 4) {
      return false;
    }

    try {
      Integer.parseInt(cvv);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public static boolean validateExpirationDate(String monthString, String yearString) {

    try {
      int month = Integer.parseInt(monthString);
      int year = Integer.parseInt(yearString);

      return validateExpirationDate(month, year);
    } catch (Exception ex) {
      return false;
    }

  }
  public static boolean validateExpirationDate(int month, int year) {
    if (month < 1 || month > 12) {
      return false;
    }

    if (year < 100) {
      year += 2000;
    }

    Date currentDate = new Date();
    SimpleDateFormat yearFormatter = new SimpleDateFormat( "yyyy" );
    int currentYear = Integer.parseInt(yearFormatter.format(currentDate));

    SimpleDateFormat monthFormatter = new SimpleDateFormat( "MM" );
    int currentMonth = Integer.parseInt(monthFormatter.format(currentDate));

    if (year < currentYear) {
      return false;
    }
    if (year == currentYear && month < currentMonth) {
      return false;
    }

    return true;
  }

  public static boolean validateCountryCode(String countryCode) {
    if (countryCode == null) {
      return false;
    }

    return sValidCountryCodes.contains(countryCode.toLowerCase());
  }


}
