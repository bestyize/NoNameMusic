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
import java.util.Map;
import java.util.Random;

import static com.yize.wuming.utils.Algo.convertToMd5;


class XiamiInfo{
    private String quality;
    private String listenFile;

    public XiamiInfo(String quality, String listenFile) {
        this.quality = quality;
        this.listenFile = listenFile;
    }

    public XiamiInfo() {
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getListenFile() {
        return listenFile;
    }

    public void setListenFile(String listenFile) {
        this.listenFile = listenFile;
    }
}



public class XmMusic implements MusicHelper {
    private static String _xmToken = "";
    private static String _xmCookie = "";

    class XiamiInfoExt extends SongInfo {
        private List<XiamiInfo> listenFiles;
        private String lyricFile;

        public List<XiamiInfo> getListenFiles() {
            return listenFiles;
        }

        public void setListenFiles(List<XiamiInfo> listenFiles) {
            this.listenFiles = listenFiles;
        }

        public String getLyricFile() {
            return lyricFile;
        }

        public void setLyricFile(String lyricFile) {
            this.lyricFile = lyricFile;
        }
    }

    @Override
    public List<SongInfo> searchMusic(String keyword, int num) {
        String method = "mtop.alimusic.search.searchservice.searchsongs";
        String model = "\\\"model\\\":{\\\"key\\\":\\\"" + keyword.replaceAll("\"", "") + "\\\",\\\"pagingVO\\\":{\\\"page\\\":" + 1 + ",\\\"pageSize\\\":" + num + "}}";
        String webContent= downloadWebSite(getXmUrl(method,model));
        webContent=webContent.substring(webContent.indexOf("\"songs\":[")+"\"songs\":[".length()-1);
        webContent=webContent.substring(0,webContent.lastIndexOf("]},")+1);
        webContent=replaceFieldName(webContent,"singers","singerName");
        webContent=replaceFieldName(webContent,"songId","songid");
        webContent=replaceFieldName(webContent,"artistLogo","picUrl");
        webContent=webContent.replaceAll("\"songid\":","\"songid\":\"").replaceAll(",\"songName\"","\",\"songName\"");
        webContent=webContent.replaceAll("\"lyricInfo\":\\{","");
        webContent=webContent.replaceAll("\\},\"musicType\"",",\"musicType\"");
        webContent=replaceFieldName(webContent,"lyricFile","lrcUrl");
       // System.out.println(webContent);
        Gson gson=new Gson();
        List<XiamiInfoExt> songInfoListExt=gson.fromJson(webContent,new TypeToken<List<XiamiInfoExt>>(){}.getType());
        List<SongInfo> songInfoList=new ArrayList<>();
        for(XiamiInfoExt xiamiInfoExt:songInfoListExt){
            String songName=xiamiInfoExt.getSongName();
            String singerName=xiamiInfoExt.getSingerName();
            String albumName=xiamiInfoExt.getAlbumName();
            String songid=xiamiInfoExt.getSongid();
            String lrcUrl=xiamiInfoExt.getLrcUrl();

            String picUrl=xiamiInfoExt.getPicUrl();
            if(xiamiInfoExt.getListenFiles()!=null){
                String quality="HQ";
                //Log.i("getListenFiles",xiamiInfoExt.getListenFiles().toString());
                if(xiamiInfoExt.getListenFiles().size()<4){
                    quality="HQ";
                }else {
                    for (XiamiInfo xiamiInfo:xiamiInfoExt.getListenFiles()){
                        if(xiamiInfo.getQuality().contains("s")){
                            quality="SQ";
                        }
                    }
                }
                SongInfo songInfo=new SongInfo(songid,songName,singerName,albumName,quality);
                songInfo.setPicUrl(picUrl);
                songInfo.setLrcUrl(lrcUrl);
                songInfo.setSource("Xm");
                songInfoList.add(songInfo);
            }
        }
        return songInfoList;
    }

    @Override
    public SongInfo getDownloadLink(SongInfo songInfo) {

        return songInfo;
    }

    @Override
    public SongInfo getDownloadLink(SongInfo songInfo, String quality) {
        return null;
    }

    @Override
    public String downloadWebSite(String website) {
        try {
            URL url=new URL(website);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            conn.setRequestProperty("Pragma","no-cache");
            conn.setRequestProperty("Cache-Control","no-cache");
            conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
            conn.setRequestProperty("Accept","/*");
            conn.setRequestProperty("Connection","keep-alive");
            conn.setRequestProperty("Cookie",_xmCookie.replaceAll("yize_uid",getUid()));
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





    private static void updateXmToken()
    {
        //cna="mwGIFQabwA8CAdvoIVtV7N8b"; uidXM=14345449; _m_h5_tk=b9ee042ad4b616035d7864a2d41b8842_1560358851910; _m_h5_tk_enc=281d3d8188785edaf1b45e3f0f9fb19a

        long time = System.currentTimeMillis();
        String data = "{\"requestStr\":\"{\\\"header\\\":{\\\"platformId\\\":\\\"h5\\\",\\\"callId\\\":" + time + ",\\\"appVersion\\\":1000000,\\\"resolution\\\":\\\"600*1067\\\"},\\\"model\\\":{\\\"listId\\\":\\\"873784428\\\",\\\"isFullTags\\\":false,\\\"pagingVO\\\":{\\\"pageSize\\\":1000,\\\"page\\\":1}}}\"}";
        String signData = "" + "&" + time + "&23649156&" + data;
        String sign = convertToMd5(signData);
        String url = "https://h5api.m.xiami.com/h5/mtop.alimusic.music.list.collectservice.getcollectdetail/1.0/?jsv=2.4.0&appKey=23649156&t="+time+"&sign="+sign+"&api=mtop.alimusic.music.list.collectservice.getcollectdetail&v=1.0&type=originaljsonp&timeout=200000&dataType=originaljsonp&closeToast=true&callback=mtopjsonp1&data=%7B%22requestStr%22%3A%22%7B%5C%22header%5C%22%3A%7B%5C%22platformId%5C%22%3A%5C%22h5%5C%22%2C%5C%22callId%5C%22%3A"+time+"%2C%5C%22appVersion%5C%22%3A1000000%2C%5C%22resolution%5C%22%3A%5C%221920*375%5C%22%7D%2C%5C%22model%5C%22%3A%7B%5C%22listId%5C%22%3A%5C%22873784428%5C%22%2C%5C%22isFullTags%5C%22%3Afalse%2C%5C%22pagingVO%5C%22%3A%7B%5C%22pageSize%5C%22%3A1000%2C%5C%22page%5C%22%3A1%7D%7D%7D%22%7D";
        String cookie =getXiaMiCookie(url);
        if (cookie==null||cookie=="")
        {
            return;
        }
        cookie=cookie.replaceAll("\\[","").replaceAll("\\]","");
        String[] cookies=cookie.split(";");
        String tempCookie = "";
        for(String c:cookies){
            if(c.contains("_m_h5_tk=")){
                _xmToken=c.substring(c.indexOf("_m_h5_tk=")+"_m_h5_tk=".length(),c.lastIndexOf("_"));
            }
            tempCookie += (c + ";");
        }

        String cnaCookie = getXiaMiCookie("http://log.mmstat.com/eg.js");
        if (cnaCookie==null||cnaCookie=="")
        {
            return;
        }
        cnaCookie=cnaCookie.replaceAll("\\[","").replaceAll("\\]","");
        String[] canCookies=cnaCookie.split(";");

        String cna="";
        for(String c:canCookies){
            if(c.contains("cna=")){
                cna=c.replaceAll("cna=","");
            }
        }
        _xmCookie = tempCookie.replaceAll("Path=/", "");
        _xmCookie="cna=\""+cna+"\";uidXM=yize_uid;"+_xmCookie;
    }

    private static String getXmUrl(String method,String model)
    {
        updateXmToken();

        String data;
        long time = System.currentTimeMillis();
        data = "{\"requestStr\":\"{\\\"header\\\":{\\\"platformId\\\":\\\"h5\\\",\\\"callId\\\":" + time + ",\\\"appVersion\\\":1000000,\\\"appId\\\":200,\\\"openId\\\":0,\\\"resolution\\\":\\\"2560x1440\\\"}," + model + "}\"}";
        String signData = _xmToken + "&" + time + "&23649156&" + data;
        String sign = convertToMd5(signData);
        try {
            String url ="https://acs.m.xiami.com/h5/" + method + "/1.0/?jsv=2.4.0&appKey=23649156&t=" + time + "&sign=" + sign + "&v=1.0&AntiCreep=true&AntiFlood=true&type=originaljson&dataType=originaljsonp&api=" + method + "&data=" + URLEncoder.encode(data,"utf-8");
            return url;
        }catch (Exception e){
        }
        return "";
    }

    private static String getXiaMiCookie(String website){
        try {
            URL url=new URL(website);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) XIAMI-MUSIC/3.0.2 Chrome/51.0.2704.106 Electron/1.2.8 Safari/537.36");
            conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            Map<String, List<String>> cookie=conn.getHeaderFields();
            conn.disconnect();
            return cookie.get("Set-Cookie").toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static String getUid(){
        Random ran=new Random();
        return String.valueOf(6000000+ran.nextInt(6000000));
    }
}