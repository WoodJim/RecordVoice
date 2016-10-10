package com.wusj.recordvoice;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by wusj on 2016/10/10.
 */
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener{


    private static final int STATE_NORMAL = 1 ;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;

    private static final int DISTANCE_Y_CANCEL = 50;

    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false ;
    private boolean isLongClick = false ;
    private float mTime = 0f;

    private DialogManager mDialogManager;
    private AudioManager mAudioManager;

    public AudioRecorderButton(Context context) {
        this(context,null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());

        String dir = Environment.getExternalStorageDirectory() + "/imocc_recorder_audios";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //todo:should check the audio and sd right before call prepareAudio
                isLongClick = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    private static final int  MSG_AUDIO_PREPARED = 0X100;
    private static final int  MSG_VOICE_CHANGE = 0X101;
    private static final int MSG_DIALOG_DISMISS = 0X102;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    mDialogManager.showRecorderDialog();
                    isRecording = true;
                    new Thread(mGetVoiceSizeRunnable).start();
                    break;
                case MSG_VOICE_CHANGE:
                    mDialogManager.updateRecorderVoiceSize(mAudioManager.getVoiceSize(7));
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    private Runnable mGetVoiceSizeRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording){
                try {
                    mTime += 0.1f;
                    Thread.sleep(100);
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public interface OnAudioFinishRecorderListener{
        void onFinishRecorder(float seconds,String filePath);
    }

    private OnAudioFinishRecorderListener mAudioFinishListener;

    public void setAudioFinishListener(OnAudioFinishRecorderListener listener){
        this.mAudioFinishListener = listener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRecording){
                    if(wantToCancel(x,y)){
                        changeState(STATE_WANT_CANCEL);
                    }else{
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if(!isLongClick){
                    reset();
                    return super.onTouchEvent(event);
                }

                if (!isRecording || mTime < 0.6f) {
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageAtTime(MSG_DIALOG_DISMISS, 1000);
                } else if (mCurState == STATE_RECORDING) {
                    mDialogManager.dismissDialog();
                    mAudioManager.realease();
                    if(mAudioFinishListener != null){
                        mAudioFinishListener.onFinishRecorder(mTime,mAudioManager.getCurrentFilePath());
                    }
                    // release
                    // callBack
                } else if (mCurState == STATE_WANT_CANCEL) {
                    // cancel
                    mDialogManager.dismissDialog();
                    mAudioManager.cancel();
                }
//                changeState(STATE_NORMAL);


                reset();
                break;
            default:
                return super.onTouchEvent(event);
        }
        //todo:
        return super.onTouchEvent(event);
    }

    private void reset() {
        changeState(STATE_NORMAL);
        isRecording = false;
        isLongClick = false;
        mTime = 0f;
    }

    private boolean wantToCancel(int x, int y) {

        if(x < 0 || x > getWidth()){
            return true;
        }

        if(y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL){
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if(mCurState != state){
            mCurState = state;
            switch (state){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_recording);
                    if(isRecording){
                        //todo show the dialog.recording
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_cancel);
                    setText(R.string.str_recorder_want_cancel);
                    //todo Dialog.wantCancel
                    mDialogManager.wantToCancel();
                    break;
                default:
                    break;
            }
        }
    }

}
