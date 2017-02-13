package com.blackrubystudio.aipel3.sms;

/**
 * Created by jaewoo on 2017. 2. 7..
 */

public class SmsAddress {
    private static final String[] bankAddress = {"15882100", "15991111", "15888100", "15447200", "15884000", "15776200", "15881688",
            "15889955", "18001111", "15889955", "15778000", "16449999", "15886800"};

    public static boolean CheckAddress(String num) {
        for (String addressNum : bankAddress) {
            if (addressNum.equals(num)) {
                return true;
            }
        }
        return false;
    }
}
