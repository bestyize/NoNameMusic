package com.yize.wuming.local;

import com.yize.wuming.download.DownloadService;
import com.yize.wuming.model.SongInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocalHelper {
    public static List<SongInfo> readLocalMusic(String mfolder){
        File folder=new File(mfolder);
        if(!folder.exists()){
            folder.mkdirs();
        }
        List<SongInfo> songInfoList=new ArrayList<>();
        File[] files=folder.listFiles();
        if(files!=null&&files.length>0){
            for(File file:files){
                SongInfo songInfo=parseSongInfo(file);
                if(songInfo!=null){
                    songInfoList.add(songInfo);
                }
            }
        }

        return songInfoList;
    }
    private static SongInfo parseSongInfo(File file){
        SongInfo songInfo=new SongInfo();
        String fileName=file.getName();
        if(fileName.endsWith(".flac")){
            songInfo.setQuality("SQ");
        }else if(fileName.endsWith(".mp3")){
            songInfo.setQuality("HQ");
        }else if(fileName.endsWith(".wav")){
            songInfo.setQuality("SQ");
        }else {
            return null;
        }
        String str1=fileName.replaceAll("_","**");
        int count=str1.length()-fileName.length();
        if(count!=2){
            return null;
        }
        String songName=fileName.substring(0,fileName.indexOf("_"));
        fileName=fileName.substring(fileName.indexOf("_")+1);
        String singerName=fileName.substring(0,fileName.indexOf("_"));
        fileName=fileName.substring(fileName.indexOf("_")+1);
        String albumName=fileName.substring(0,fileName.lastIndexOf("."));
        songInfo.setSingerName(singerName);
        songInfo.setSongName(songName);
        songInfo.setAlbumName(albumName);
        return songInfo;
    }

    public static String getSongFullPath(SongInfo songInfo){
        String baseFolder= DownloadService.SAVE_FLODER;
        baseFolder+=songInfo.getSongName()+"_"+songInfo.getSingerName()+"_"+songInfo.getAlbumName();
        if(songInfo.getQuality().equals("SQ")){
            baseFolder+=".flac";
            File file=new File(baseFolder);
            if(!file.exists()){
                baseFolder=baseFolder.replaceAll(".flac",".wav");
            }

        }else {
            baseFolder+=".mp3";
        }
        return baseFolder;
    }

    public static List<SongInfo> sortLocalMusicList(List<SongInfo> songInfoList){
        if(songInfoList!=null&&songInfoList.size()>1){
            Collections.sort(songInfoList, new Comparator<SongInfo>() {
                @Override
                public int compare(SongInfo o1, SongInfo o2) {
                    return o1.getSongName().compareTo(o2.getSongName());
                }
            });
        }

        return songInfoList;
    }


    public static boolean deleteFile(SongInfo songInfo){
        File file=new File(getSongFullPath(songInfo));
        try {
            if(file.exists()){
                file.delete();
                return true;
            }
        }catch (Exception e){

        }
        return false;
    }
}
