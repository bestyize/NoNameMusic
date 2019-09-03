package com.yize.wuming;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.yize.wuming.adapter.MusicAdapter;
import com.yize.wuming.control.KwMusic;
import com.yize.wuming.control.MgMusic;
import com.yize.wuming.control.XmMusic;
import com.yize.wuming.model.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private TextView search_notify;
    private RecyclerView rv_search_music_list;
    private LinearLayout ll_show_download_list;
    private SearchView search_view;
    private MusicAdapter musicAdapter;
    private List<SongInfo> songInfoList=new ArrayList<>();
    private static List<SongInfo> allSongInfoList=new ArrayList<>();
    public static KwMusic kwMusic=new KwMusic();
    private static MgMusic mgMusic=new MgMusic();
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SongInfo downloadInfo=(SongInfo) intent.getSerializableExtra("songInfo");
            Log.i("test","Search View 收到一个广播");
            long progress=intent.getLongExtra("progress",0);
            if (progress==100){
                Toast.makeText(getApplicationContext(),"下载成功！",Toast.LENGTH_SHORT).show();
            }else if(progress==-1){
                Toast.makeText(getApplicationContext(),"下载失败！",Toast.LENGTH_SHORT).show();
            }else if(progress==-2){
                Toast.makeText(getApplicationContext(),"暂停下载",Toast.LENGTH_SHORT).show();
            }else if(progress==-3){
                Toast.makeText(getApplicationContext(),"取消下载",Toast.LENGTH_SHORT).show();
            }
            if(downloadInfo!=null){
                musicAdapter.updateProgress(downloadInfo,progress);

            }

        }
    };
    private ItemTouchHelper itemTouchHelper=new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager){
                final int dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
                final int swipeFlags=0;
                return makeMovementFlags(dragFlags,swipeFlags);
            }else {
                final int dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN;
                final int swipeFlags=0;
                return makeMovementFlags(dragFlags,swipeFlags);
            }
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition=viewHolder.getAdapterPosition();
            int toPosition=target.getAdapterPosition();
            int position=viewHolder.getLayoutPosition();
            if(position==0){
                return false;
            }

            musicAdapter.notifyItemMoved(fromPosition,toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView(){
        search_notify=(TextView)findViewById(R.id.search_notify);
        search_notify.setVisibility(View.GONE);
        search_view=(SearchView)findViewById(R.id.search_view);
        ll_show_download_list=(LinearLayout)findViewById(R.id.ll_show_download_list);
        rv_search_music_list=(RecyclerView)findViewById(R.id.rv_search_music_list);
 //       rv_search_music_list.setLayoutManager(new LinearLayoutManager(this));
       rv_search_music_list.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        musicAdapter=new MusicAdapter(songInfoList,1,itemTouchHelper);
        rv_search_music_list.setAdapter(musicAdapter);
//        search_view.setBackgroundColor(getResources().getColor(R.color.colorButtonGreen));
        search_view.setIconified(false);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String keyword) {
                if(songInfoList.size()>0){
                    songInfoList.clear();
                }
                search_notify.setVisibility(View.VISIBLE);
                search_notify.setText("正在为您搜索...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        songInfoList=searchMusic(keyword,50);
                        //songInfoList=searchAllDownloadLink(songInfoList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rv_search_music_list=(RecyclerView)findViewById(R.id.rv_search_music_list);
  //                              rv_search_music_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                rv_search_music_list.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                                musicAdapter=new MusicAdapter(songInfoList,1,itemTouchHelper);
                                rv_search_music_list.setAdapter(musicAdapter);
                                itemTouchHelper.attachToRecyclerView(rv_search_music_list);
                                search_notify.setVisibility(View.GONE);

                            }
                        });
                    }
                }).start();
                musicAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        ll_show_download_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DownloadActivity.class);
                startActivity(intent);
            }
        });

        itemTouchHelper.attachToRecyclerView(rv_search_music_list);
        checkPermission();

        intentFilter=new IntentFilter();
        intentFilter.addAction("com.yize.mdownloader.DOWNLOAD_STATUS_BROADCAST");
        registerReceiver(broadcastReceiver,intentFilter);


    }




    public static List<SongInfo> searchMusic(String keyword,int num){
        int other=num%3;
        List<SongInfo> songInfoList1= new MgMusic().searchMusic(keyword,num/3+other);
        List<SongInfo> songInfoList2= new KwMusic().searchMusic(keyword,num/3);
        List<SongInfo> songInfoList3=new XmMusic().searchMusic(keyword,num/3);
        List<SongInfo> mSongInfoList=new ArrayList<>();
        if(songInfoList1!=null){
            mSongInfoList.addAll(songInfoList1);
        }
        if (songInfoList2!=null){
            mSongInfoList.addAll(songInfoList2);
        }
        if(songInfoList3!=null){
            mSongInfoList.addAll(songInfoList3);
        }


        return mSongInfoList;
    }



    public static List<SongInfo> searchAllDownloadLink(List<SongInfo> songInfoList){
        for(SongInfo songInfo:songInfoList){
            if(songInfo.getSongid().length()<9){
                songInfo=new KwMusic().getDownloadLink(songInfo);
            }else if(songInfo.getSongid().length()>10){
                songInfo=new MgMusic().getDownloadLink(songInfo);
            }
        }
        return songInfoList;
    }

    public static SongInfo searchDownloadLink(SongInfo songInfo){
        if(songInfo.getSongid().length()<9){
            songInfo=new KwMusic().getDownloadLink(songInfo);
        }else if(songInfo.getSongid().length()>10){
            songInfo=new MgMusic().getDownloadLink(songInfo);
        }
        return songInfo;
    }


    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
