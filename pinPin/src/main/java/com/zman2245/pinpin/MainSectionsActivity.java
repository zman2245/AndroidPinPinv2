package com.zman2245.pinpin;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.vending.billing.IInAppBillingService;
import com.crittercism.app.Crittercism;
import com.flurry.android.FlurryAdListener;
import com.flurry.android.FlurryAdSize;
import com.flurry.android.FlurryAdType;
import com.flurry.android.FlurryAds;
import com.sbstrm.appirater.Appirater;
import com.zman2245.pinpin.appstate.InAppPurchasesModel;
import com.zman2245.pinpin.fragment.FragmentPurchasedQuery;
import com.zman2245.pinpin.fragment.PinBaseFragment;
import com.zman2245.pinpin.fragment.dialog.DialogUpgrade;
import com.zman2245.pinpin.fragment.event.Event;
import com.zman2245.pinpin.fragment.event.FragmentEventListener;
import com.zman2245.pinpin.fragment.tab.FragmentTabLearn;
import com.zman2245.pinpin.fragment.tab.FragmentTabQuiz;
import com.zman2245.pinpin.fragment.tab.FragmentTabReference;
import com.zman2245.pinpin.fragment.tab.TabListener;
import com.zman2245.pinpin.log.EventLog;

/**
 * "Top-level" sections including Learn, Quiz, and Reference Fragments
 *
 * @author zack
 */
public class MainSectionsActivity extends SherlockFragmentActivity implements FragmentEventListener, FlurryAdListener
{
    public static String INAPP_LOG_TAG = "InApp";

    // store the active tab here
    public static String ACTIVE_TAB = "activeTab";

    // ad container
    private FrameLayout mAdContainer;

    private MenuItem mUpgradeMenuItem;

    private FragmentPurchasedQuery mPurchasedQueryFragment;

    // may need to wait
    private final boolean mUpgradeClicked = false;

    // For in-app billing
    private IInAppBillingService mService;
    private final ServiceConnection mServiceConn = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.d(INAPP_LOG_TAG, "onServiceDisconnected");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.d(INAPP_LOG_TAG, "onServiceConnected");

            mService = IInAppBillingService.Stub.asInterface(service);

            if (mService == null)
                throw new NullPointerException("service connected but service is null wtf");

            // purchased query fragment
            mPurchasedQueryFragment.setInAppBillingService(mService);
            getPurchased();

            if (InAppPurchasesModel.TEST)
            {
                String purchaseToken = "inapp:"+getPackageName()+":android.test.purchased";
                try
                {
                    int response = mService.consumePurchase(3, getPackageName(),purchaseToken);
                }
                catch (RemoteException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Crittercism.init(getApplicationContext(), "51e755c3558d6a2d65000004");

        setContentView(R.layout.activity_main_sections);

        mAdContainer = (FrameLayout)findViewById(R.id.ad_container);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab tab2 = actionBar.newTab().setText(R.string.menu_global_nav_learn)
                .setTabListener(new TabListener<FragmentTabLearn>(this, "maintab-learn", FragmentTabLearn.class, R.string.flurry_event_mode_learn));

        actionBar.addTab(tab2);

        Tab tab1 = actionBar.newTab().setText(R.string.menu_global_nav_quiz)
                .setTabListener(new TabListener<FragmentTabQuiz>(this, "maintab-quiz", FragmentTabQuiz.class, R.string.flurry_event_mode_quiz));

        actionBar.addTab(tab1, true);

        Tab tab3 = actionBar.newTab().setText(R.string.menu_global_nav_reference)
                .setTabListener(new TabListener<FragmentTabReference>(this, "maintab-reference", FragmentTabReference.class, R.string.flurry_event_mode_reference));

        actionBar.addTab(tab3);

        // check if there is a saved state to select active tab
        if (savedInstanceState != null)
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(ACTIVE_TAB));
        else
            getSupportActionBar().setSelectedNavigationItem(0);

        // For in-app billing
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        // set action bar color to orange
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.orange_default));
        getSupportActionBar().setIcon(R.drawable.icon_white);

        FlurryAds.setAdListener(this);
//        FlurryAds.enableTestAds(true);

        // set up purchased query fragment
        mPurchasedQueryFragment = (FragmentPurchasedQuery)getSupportFragmentManager().findFragmentByTag("purchased");
        if (mPurchasedQueryFragment == null)
        {
            mPurchasedQueryFragment = FragmentPurchasedQuery.newInstance();
            FragmentTransaction ft  = getSupportFragmentManager().beginTransaction();
            ft.add(mPurchasedQueryFragment, "purchased");
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // save active tab
        outState.putInt(ACTIVE_TAB, getSupportActionBar().getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentByTag("maintab-learn");

        if (frag != null && frag instanceof PinBaseFragment)
        {
            if (((PinBaseFragment) frag).onBackPressed())
                return;
        }

        frag = fm.findFragmentByTag("maintab-reference");

        if (frag != null && frag instanceof PinBaseFragment)
        {
            if (((PinBaseFragment) frag).onBackPressed())
                return;
        }

        frag = fm.findFragmentByTag("maintab-quiz");

        if (frag != null && frag instanceof PinBaseFragment)
        {
            if (((PinBaseFragment) frag).onBackPressed())
                return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_default, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.menu_item_upgrade:
            showStore();
            return true;
        }

        return false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
//
//        if (mServiceConn != null && mService != null)
//        {
//            unbindService(mServiceConn);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == InAppPurchasesModel.PURCHASE_ACTIVITY_REQUEST_CODE)
        {
            int responseCode        = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData     = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature    = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK)
            {
                try
                {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    InAppPurchasesModel.mPurchasedStatusMap.put(sku, true);

                    processPurchasedItems();

                    EventLog.trackEvent(R.string.flurry_event_store_purchase_complete);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                Toast.makeText(this, getString(R.string.toast_upgrade_purchase_success), Toast.LENGTH_LONG).show();
            }
            else
            {
                EventLog.trackEvent(R.string.flurry_event_store_purchase_fail);
                Toast.makeText(this, getString(R.string.toast_upgrade_purchase_failed), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Appirater.appLaunched(this);

        // For ad serving. TODO: if upgraded, hide the ads banner
        FlurryAds.fetchAd(this, "PinPinMainBottom", mAdContainer, FlurryAdSize.BANNER_BOTTOM);

        if (InAppPurchasesModel.mPurchasedStatusMap.size() == 0)
            getPurchased();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        FlurryAds.removeAd(this, "PinPinMainBottom", mAdContainer);
    }

    // private helpers

    private void showStore()
    {
        EventLog.trackEvent(R.string.flurry_event_mode_store);
        DialogUpgrade dialog = new DialogUpgrade();
        dialog.show(getSupportFragmentManager(), "upgrade_dialog");
    }

    private void processPurchasedItems()
    {
        if (InAppPurchasesModel.mPurchasedStatusMap.get(InAppPurchasesModel.PURCHASE_AD_FREE) != null &&
                InAppPurchasesModel.mPurchasedStatusMap.get(InAppPurchasesModel.PURCHASE_AD_FREE))
        {
            FlurryAds.removeAd(this, "PinPinMainBottom", mAdContainer);
            mAdContainer.setVisibility(View.GONE);
        }

        if (InAppPurchasesModel.mPurchasedStatusMap.get(InAppPurchasesModel.PURCHASE_QUIZZES) != null &&
                InAppPurchasesModel.mPurchasedStatusMap.get(InAppPurchasesModel.PURCHASE_QUIZZES))
        {

        }

        if (InAppPurchasesModel.mPurchasedStatusMap.get(InAppPurchasesModel.PURCHASE_TEST_SUCCESS) != null &&
                InAppPurchasesModel.mPurchasedStatusMap.get(InAppPurchasesModel.PURCHASE_TEST_SUCCESS))
        {
            FlurryAds.removeAd(this, "PinPinMainBottom", mAdContainer);
            mAdContainer.setVisibility(View.GONE);
        }
    }

    // FragmentEventListener impl

    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(Event event)
    {
        switch (event.type)
        {
        case INAPP_PURCHASED:
            ArrayList<String> purchasedProdIds = (ArrayList<String>)event.data.get("purchased");

            if (purchasedProdIds.contains(InAppPurchasesModel.PURCHASE_AD_FREE))
                InAppPurchasesModel.mPurchasedStatusMap.put(InAppPurchasesModel.PURCHASE_AD_FREE, true);
            else
                InAppPurchasesModel.mPurchasedStatusMap.put(InAppPurchasesModel.PURCHASE_AD_FREE, false);

            if (purchasedProdIds.contains(InAppPurchasesModel.PURCHASE_QUIZZES))
                InAppPurchasesModel.mPurchasedStatusMap.put(InAppPurchasesModel.PURCHASE_QUIZZES, true);
            else
                InAppPurchasesModel.mPurchasedStatusMap.put(InAppPurchasesModel.PURCHASE_QUIZZES, false);

            if (purchasedProdIds.contains(InAppPurchasesModel.PURCHASE_TEST_SUCCESS))
                InAppPurchasesModel.mPurchasedStatusMap.put(InAppPurchasesModel.PURCHASE_TEST_SUCCESS, true);
            else
                InAppPurchasesModel.mPurchasedStatusMap.put(InAppPurchasesModel.PURCHASE_TEST_SUCCESS, false);

            processPurchasedItems();

            break;

        case INAPP_PURCHASED_FAILED:
            Toast.makeText(this, getString(R.string.toast_upgrade_purchased_items_failed), Toast.LENGTH_LONG).show();
            break;

        case INAPP_SHOW_STORE:
            showStore();
            break;

        default:
        }
    }

    // private helpers

    private void getPurchased()
    {
        mPurchasedQueryFragment.getPurchased(getPackageName());
    }

    // FlurryAdListener impl

    @Override
    public void onAdClicked(String arg0)
    {
        EventLog.trackEvent(R.string.flurry_event_ad_rendered);
        Log.d("Ads", "onAdClicked: " + arg0);
    }

    @Override
    public void onAdClosed(String arg0)
    {
        Log.d("Ads", "onAdClosed: " + arg0);
    }

    @Override
    public void onAdOpened(String arg0)
    {
        Log.d("Ads", "onAdOpened: " + arg0);
    }

    @Override
    public void onApplicationExit(String arg0)
    {
        Log.d("Ads", "onApplicationExit: " + arg0);
    }

    @Override
    public void onRendered(String s) {
        EventLog.trackEvent(R.string.flurry_event_ad_rendered);
        Log.d("Ads", "onRendered: " + s);
    }

    @Override
    public void onRenderFailed(String arg0)
    {
        Log.d("Ads", "onRenderFailed: " + arg0);
    }

    @Override
    public boolean shouldDisplayAd(String arg0, FlurryAdType arg1)
    {
        Log.d("Ads", "shouldDisplayAd (returning true): " + arg0 + ". Type: " + arg1);

        return true;
    }

    @Override
    public void spaceDidFailToReceiveAd(String arg0)
    {
        EventLog.trackEvent("Ad space did fail to receive ad");
        Log.d("Ads", "spaceDidFailToReceiveAd: " + arg0);
    }

    @Override
    public void spaceDidReceiveAd(String arg0)
    {
        EventLog.trackEvent("Ad space did receive ad");

        Log.d("Ads", "spaceDidReceiveAd: " + arg0);

        FlurryAds.displayAd(this, "PinPinMainBottom", mAdContainer);
    }

    @Override
    public void onVideoCompleted(String arg0)
    {
        Log.d("Ads", "onVideoCompleted: " + arg0);
    }
}
