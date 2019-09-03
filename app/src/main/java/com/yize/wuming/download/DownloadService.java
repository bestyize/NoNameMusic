package com.yize.wuming.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.yize.wuming.control.KwMusic;
import com.yize.wuming.control.MgMusic;
import com.yize.wuming.model.SongInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import sun.BASE64Encoder;

public class DownloadService extends Service {
    public static final String SAVE_FLODER= Environment.getExternalStorageDirectory()+ File.separator+"atter"+File.separator;
    private Context context;
    Intent intent1=new Intent("com.yize.mdownloader.DOWNLOAD_STATUS_BROADCAST");
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        Log.i("downloadService","onCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(intent.getAction()!=null){
            Toast.makeText(getApplicationContext(),"开始下载",Toast.LENGTH_SHORT).show();
            final SongInfo downloadInfo=(SongInfo)intent.getSerializableExtra("songInfo");
            final String musicQuality=intent.getStringExtra("musicQuality");
            Log.i("download serivce","start download");
            new Thread(new Runnable() {
                @Override
                public synchronized void run() {
                        try {
                            Log.i("send broadcast","开启任务+1");
                            Thread.sleep(1000);

                            //intent1.setComponent(new ComponentName("com.yize.mdownloader","com.yize.mdownloader.DownloadBroadcastReceiver"));

                            DownloadListener listener=new DownloadListener() {
                                @Override
                                public void onProgress(Long progress) {
                                    intent1.putExtra("songInfo",downloadInfo);
                                    intent1.putExtra("progress",progress);
                                    intent1.setPackage("com.yize.wuming");
                                    sendBroadcast(intent1);
                                }

                                @Override
                                public void onSuccess() {
                                    intent1.putExtra("downloadInfo",downloadInfo);
                                    intent1.putExtra("progress",(long)100);
                                    intent1.setPackage("com.yize.wuming");
                                    sendBroadcast(intent1);
                                    //Toast.makeText(getApplicationContext(),"下载成功",Toast.LENGTH_SHORT);

                                }

                                @Override
                                public void onFailed() {
                                    intent1.putExtra("downloadInfo",downloadInfo);
                                    intent1.putExtra("progress",(long)(-1));
                                    intent1.setPackage("com.yize.wuming");
                                    sendBroadcast(intent1);
                                    //Toast.makeText(getApplicationContext(),"下载失败",Toast.LENGTH_SHORT);
                                }

                                @Override
                                public void onPaused() {
                                    intent1.putExtra("downloadInfo",downloadInfo);
                                    intent1.putExtra("progress",(long)(-2));
                                    intent1.setPackage("com.yize.wuming");
                                    sendBroadcast(intent1);
                                    //Toast.makeText(context,"暂停下载",Toast.LENGTH_SHORT);
                                }

                                @Override
                                public void onCanceled() {
                                    intent1.putExtra("downloadInfo",downloadInfo);
                                    intent1.putExtra("progress",(long)(-3));
                                    intent1.setPackage("com.yize.mdownloader");
                                    sendBroadcast(intent1);
                                    //Toast.makeText(getApplicationContext(),"取消下载",Toast.LENGTH_SHORT);
                                }
                            };
                            SongInfo newSongInfo=downloadInfo;
                            String lastName=".flac";
                            String downloadLink=getDownloadLinkFromServer(newSongInfo.getSongid(),musicQuality,newSongInfo.getSource());
                            Log.i("download link",downloadLink);
                            if(downloadLink.contains(".mp3")){
                                lastName=".mp3";
                            }
                            if(downloadLink.contains(".wav")){
                                lastName=".wav";
                            }
                            //newSongInfo=searchDownloadLink(newSongInfo);
//                            String downloadLink=newSongInfo.getSqDownloadLink();
//                            String lastName=".mp3";
//                            if(musicQuality.equals("SQ")){
//                                downloadLink=newSongInfo.getSqDownloadLink();
//                                if(downloadLink!=null){
//                                    lastName=".flac";
//                                }
//                            }else if(musicQuality.equals("HQ")){
//                                downloadLink=newSongInfo.getHqDownloadLink();
//                                if(downloadLink==null){
//                                    downloadLink=newSongInfo.getPqDownloadLink();
//                                }
//                            }else if(downloadLink==null){
//                                downloadLink=newSongInfo.getLqDownloadLink();
//                            }
                            if (downloadLink!=null&&downloadLink.length()>15){
                                String saveFileName=newSongInfo.getSongName()+"_";
                                if(newSongInfo.getSingerName().length()>10){
                                    saveFileName+=newSongInfo.getSingerName().substring(0,10);
                                }else {
                                    saveFileName+=newSongInfo.getSingerName()+"_";
                                }
                                if(newSongInfo.getAlbumName()!=null){
                                    saveFileName+=newSongInfo.getAlbumName();
                                }
                                saveFileName+=lastName;
                                new DownloadTask(listener).doInBackground(downloadLink,saveFileName,SAVE_FLODER);
                            }
                        } catch (InterruptedException e) {
                            Log.i("错误",e.toString());
                            //e.printStackTrace();
                        }


                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("downloadService","onDestroy");
        super.onDestroy();
    }

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("downloadService","onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static SongInfo searchDownloadLink(SongInfo songInfo){
        if(songInfo.getSongid().length()<9){
            songInfo=new KwMusic().getDownloadLink(songInfo);
        }else if(songInfo.getSongid().length()>10){
            songInfo=new MgMusic().getDownloadLink(songInfo);
        }
        return songInfo;
    }

    public static String getDownloadLinkFromServer(String songid,String quality,String source){
        String requestUrl="http://www.freedraw.xyz:8080/cloudmusic/api/download?songid="+songid+"&quality="+quality+"&accessKey="+encrypt(songid)+"&source="+source;
        Log.i("requestUrl:",requestUrl);
        try {
            URL url=new URL(requestUrl);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent","wuming");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            StringBuilder sb=new StringBuilder();
            while ((line=reader.readLine())!=null){
                sb.append(line);
            }
            reader.close();
            conn.disconnect();
            return sb.toString();
        } catch (Exception e) {
            Log.i("error",e.getCause().toString());
        }
        return "error";
    }

    public static String encrypt(String songid){
        String encParam1=new BASE64Encoder().encode((songid).getBytes());
        String encParams2=new BASE64Encoder().encode(String.valueOf(System.currentTimeMillis()).getBytes());
        String encStr= encParam1+"@"+encParams2;
        encStr=encStr.replaceAll("=",songid.substring(0,4));
        encStr=new BASE64Encoder().encode(encStr.getBytes()).replaceAll("=","_");
        return encStr;
    }
}
