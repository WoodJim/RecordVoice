package com.wusj.recordvoice;

import android.media.*;
import android.media.AudioManager;

import java.io.IOException;

/**
 * Created by wusj on 2016/10/10.
 */
public class MediaManager {

    private static MediaPlayer sMediaPlayer;
    private static boolean isPause;

    public static void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener){
        if(sMediaPlayer == null){
            sMediaPlayer = new MediaPlayer();
            sMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    sMediaPlayer.reset();
                    return false;
                }
            });
        }else{
            sMediaPlayer.reset();
        }


        try {
            sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            sMediaPlayer.setOnCompletionListener(onCompletionListener);
            sMediaPlayer.setDataSource(filePath);
            sMediaPlayer.prepare();
            sMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void pause(){
        if(sMediaPlayer != null && sMediaPlayer.isPlaying()){
            sMediaPlayer.pause();
            isPause = true;
        }

    }

    public static void resume(){
        if(sMediaPlayer != null && isPause){
            sMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release(){
        if( sMediaPlayer != null){
            sMediaPlayer.release();
            sMediaPlayer = null;
        }
    }
}
