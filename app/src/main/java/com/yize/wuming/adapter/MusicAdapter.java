package com.yize.wuming.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.yize.wuming.R;
import com.yize.wuming.download.DownloadService;
import com.yize.wuming.model.SongInfo;

import java.util.List;
import java.util.Random;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private Context context;
    private int layoutStyle;
    private List<SongInfo> songInfoList;
    private ItemTouchHelper itemTouchHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_music_list_item,ll_music_list_item_header;
        TextView tv_music_item_songname,tv_music_item_detail,tv_music_item_progress;
        Button btn_download_mp3,btn_download_flac,btn_download_mv;
        public ViewHolder(@NonNull View view) {
            super(view);
            ll_music_list_item=(LinearLayout)view.findViewById(R.id.ll_music_list_item);
            ll_music_list_item_header=(LinearLayout)view.findViewById(R.id.ll_music_list_item_header);
            tv_music_item_songname=(TextView)view.findViewById(R.id.tv_music_item_songname);
            tv_music_item_detail=(TextView)view.findViewById(R.id.tv_music_item_detail);
            btn_download_mp3=(Button)view.findViewById(R.id.btn_download_mp3);
            btn_download_flac=(Button)view.findViewById(R.id.btn_download_flac);
            btn_download_mv=(Button)view.findViewById(R.id.btn_download_mv);
            tv_music_item_progress=(TextView)view.findViewById(R.id.tv_music_item_progress);
        }
    }
    public MusicAdapter(List<SongInfo> songInfoList, int layoutStyle, ItemTouchHelper itemTouchHelper){
        this.songInfoList=songInfoList;
        this.layoutStyle=layoutStyle;
        this.itemTouchHelper=itemTouchHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view=null;
        if(layoutStyle==0){
            view= LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        }else if(layoutStyle==1){
            view= LayoutInflater.from(context).inflate(R.layout.music_item_card_style,parent,false);
        }

        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SongInfo songInfo=songInfoList.get(position);
        holder.tv_music_item_songname.setText(songInfo.getSongName());
        holder.tv_music_item_detail.setText(songInfo.getSingerName()+"\n"+songInfo.getAlbumName());
        holder.btn_download_mp3.setText("HQ");
        if(songInfo.getQuality().equals("SQ")){
            holder.btn_download_flac.setVisibility(View.VISIBLE);
            holder.btn_download_flac.setText("SQ");
            //holder.ll_music_list_item_header.setBackgroundColor(context.getResources().getColor(R.color.color_soft_red));
        }else{
            holder.btn_download_flac.setVisibility(View.GONE);
            //holder.ll_music_list_item_header.setBackgroundColor(context.getResources().getColor(R.color.color_soft_green));
        }
        holder.ll_music_list_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemTouchHelper.startDrag(holder);
                return false;
            }
        });
//        holder.ll_music_list_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu=new PopupMenu(context,v);
//                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        if(item.getItemId()==R.id.popup_menu_delete){
//                            //songInfoList.remove(position);
//                            songInfoList.remove(position);
//                            notifyDataSetChanged();
//                            notifyItemRemoved(position);
//
//                        }
//                        if(item.getItemId()==R.id.popup_menu_start){
//                            Intent intent=new Intent(context,DownloadActivity.class);
//                            context.startActivity(intent);
//
//                        }
//                        if(item.getItemId()==R.id.popup_menu_stop){
////                            Intent intent=new Intent(context, DownloadService.class);
////                            context.stopService(intent);
//
//                        }
//                        return false;
//                    }
//                });
//               popupMenu.show();
//            }
//        });
        switchStyle(holder,0);
        holder.btn_download_flac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tv_music_item_progress.setVisibility(View.VISIBLE);
                Intent intent=new Intent(context, DownloadService.class);
                intent.setAction("downloadAction");
                intent.putExtra("songInfo",songInfo);
                intent.putExtra("musicQuality","SQ");
                context.startService(intent);
            }
        });
        holder.btn_download_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tv_music_item_progress.setVisibility(View.VISIBLE);
                Intent intent=new Intent(context, DownloadService.class);
                intent.setAction("downloadAction");
                intent.putExtra("songInfo",songInfo);
                intent.putExtra("musicQuality","HQ");
                context.startService(intent);
            }
        });

        holder.tv_music_item_progress.setText("下载进度："+songInfo.getDownloadProgress()+"%");


    }

    @Override
    public int getItemCount() {
        return songInfoList.size();
    }

    private void switchStyle(ViewHolder holder,int style){
        if(style==1){
            holder.tv_music_item_detail.setTextColor(context.getResources().getColor(R.color.colorWhite));
            holder.tv_music_item_songname.setTextColor(context.getResources().getColor(R.color.colorWhite));
            int[] colors={context.getResources().getColor(R.color.color_soft_green),
                    context.getResources().getColor(R.color.color_soft_blue),
                    context.getResources().getColor(R.color.color_soft_red),
                    context.getResources().getColor(R.color.color_soft_yellow),
                    context.getResources().getColor(R.color.color_soft_pink),
                    context.getResources().getColor(R.color.color_soft_purple)};
            int ran=new Random().nextInt(colors.length);
            holder.ll_music_list_item.setBackgroundColor(colors[ran]);
        }else if(style==0){
            //holder.tv_music_item_detail.setTextColor(context.getResources().getColor(R.color.color_soft_blue));
            //holder.tv_music_item_songname.setTextColor(context.getResources().getColor(R.color.colorButtonGreen));
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
