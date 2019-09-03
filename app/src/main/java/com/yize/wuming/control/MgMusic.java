package com.yize.wuming.control;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yize.wuming.model.MusicHelper;
import com.yize.wuming.model.SongInfo;




import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

class MiguInfo{
    private String formatType;
    private String url;

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MiguInfo(String formatType, String url) {
        this.formatType = formatType;
        this.url = url;
    }
}

public class MgMusic implements MusicHelper {
    private String songidField="copyrightId";
    private String picUrlField="cover";
    private String qualityField="hasSQqq";


    public void testSearchAll(){
        System.out.println("开始搜索...");
        List<SongInfo> songInfoList=searchMusic("十年",10);
        System.out.println(songInfoList);
        System.out.println("搜索结束...");
        System.out.println("正在获取下载链接...");
        for(SongInfo songInfo:songInfoList){
            songInfo=getDownloadLink(songInfo);
        }
        System.out.println("获取完毕...");
        System.out.println(songInfoList);

    }

    public void testSearchById(){
        SongInfo songInfo=new SongInfo();
        songInfo.setSongid("69910406417");
        songInfo=getDownloadLink(songInfo);
        System.out.println(songInfo.getSqDownloadLink());
    }

    @Override
    public List<SongInfo> searchMusic(String keyword, int num) {
        List<SongInfo> miguMusicInfoList=new ArrayList<>();
        try {
            String link="http://m.music.migu.cn/migu/remoting/scr_search_tag?rows="+num+"&type=2&keyword="+ URLEncoder.encode(keyword,"utf-8") +"&pgc=1";
            String content=downloadWebSite(link);
            content=replaceFieldName(content,songidField,"songid");
            content=replaceFieldName(content,picUrlField,"picUrl");
            content=replaceFieldName(content,qualityField,"quality");
            content=content.substring(content.indexOf("["),content.lastIndexOf("]")+1);
            Gson gson=new Gson();
            miguMusicInfoList=gson.fromJson(content,new TypeToken<List<SongInfo>>(){}.getType());
            for(SongInfo songInfo:miguMusicInfoList){
                songInfo.setSource("Mg");
                if(songInfo.getQuality()!=null&&songInfo.getQuality().equals("1")){
                    songInfo.setQuality("SQ");
                }else {
                    songInfo.setQuality("HQ");
                }
            }
            return miguMusicInfoList;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    @Override
    public SongInfo getDownloadLink(SongInfo songInfo) {

        return songInfo;
    }

    @Override
    public SongInfo getDownloadLink(SongInfo songInfo, String quality) {
        return getDownloadLink(songInfo);
    }

    @Override
    public String downloadWebSite(String website) {
        try {
            URL url=new URL(website);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Mobile Safari/537.36");
            conn.setRequestProperty("Host","m.music.migu.cn");
            BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            StringBuilder sb=new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            reader.close();
            conn.disconnect();
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String replaceFieldName(String info,String field, String newField) {
        return info.replaceAll("\""+field+"\"","\""+newField+"\"");
    }
}