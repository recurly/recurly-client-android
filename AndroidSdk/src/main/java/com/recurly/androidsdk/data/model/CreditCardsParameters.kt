package com.recurly.androidsdk.data.model

/**
 *
 * This class is an enum class which stores values, it stores all the data to identify each
 * Credit Card type by its pattern, length and physical pattern
 *
 */

internal enum class CreditCardsParameters(
    /**
     * The cardType is the name of the credit card, it has to be the same as the enumerator
     * name, but the enum name has to be capitalized
     */
    val cardType: String,

    /**
     * The numberPattern is a regular expression that old how that type of card can be
     * identified since its first digits
     */
    val numberPattern: String,

    /**
     * The minLength and maxLength fields are used to validate the min and max number digits
     * that an specific credit card type can have.
     * The cvvLength indicates how much digits the credit card cvv code have (usually 3 or 4)
     */
    val minLength: Int, val maxLength: Int, val cvvLength: Int,

    /**
     * The physicalPattern is an expression that shows how the digits of each credit card number
     * must be separated to make them more understandable
     */
    val physicalPattern: String
) {
    DISCOVER(
        "discover",
        "^6[045]([0-9]*)",
        16, 19, 3,
        "4-4-4-7"
    ),
    UNION_PAY(
        "union_pay",
        "^6[2]([0-9]*)",
        16, 19, 3,
        "4-4-4-7"
    ),
    MASTER(
        "master",
        "^5[1-5]([0-9]*)",
        16, 16, 3,
        "4-4-4-4"
    ),
    AMERICAN_EXPRESS(
        "american_express",
        "^3[47]([0-9]*)",
        15, 15, 4,
        "4-6-5"
    ),
    ELO(
        "elo",
        "^(636368|504175|636297|506[67]\\d\\d)\\d{0,10}\$",
        13, 16, 3,
        "4-4-4-4"
    ),
    VISA(
        "visa",
        "^4([0-9]*)",
        13, 16, 3,
        "4-4-4-4"
    ),
    JCB(
        "jcb",
        "^35[2-8]([0-9]*)",
        16, 16, 3,
        "4-4-4-4"
    ),
    DINERS_CLUB(
        "diners_club",
        "^3(?:0[0-5]|[68][0-9])([0-9]*)",
        14, 14, 3,
        "4-6-4"
    ),
    HIPERCARD(
        "hipercard",
        "^(606282\\d{10}(\\d{3})?)|(3841\\d{15})\$",
        16, 16, 4,
        "4-4-4-4"
    ),
    TARJETA_NARANJA(
        "tarjeta_naranja",
        "^58956[200-299]([0-9]*)",
        16, 19, 3,
        "4-4-4-7"
    )
}