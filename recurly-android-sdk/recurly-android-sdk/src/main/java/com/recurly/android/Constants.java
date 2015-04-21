package com.recurly.android;

/**
 * Constants used by the RecurlyApi
 */
public class Constants {

  /**
   * Enum representing currencies supported by RecurlyApi
   */
  public enum CurrencyType {
    CURRENCY_TYPE_USD("USD"),
    ;

    private final String text;

    private CurrencyType(final String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }

  }

  /**
   * Enum representing intervals for plan durations supported by Recurly
   */
  public enum PlanInterval {
    PLAN_INTERVAL_MONTHS("months"),
    ;

    private final String text;

    private PlanInterval(final String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }

  }

  /**
   * Enum representing well known credit card types
   */
  public enum CreditCardType {
    CREDIT_CARD_TYPE_UNKNOWN,
    CREDIT_CARD_TYPE_VISA,
    CREDIT_CARD_TYPE_MASTERCARD,
    CREDIT_CARD_TYPE_AMEX,
    CREDIT_CARD_TYPE_DISCOVER,
    CREDIT_CARD_TYPE_DINERS,
  }


}
