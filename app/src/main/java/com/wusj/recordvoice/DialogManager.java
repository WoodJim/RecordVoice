package com.wusj.recordvoice;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by wusj on 2016/10/10.
 */
public class DialogManager {

    private Dialog mDialog;

    private ImageView mRecorderIcon;
    private ImageView mRecorderVoiceSize;
    private TextView mRecorderTip;

    private Context mContext;

    public DialogManager(Context context){
        this.mContext = context;
    }

    public void showRecorderDialog(){
        mDialog = new Dialog(mContext,R.style.Theme_AudioDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.recorder_dialog,null);
        mDialog.setContentView(view);

        mRecorderIcon = (ImageView) mDialog.findViewById(R.id.iv_recorder);
        mRecorderVoiceSize = (ImageView) mDialog.findViewById(R.id.iv_voice_size);
        mRecorderTip = (TextView) mDialog.findViewById(R.id.tv_recorder_tip);

        mDialog.show();
    }

    public void recording(){
        if(mDialog != null && mDialog.isShowing()){
            mRecorderIcon.setVisibility(View.VISIBLE);
            mRecorderVoiceSize.setVisibility(View.VISIBLE);
            mRecorderTip.setVisibility(View.VISIBLE);

            mRecorderIcon.setImageResource(R.drawable.recorder);
            mRecorderTip.setText("手指上滑，取消发送");
        }
    }

    public void wantToCancel(){
        if(mDialog != null && mDialog.isShowing()){
            mRecorderIcon.setVisibility(View.VISIBLE);
            mRecorderVoiceSize.setVisibility(View.GONE);
            mRecorderTip.setVisibility(View.VISIBLE);

            mRecorderIcon.setImageResource(R.drawable.cancel);
            mRecorderTip.setText("松开手指，取消发送");
        }
    }

    public void tooShort(){
        if(mDialog != null && mDialog.isShowing()){
            mRecorderIcon.setVisibility(View.VISIBLE);
            mRecorderVoiceSize.setVisibility(View.GONE);
            mRecorderTip.setVisibility(View.VISIBLE);

            mRecorderIcon.setImageResource(R.drawable.voice_to_short);
            mRecorderTip.setText("录音时间过短");
        }
    }

    public void dismissDialog(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateRecorderVoiceSize(int size){
        if(mDialog != null && mDialog.isShowing()){
//            mRecorderIcon.setVisibility(View.VISIBLE);
//            mRecorderVoiceSize.setVisibility(View.VISIBLE);
//            mRecorderTip.setVisibility(View.VISIBLE);

            int resId = mContext.getResources().getIdentifier("v" + size, "drawable", mContext.getPackageName());
            mRecorderVoiceSize.setImageResource(resId);
        }
    }

}
