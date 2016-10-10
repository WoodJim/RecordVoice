package com.wusj.recordvoice;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wusj on 2016/10/10.
 */
public class RecorderAdapter extends ArrayAdapter<Recorder> {

    private List<Recorder> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    private int mMinItemWidth;
    private int mMaxItemWidth;



    public RecorderAdapter(Context context, List<Recorder> datas) {
        super(context, -1,datas);
        mContext = context;
        mDatas = datas;
        mInflater = LayoutInflater.from(context);
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);

        mMaxItemWidth = (int) (dm.widthPixels * 0.7f);
        mMinItemWidth = (int) (dm.widthPixels * 0.15f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.lv_recorder_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.seconds = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.length = convertView.findViewById(R.id.fl_recorder_length);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.seconds.setText((int)(mDatas.get(position).getTime()) +"\"");
//        viewHolder.seconds.setText(Math.round(getItem(position).getTime()) +"\"");
        ViewGroup.LayoutParams lp = viewHolder.length.getLayoutParams();
        lp.width = (int) (mMinItemWidth + ( mMaxItemWidth / 60f * getItem(position).time));
        return convertView;
    }

    private class ViewHolder{
        TextView seconds;
        View length;
    }
}
