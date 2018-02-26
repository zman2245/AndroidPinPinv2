package com.zman2245.pinpin.appstate;

import java.util.HashMap;

/**
 * Manages info about in app purchases
 *
 * @author zfoster
 */
public class InAppPurchasesModel
{
    public static boolean TEST = false;

    // magic test productIds
    public static String PURCHASE_TEST_SUCCESS          = "android.test.purchased";
    public static String PURCHASE_TEST_CANCELLED        = "android.test.canceled";
    public static String PURCHASE_TEST_REFUNDED         = "android.test.refunded";
    public static String PURCHASE_TEST_ITEM_UNAVAILABLE = "android.test.item_unavailable";

    // the real productIds
    public static String PURCHASE_AD_FREE = "noads";
    public static String PURCHASE_QUIZZES = "quizzes";

    // req code for the in-app billing/purchasing activity
    public static int PURCHASE_ACTIVITY_REQUEST_CODE = 1001;

    // contains productId to "is purchased"
    public static HashMap<String, Boolean> mPurchasedStatusMap = new HashMap<String, Boolean>();
}
