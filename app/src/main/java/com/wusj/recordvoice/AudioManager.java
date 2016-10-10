package com.wusj.recordvoice;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wusj on 2016/10/10.
 */
public class AudioManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private static AudioManager sInstance;

    private boolean isPrepare = false;

    private AudioManager(String dir){
        this.mDir = dir;
    }


    public static AudioManager getInstance(String dir){
        if(sInstance == null){
            synchronized (AudioManager.class){
                if(sInstance == null){
                    sInstance = new AudioManager(dir);
                }
            }
        }

        return sInstance;
    }

    public interface AudioStateListener{
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setStateListener(AudioStateListener listener){
        this.mListener = listener;
    }

    public void  prepareAudio(){
        isPrepare = false;
        try {
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdir();
            }

            String fileName = generateFileName();
            File file = new File(dir, fileName);

            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepare = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceSize(int maxSize){
        if(isPrepare){
            try{
                return maxSize * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return 1;
    }

    public void realease(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel(){
        realease();
        if(mCurrentFilePath != null){
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;

        }
    }

    public String getCurrentFilePath(){
        return this.mCurrentFilePath;
    }
}
