package com.yize.wuming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yize.wuming.adapter.DownloadAdapter;
import com.yize.wuming.model.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {
    private TextView tv_no_download_notice;
    private IntentFilter intentFilter;
    private RecyclerView rv_download_list;
    private DownloadAdapter downloadAdapter;
    private List<SongInfo> downloadSongList=new ArrayList<>();
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final SongInfo downloadInfo=(SongInfo) intent.getSerializableExtra("songInfo");
            final long progress=intent.getLongExtra("progress",0);
            tv_no_download_notice.setVisibility(View.GONE);
            downloadAdapter.updateProgress(downloadInfo,progress);
            Log.i("DownloadActivity","收到一个广播");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(downloadSongList!=null&&downloadSongList.size()>0){
                                for(SongInfo songInfo:downloadSongList){
                                    if(songInfo.getSongid().equals(downloadInfo.getSongid())){
                                        return;
                                    }
                                }
                            }
                            downloadSongList.add(downloadInfo);

                           // downloadAdapter.addDownloadItem(downloadInfo);
                            downloadAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
    }

    private void initView(){
        tv_no_download_notice=(TextView)findViewById(R.id.tv_no_download_notice);
        rv_download_list=(RecyclerView)findViewById(R.id.rv_download_list);
        rv_download_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        downloadAdapter=new DownloadAdapter(downloadSongList);
        rv_download_list.setAdapter(downloadAdapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.yize.mdownloader.DOWNLOAD_STATUS_BROADCAST");
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
