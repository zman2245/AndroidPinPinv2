package com.zman2245.pinpin.adapter.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.zman2245.pinpin.R;

/**
 * ListView adapter for the upgrade menu
 *
 * @author zack
 */
public class AdapterListUpgrade extends BaseAdapter
{
    private final Context        mContext;
    private final LayoutInflater mInflater;

    private UpgradeData[] mData = new UpgradeData[0];

    // Constructor

    public AdapterListUpgrade(Context context, LayoutInflater inflater)
    {
        mContext    = context;
        mInflater   = inflater;
    }

    public void setPurchasedItems(UpgradeData[] data)
    {
        mData = data;

        notifyDataSetChanged();
    }

    // BaseAdapter hooks

    @Override
    public int getCount()
    {
        return mData.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mData[position];
    }

    @Override
    public long getItemId(int position)
    {
        // All are the same
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item_list_upgrade, parent, false);
            holder = new ViewHolder();
            holder.mTitle       = (TextView) convertView.findViewById(R.id.title);
            holder.mSubtitle    = (TextView) convertView.findViewById(R.id.subtitle);
            holder.mPrice       = (TextView) convertView.findViewById(R.id.price);
            holder.mIcon        = (ImageView) convertView.findViewById(R.id.icon);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTitle.setText(mData[position].title);
        holder.mSubtitle.setText(mData[position].description);
        holder.mPrice.setText(mData[position].price);
        holder.mIcon.setImageResource(mData[position].icon);

        return convertView;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return (mData[position].icon == R.drawable.key_orange);
    }

    // ViewHolder helper class

    private static class ViewHolder
    {
        TextView    mTitle;
        TextView    mSubtitle;
        TextView    mPrice;
        ImageView   mIcon;
    }

    // data class
    public static class UpgradeData
    {
        public String productId;
        public String title;
        public String description;
        public String price;
        public int icon;
    }
}
