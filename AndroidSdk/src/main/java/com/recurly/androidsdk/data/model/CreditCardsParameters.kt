package com.recurly.androidsdk.data.model

// A Pair, not IntRange: canon pairs like 5068 to 508 are not ascending.
internal data class BinGroup(val lengths: Set<Int>, val ranges: List<Pair<Int, Int>>)

internal enum class CreditCardsParameters(
    /**
     * The cardType is the name of the credit card, it has to be the same as the enumerator
     * name, but the enum name has to be capitalized
     */
    val cardType: String,

    val cvvLength: Int,

    val gaps: Set<Int>,

    val groups: List<BinGroup>
) {
    MASTER(
        "master",
        3,
        setOf(4, 8, 12),
        listOf(BinGroup(setOf(16), listOf(2221 to 2720, 51 to 55)))
    ),
    DINERS_CLUB(
        "diners_club",
        3,
        setOf(4, 10),
        listOf(BinGroup(setOf(14), listOf(300 to 305, 36 to 36, 38 to 38)))
    ),
    AMERICAN_EXPRESS(
        "american_express",
        4,
        setOf(4, 10),
        listOf(BinGroup(setOf(15), listOf(34 to 34, 37 to 37)))
    ),
    JCB(
        "jcb",
        3,
        setOf(4, 8, 12),
        listOf(BinGroup(setOf(16), listOf(3528 to 3589)))
    ),
    HIPERCARD(
        "hipercard",
        3,
        setOf(4, 8, 12),
        listOf(
            BinGroup(setOf(19), listOf(3841 to 3841, 606282 to 606282)),
            BinGroup(setOf(16, 17, 18), listOf(606282 to 606282))
        )
    ),
    VISA(
        "visa",
        3,
        setOf(4, 8, 12),
        listOf(BinGroup(setOf(13, 16), listOf(400000 to 451415, 451417 to 499999)))
    ),
    ELO(
        "elo",
        3,
        setOf(4, 8, 12),
        listOf(
            BinGroup(
                setOf(16),
                listOf(
                    451416 to 451416, 506699 to 506699, 506707 to 506708, 506715 to 506715,
                    506717 to 506722, 506724 to 506736, 506739 to 506748, 506753 to 506753,
                    506774 to 506778, 509000 to 509014, 509020 to 509089, 509091 to 509101,
                    509103 to 509807, 509831 to 509877, 509897 to 509900, 509918 to 509964,
                    509971 to 509986, 509995 to 509999, 627780 to 627780, 636297 to 636297,
                    636368 to 636368, 650031 to 650033, 650035 to 650051, 650057 to 650081,
                    650406 to 650439, 650485 to 650504, 650506 to 650538, 650552 to 650598,
                    650720 to 650727, 650901 to 650922, 650928 to 650928, 650938 to 650939,
                    650946 to 650978, 651652 to 651704, 655000 to 655019, 655021 to 655057
                )
            )
        )
    ),
    TARJETA_NARANJA(
        "tarjeta_naranja",
        3,
        setOf(4, 8, 12),
        listOf(BinGroup(setOf(16, 17, 18, 19), listOf(589562 to 589562)))
    ),
    DISCOVER(
        "discover",
        3,
        setOf(4, 8, 12),
        listOf(
            BinGroup(
                setOf(16, 17, 18, 19),
                listOf(
                    601100 to 601103, 601105 to 601109, 60112 to 60114, 601174 to 601174,
                    601177 to 601179, 601186 to 601199, 6440 to 650030, 650034 to 650034,
                    650052 to 650056, 650505 to 650505, 650539 to 650551, 650599 to 650599,
                    650082 to 650405, 650440 to 650484, 650601 to 650609, 650611 to 650719,
                    650728 to 650900, 650923 to 650927, 650929 to 650937, 650940 to 650945,
                    650979 to 651651, 651705 to 654999, 655020 to 655020, 655058 to 659999
                )
            )
        )
    ),
    UNION_PAY(
        "union_pay",
        3,
        setOf(4, 8, 12),
        listOf(
            BinGroup(
                setOf(16, 17, 18, 19),
                listOf(
                    62000 to 62182, 62184 to 62197, 622126 to 622925, 624000 to 626999,
                    6272 to 6272, 62760 to 62777, 627781 to 627799, 6282 to 6289, 6291 to 6292,
                    8100 to 8171
                )
            )
        )
    ),
    MAESTRO(
        "maestro",
        3,
        setOf(4, 8, 12),
        listOf(
            BinGroup(setOf(12, 13, 14, 15), listOf(50 to 50, 56 to 58, 6 to 6)),
            BinGroup(
                setOf(16),
                listOf(
                    500000 to 506698, 5068 to 508, 560000 to 589561, 589563 to 589999,
                    6000 to 6010, 601104 to 601104, 60111 to 60111, 601150 to 601173,
                    601175 to 601176, 601180 to 601185, 601200 to 606281, 606283 to 619999,
                    62183 to 62183, 62198 to 62199, 6271 to 6271, 6273 to 6275, 6278 to 6281,
                    6290 to 6290, 629300 to 636296, 636298 to 636367, 636369 to 643999,
                    650600 to 650600, 650610 to 650610, 66 to 69
                )
            ),
            BinGroup(
                setOf(17, 18, 19),
                listOf(
                    50 to 50, 560000 to 589561, 589563 to 589999, 6000 to 6010,
                    601104 to 601104, 60111 to 60111, 601150 to 601173, 601175 to 601176,
                    601180 to 601185, 601200 to 606281, 606283 to 619999, 62183 to 62183,
                    62198 to 62199, 6271 to 6271, 6273 to 6275, 627780 to 627780, 6278 to 6281,
                    6290 to 6290, 6293 to 6439, 650600 to 650600, 650610 to 650610, 66 to 69
                )
            )
        )
    )
}