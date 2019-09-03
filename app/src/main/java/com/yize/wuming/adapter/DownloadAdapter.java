package com.yize.wuming.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yize.wuming.R;
import com.yize.wuming.model.SongInfo;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    private List<SongInfo> songInfoList;
    private Context context;

    public DownloadAdapter(List<SongInfo> songInfoList){
        this.songInfoList=songInfoList;
    }

    @Override
    public int getItemCount() {
        return songInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_download_progress,tv_music_item_songname,tv_music_item_detail;
        Button btn_download_start;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_download_progress=(TextView)itemView.findViewById(R.id.tv_download_progress);
            btn_download_start=(Button)itemView.findViewById(R.id.btn_download_start);
            tv_music_item_songname=(TextView)itemView.findViewById(R.id.tv_music_item_songname);
            tv_music_item_detail=(TextView)itemView.findViewById(R.id.tv_music_item_detail);
        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.download_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SongInfo songInfo=songInfoList.get(position);

        holder.btn_download_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btn_download_start.getText().equals("开始")){

                }else if(holder.btn_download_start.getText().equals("继续")){

                }else if(holder.btn_download_start.getText().equals("暂停")){

                }
            }
        });
        String songname=songInfo.getSongName();
        String singername=songInfo.getSingerName();
        String albumName=songInfo.getAlbumName();
        if(songname.length()>12){
            songname=songname.substring(0,11)+"...";
        }
        if(singername.length()>10){
            singername=singername.substring(0,10)+"...";
        }
        if (albumName.length()>10){
            albumName=albumName.substring(0,9)+"...";
        }
        holder.tv_music_item_songname.setText(songname);
        holder.tv_music_item_detail.setText(singername+"|《"+albumName+"》");
        if(songInfo.getDownloadProgress()==100){
            holder.tv_download_progress.setText("已下载");
        }else if(songInfo.getDownloadProgress()==-1){
            holder.tv_download_progress.setText("下载失败，请重试！");
        }else if(songInfo.getDownloadProgress()==-2){
            holder.tv_download_progress.setText("暂停中...");
        }else if(songInfo.getDownloadProgress()==-3){
            holder.tv_download_progress.setText("已取消");
        }else {
            holder.tv_download_progress.setText("下载进度："+songInfo.getDownloadProgress()+"%");
        }


    }

    public void updateProgress(SongInfo songInfo,long progress){
        for (SongInfo mSongInfo:songInfoList){
            if(mSongInfo.getSongid().equals(songInfo.getSongid())){
                mSongInfo.setDownloadProgress(progress);
                notifyDataSetChanged();
            }
        }
    }





}
