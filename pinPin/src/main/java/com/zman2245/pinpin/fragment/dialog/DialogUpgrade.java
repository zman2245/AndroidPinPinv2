package com.zman2245.pinpin.fragment.dialog;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.zman2245.pinpin.R;
import com.zman2245.pinpin.adapter.list.AdapterListUpgrade;
import com.zman2245.pinpin.adapter.list.AdapterListUpgrade.UpgradeData;
import com.zman2245.pinpin.appstate.InAppPurchasesModel;
import com.zman2245.pinpin.log.EventLog;

/**
 * A dialog for upgrade purchases
 *
 * @author zfoster
 */
public class DialogUpgrade extends DialogFragment
{
    private View                    mLoadingView;
    private ListView                mList;
    private AdapterListUpgrade      mAdapter;
    private QuerySkuInfoAsyncTask   mInfoTask;

    // For in-app billing
    private IInAppBillingService    mService;
    private final ServiceConnection mServiceConn = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mService = IInAppBillingService.Stub.asInterface(service);

            mInfoTask = new QuerySkuInfoAsyncTask();
            mInfoTask.execute((Object[])null);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // For in-app billing
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dialog_upgrade, container, false);

        mLoadingView    = rootView.findViewById(R.id.loading);
        mList           = (ListView)rootView.findViewById(R.id.list_upgrade);

        mLoadingView.setVisibility(View.VISIBLE);

        mAdapter = new AdapterListUpgrade(getActivity(), inflater);

        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                EventLog.trackEvent(R.string.flurry_event_store_purchase_start);

                UpgradeData upgradeData = (UpgradeData)mAdapter.getItem(position);

                mLoadingView.setVisibility(View.VISIBLE);

                PurchaseItemAsyncTask purchaseTask = new PurchaseItemAsyncTask();

                if (InAppPurchasesModel.TEST)
                    purchaseTask.execute(InAppPurchasesModel.PURCHASE_TEST_SUCCESS);
                else
                    purchaseTask.execute(upgradeData.productId);
            }
        });

        getDialog().setTitle(R.string.title_upgrade_dialog);

        EventLog.trackEvent(R.string.flurry_event_store);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.upgrade_dialog_height));
    }

    @Override
    public void onDestroyView()
    {
        if (getDialog() != null && getRetainInstance())
        {
            // Work around for a bug in the support library. However, this still
            // does not seem to fix the dialog dismissing during orientation
            // change. See:
            // http://stackoverflow.com/questions/8235080/fragments-dialogfragment-and-screen-rotation
            getDialog().setDismissMessage(null);
            getDialog().setOnDismissListener(null);
        }

        super.onDestroyView();
    }

    /**
     * Task to get details of in-app items
     *
     * @author zfoster
     */
    private class QuerySkuInfoAsyncTask extends AsyncTask<Object, Object, UpgradeData[]>
    {
        @Override
        protected UpgradeData[] doInBackground(Object... params)
        {
            UpgradeData[] upgradeData = null;

            ArrayList<String> skuList = new ArrayList<String>();
            skuList.add(InAppPurchasesModel.PURCHASE_AD_FREE);
            skuList.add(InAppPurchasesModel.PURCHASE_QUIZZES);
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

            try
            {
                if (mService == null || getActivity() == null)
                    return upgradeData;

                Bundle skuDetails = mService.getSkuDetails(3, getActivity().getPackageName(), "inapp", querySkus);

                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0)
                {
                    ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                    upgradeData = new UpgradeData[responseList.size()];
                    int i = 0;

                    for (String thisResponse : responseList)
                    {
                        upgradeData[i] = new UpgradeData();
                        JSONObject object = new JSONObject(thisResponse);

                        // take off the "(Pin Pin)" from the title
                        String title = object.getString("title");
                        if (title.contains("(Pin Pin)"))
                            title = title.substring(0, title.length() - 10);

                        upgradeData[i].title        = title;
                        upgradeData[i].productId    = object.getString("productId");
                        upgradeData[i].price        = object.getString("price");
                        upgradeData[i].description  = object.getString("description");

                        if ((InAppPurchasesModel.TEST &&
                              InAppPurchasesModel.mPurchasedStatusMap.get(InAppPurchasesModel.PURCHASE_TEST_SUCCESS) != null &&
                              InAppPurchasesModel.mPurchasedStatusMap.get(InAppPurchasesModel.PURCHASE_TEST_SUCCESS))
                              ||
                                (InAppPurchasesModel.mPurchasedStatusMap.get(upgradeData[i].productId) != null &&
                                InAppPurchasesModel.mPurchasedStatusMap.get(upgradeData[i].productId)))
                        {
                            upgradeData[i].icon = R.drawable.unlock;
                            upgradeData[i].price = getString(R.string.purchased);
                        }
                        else
                        {
                            upgradeData[i].icon = R.drawable.key_orange;
                        }

                        i++;
                    }
                }
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return upgradeData;
        }

        @Override
        protected void onPostExecute(UpgradeData[] upgradeData)
        {
            if (!isAdded())
                return;

            mLoadingView.setVisibility(View.GONE);

            if (upgradeData == null || upgradeData.length <= 0)
            {
                Toast.makeText(getActivity(), getString(R.string.toast_upgrade_info_failed), Toast.LENGTH_LONG).show();
                return;
            }

            mAdapter.setPurchasedItems(upgradeData);
        }
    }

    /**
     * Task to purchase an item
     *
     * @author zfoster
     */
    private class PurchaseItemAsyncTask extends AsyncTask<String, Object, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params)
        {
            String prodId = params[0];

            try
            {
                Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(), prodId, "inapp", "");

                int responseCode = buyIntentBundle.getInt("RESPONSE_CODE");

                if (responseCode == 0)
                {
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                    getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                            InAppPurchasesModel.PURCHASE_ACTIVITY_REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                            Integer.valueOf(0));

                    return true;
                }
                else
                {
                    EventLog.trackEvent(R.string.flurry_event_store_purchase_fail);
                }
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
            catch (SendIntentException e)
            {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            mLoadingView.setVisibility(View.GONE);

            if (result)
            {
                dismiss();
            }
            else
            {
                EventLog.trackEvent(R.string.flurry_event_store_purchase_fail);
                Toast.makeText(getActivity(), getString(R.string.toast_upgrade_purchase_failed), Toast.LENGTH_LONG).show();
            }
        }
    }
}