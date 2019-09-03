package com.yize.wuming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yize.wuming.adapter.LocalMusicAdapter;
import com.yize.wuming.download.DownloadService;
import com.yize.wuming.local.LocalHelper;
import com.yize.wuming.model.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView rv_local_music_list;
    private LocalMusicAdapter localMusicAdapter;
    private List<SongInfo> songInfoList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView(){
        rv_local_music_list=(RecyclerView)findViewById(R.id.rv_local_music_list);
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bottom_item_search:
                        Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.bottom_item_my:
                        //Toast.makeText(getApplicationContext(),"我的",Toast.LENGTH_SHORT).show();
                        Intent my_intent=new Intent(getApplicationContext(),MyActivity.class);
                        startActivity(my_intent);
                        break;
                    case R.id.bottom_item_file:
                        //Toast.makeText(getApplicationContext(),"文件",Toast.LENGTH_SHORT).show();
                        break;
                    default :
                        break;

                }
                return false;
            }
        });

        songInfoList= LocalHelper.readLocalMusic(DownloadService.SAVE_FLODER);
        songInfoList=LocalHelper.sortLocalMusicList(songInfoList);
        localMusicAdapter=new LocalMusicAdapter(songInfoList);
        rv_local_music_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_local_music_list.setAdapter(localMusicAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity","onResume");
        songInfoList= LocalHelper.readLocalMusic(DownloadService.SAVE_FLODER);
        songInfoList=LocalHelper.sortLocalMusicList(songInfoList);
        localMusicAdapter=new LocalMusicAdapter(songInfoList);
        rv_local_music_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_local_music_list.setAdapter(localMusicAdapter);

    }


}
