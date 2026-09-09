package com.aditya.expensetracker.data.model

fun getCurrency() : List<Currency>{
    val currencies = listOf(

        Currency("INR", "Indian Rupee", "₹", "India", "🇮🇳"),
        Currency("USD", "US Dollar", "$", "United States", "🇺🇸"),
        Currency("EUR", "Euro", "€", "European Union", "🇪🇺"),
        Currency("GBP", "British Pound", "£", "United Kingdom", "🇬🇧"),
        Currency("JPY", "Japanese Yen", "¥", "Japan", "🇯🇵"),
        Currency("CNY", "Chinese Yuan", "¥", "China", "🇨🇳"),
        Currency("KRW", "South Korean Won", "₩", "South Korea", "🇰🇷"),
        Currency("AUD", "Australian Dollar", "A$", "Australia", "🇦🇺"),
        Currency("CAD", "Canadian Dollar", "C$", "Canada", "🇨🇦"),
        Currency("CHF", "Swiss Franc", "CHF", "Switzerland", "🇨🇭"),
        Currency("NZD", "New Zealand Dollar", "NZ$", "New Zealand", "🇳🇿"),

        Currency("SGD", "Singapore Dollar", "S$", "Singapore", "🇸🇬"),
        Currency("HKD", "Hong Kong Dollar", "HK$", "Hong Kong", "🇭🇰"),
        Currency("AED", "UAE Dirham", "د.إ", "United Arab Emirates", "🇦🇪"),
        Currency("SAR", "Saudi Riyal", "﷼", "Saudi Arabia", "🇸🇦"),
        Currency("QAR", "Qatari Riyal", "﷼", "Qatar", "🇶🇦"),
        Currency("KWD", "Kuwaiti Dinar", "د.ك", "Kuwait", "🇰🇼"),
        Currency("BHD", "Bahraini Dinar", ".د.ب", "Bahrain", "🇧🇭"),
        Currency("OMR", "Omani Rial", "﷼", "Oman", "🇴🇲"),

        Currency("PKR", "Pakistani Rupee", "₨", "Pakistan", "🇵🇰"),
        Currency("BDT", "Bangladeshi Taka", "৳", "Bangladesh", "🇧🇩"),
        Currency("LKR", "Sri Lankan Rupee", "₨", "Sri Lanka", "🇱🇰"),
        Currency("NPR", "Nepalese Rupee", "₨", "Nepal", "🇳🇵"),
        Currency("BTN", "Bhutanese Ngultrum", "Nu.", "Bhutan", "🇧🇹"),

        Currency("RUB", "Russian Ruble", "₽", "Russia", "🇷🇺"),
        Currency("UAH", "Ukrainian Hryvnia", "₴", "Ukraine", "🇺🇦"),
        Currency("TRY", "Turkish Lira", "₺", "Turkey", "🇹🇷"),
        Currency("PLN", "Polish Zloty", "zł", "Poland", "🇵🇱"),
        Currency("CZK", "Czech Koruna", "Kč", "Czech Republic", "🇨🇿"),
        Currency("HUF", "Hungarian Forint", "Ft", "Hungary", "🇭🇺"),
        Currency("RON", "Romanian Leu", "lei", "Romania", "🇷🇴"),
        Currency("BGN", "Bulgarian Lev", "лв", "Bulgaria", "🇧🇬"),

        Currency("ZAR", "South African Rand", "R", "South Africa", "🇿🇦"),
        Currency("NGN", "Nigerian Naira", "₦", "Nigeria", "🇳🇬"),
        Currency("KES", "Kenyan Shilling", "KSh", "Kenya", "🇰🇪"),
        Currency("GHS", "Ghanaian Cedi", "₵", "Ghana", "🇬🇭"),
        Currency("MAD", "Moroccan Dirham", "د.م.", "Morocco", "🇲🇦"),
        Currency("EGP", "Egyptian Pound", "£", "Egypt", "🇪🇬"),

        Currency("BRL", "Brazilian Real", "R$", "Brazil", "🇧🇷"),
        Currency("ARS", "Argentine Peso", "$", "Argentina", "🇦🇷"),
        Currency("CLP", "Chilean Peso", "$", "Chile", "🇨🇱"),
        Currency("COP", "Colombian Peso", "$", "Colombia", "🇨🇴"),
        Currency("PEN", "Peruvian Sol", "S/", "Peru", "🇵🇪"),

        Currency("MXN", "Mexican Peso", "$", "Mexico", "🇲🇽"),
        Currency("GTQ", "Guatemalan Quetzal", "Q", "Guatemala", "🇬🇹"),
        Currency("CRC", "Costa Rican Colon", "₡", "Costa Rica", "🇨🇷"),
        Currency("DOP", "Dominican Peso", "RD$", "Dominican Republic", "🇩🇴"),

        Currency("THB", "Thai Baht", "฿", "Thailand", "🇹🇭"),
        Currency("MYR", "Malaysian Ringgit", "RM", "Malaysia", "🇲🇾"),
        Currency("IDR", "Indonesian Rupiah", "Rp", "Indonesia", "🇮🇩"),
        Currency("PHP", "Philippine Peso", "₱", "Philippines", "🇵🇭"),
        Currency("VND", "Vietnamese Dong", "₫", "Vietnam", "🇻🇳"),

        Currency("ILS", "Israeli New Shekel", "₪", "Israel", "🇮🇱"),
        Currency("JOD", "Jordanian Dinar", "د.ا", "Jordan", "🇯🇴"),
        Currency("LBP", "Lebanese Pound", "ل.ل", "Lebanon", "🇱🇧"),

        Currency("ISK", "Icelandic Krona", "kr", "Iceland", "🇮🇸"),
        Currency("NOK", "Norwegian Krone", "kr", "Norway", "🇳🇴"),
        Currency("SEK", "Swedish Krona", "kr", "Sweden", "🇸🇪"),
        Currency("DKK", "Danish Krone", "kr", "Denmark", "🇩🇰"),

        Currency("RSD", "Serbian Dinar", "дин", "Serbia", "🇷🇸"),
        Currency("ALL", "Albanian Lek", "Lek", "Albania", "🇦🇱"),
        Currency("GEL", "Georgian Lari", "₾", "Georgia", "🇬🇪"),
        Currency("AMD", "Armenian Dram", "֏", "Armenia", "🇦🇲"),
        Currency("AZN", "Azerbaijani Manat", "₼", "Azerbaijan", "🇦🇿"),

        Currency("KZT", "Kazakhstani Tenge", "₸", "Kazakhstan", "🇰🇿"),
        Currency("UZS", "Uzbekistani Som", "so'm", "Uzbekistan", "🇺🇿"),
        Currency("MNT", "Mongolian Tugrik", "₮", "Mongolia", "🇲🇳"),

        Currency("ETB", "Ethiopian Birr", "Br", "Ethiopia", "🇪🇹"),
        Currency("TZS", "Tanzanian Shilling", "TSh", "Tanzania", "🇹🇿"),
        Currency("UGX", "Ugandan Shilling", "USh", "Uganda", "🇺🇬"),
        Currency("ZMW", "Zambian Kwacha", "ZK", "Zambia", "🇿🇲"),

        Currency("JMD", "Jamaican Dollar", "J$", "Jamaica", "🇯🇲"),
        Currency("TTD", "Trinidad and Tobago Dollar", "TT$", "Trinidad and Tobago", "🇹🇹"),
        Currency("BBD", "Barbadian Dollar", "Bds$", "Barbados", "🇧🇧"),

        Currency("FJD", "Fijian Dollar", "FJ$", "Fiji", "🇫🇯"),
        Currency("PGK", "Papua New Guinean Kina", "K", "Papua New Guinea", "🇵🇬"),
        Currency("WST", "Samoan Tala", "WS$", "Samoa", "🇼🇸")
    )
    return currencies
}
