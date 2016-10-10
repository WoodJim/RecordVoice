package com.wusj.recordvoice;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ListView mListView;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mDatas = new ArrayList<>();
    private AudioRecorderButton mAudioRecorderButton;
    private View mAnimView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.lv_content_list);
        mAudioRecorderButton = (AudioRecorderButton)findViewById(R.id.btn_recorder);
        mAudioRecorderButton.setAudioFinishListener(new AudioRecorderButton.OnAudioFinishRecorderListener() {
            @Override
            public void onFinishRecorder(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds,filePath);
                mDatas.add(recorder);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mDatas.size() - 1);
            }
        });
        mAdapter = new RecorderAdapter(this,mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //todo:play audio and anima
                if(mAnimView != null){
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                mAnimView = view.findViewById(R.id.view_recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_audio_anim);
                final AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();

                MediaManager.playSound(mDatas.get(i).getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mAnimView.setBackgroundResource(R.drawable.adj);
                        anim.stop();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }
}
