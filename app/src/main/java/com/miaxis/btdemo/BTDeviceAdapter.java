package com.miaxis.btdemo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xu.nan on 2017/8/7.
 */

public class BTDeviceAdapter extends RecyclerView.Adapter<BTDeviceAdapter.ViewHolder> {

    private List<BluetoothDevice> resultList;
    private Context mContext;
    private OnItemListener listener;

    public BTDeviceAdapter(List<BluetoothDevice> resultList, Context mContext) {
        this.resultList = resultList;
        this.mContext = mContext;
    }

    public void setListener(OnItemListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_bt_device, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BluetoothDevice btDevice = resultList.get(position);
        holder.tvDeviceName.setText(btDevice.getName());
        holder.tvDeviceAddress.setText(btDevice.getAddress());
    }

    @Override
    public int getItemCount() {
        if (resultList == null) {
            return 0;
        }
        return resultList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_device_name)
        TextView tvDeviceName;
        @BindView(R.id.tv_device_rssi)
        TextView tvDeviceRssi;
        @BindView(R.id.tv_device_mac)
        TextView tvDeviceAddress;

        private OnItemListener listener;

        ViewHolder(View view, OnItemListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            this.listener = listener;
        }

        @OnClick(R.id.ll_device)
        void onItemClick(View view) {
            if (listener != null) {
                listener.onItemClick(view, getPosition());
            }
        }

    }

    public interface OnItemListener {
        void onItemClick(View view, int position);
    }

}
