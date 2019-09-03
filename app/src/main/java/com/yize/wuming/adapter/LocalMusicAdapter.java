package com.yize.wuming.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.yize.wuming.R;
import com.yize.wuming.local.LocalHelper;
import com.yize.wuming.model.SongInfo;

import java.io.File;
import java.util.List;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {
    private List<SongInfo> localSongInfoList;
    private Context context;

    public LocalMusicAdapter(List<SongInfo> localSongInfoList) {
        this.localSongInfoList = localSongInfoList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_loacl_music_quality,tv_local_songname,tv_local_detail;
        Button btn_local_music_share;
        LinearLayout ll_local_music_list_item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_loacl_music_quality=(TextView)itemView.findViewById(R.id.tv_loacl_music_quality);
            tv_local_songname=(TextView)itemView.findViewById(R.id.tv_local_songname);
            tv_local_detail=(TextView)itemView.findViewById(R.id.tv_local_detail);
            btn_local_music_share=(Button)itemView.findViewById(R.id.btn_local_music_share);
            ll_local_music_list_item=(LinearLayout)itemView.findViewById(R.id.ll_local_music_list_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.local_music_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final SongInfo songInfo=localSongInfoList.get(position);
        String quality=songInfo.getQuality();
        if(quality.equals("SQ")){

            String fullPath=LocalHelper.getSongFullPath(songInfo);
            if(fullPath.endsWith(".flac")){
                holder.tv_loacl_music_quality.setTextColor(context.getResources().getColor(R.color.colorButtonGold));
                holder.tv_loacl_music_quality.setText("FLAC");
            }else if(fullPath.endsWith(".wav")){
                holder.tv_loacl_music_quality.setTextColor(context.getResources().getColor(R.color.weixin_green));
                holder.tv_loacl_music_quality.setText("WAV");

            }


        }else {
            holder.tv_loacl_music_quality.setTextColor(context.getResources().getColor(R.color.color_soft_green));
            holder.tv_loacl_music_quality.setText("MP3");
        }
        holder.tv_local_songname.setText(songInfo.getSongName());
        holder.tv_local_detail.setText(songInfo.getSingerName()+"|《"+songInfo.getAlbumName()+"》");
        holder.btn_local_music_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fullPath = LocalHelper.getSongFullPath(songInfo);
                    File file = new File(fullPath);
                    Uri uri = null;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(context, "com.yize.wuming.adapter.fileprovider", file);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setType("audio/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                }catch (Exception e){
                    Log.i("Error:",e.getCause().toString());
                }
            }
        });

        holder.ll_local_music_list_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("delete","删除");
                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.popup_menu_delete){
                            //songInfoList.remove(position);
                            if(LocalHelper.deleteFile(songInfo)){
                                localSongInfoList.remove(position);
                                notifyDataSetChanged();
                                notifyItemRemoved(position);
                            }


                        }
                        return false;
                    }
                });

                popupMenu.show();
                return false;
            }

        });

        holder.ll_local_music_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullPath = LocalHelper.getSongFullPath(songInfo);
                File file = new File(fullPath);
                Uri uri = null;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, "com.yize.wuming.adapter.fileprovider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return localSongInfoList.size();
    }





}
