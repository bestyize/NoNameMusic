package com.yize.wuming;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.yize.wuming.utils.PersistHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StartupActivity extends AppCompatActivity {
    public static final int OPCODE_AGREEMENT_STATUS=0;
    public static final int OPCODE_FORBIDDEN_STATUS=1;
    public static final int OPCODE_NEWVERSION_STATUS=2;
    public static final int OPCODE_CANTCONNECT_STATUS=3;
    public String newDownloadLink="http://www.freedraw.xyz/download/noname.apk";
    private boolean needNotice=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        initView();
    }
    private void initView(){
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        checkPermission();

        String user_agreement_status= PersistHelper.readFromSharedPreference(StartupActivity.this,PersistHelper.SP_USER_AGREEMENT_STATUS,"");
        if(!user_agreement_status.equals("agree")){
            showNotice("用户协议","本软件仅作为研究App开发所用，如果您有任何其他用途，" +
                    "请立即停止使用本App,点击同意，即为接受以上条款，点击不同意，即为拒绝上述条款，由此带来的法律" +
                    "问题，均与开发者无关","同意","拒绝",OPCODE_AGREEMENT_STATUS);
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long startTime=System.currentTimeMillis();
                        String content=downloadWebSite("http://www.freedraw.xyz:8080/cloudmusic/api/check");
                        Gson gson=new Gson();
                        final StartUpInfo startUpInfo=gson.fromJson(content,StartUpInfo.class);
                        long endTime=System.currentTimeMillis();
                        if(endTime-startTime<3000){
                            Thread.sleep(3000-(endTime-startTime));
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //check status from server,then change needNotice status to true or false
                                if(startUpInfo.getForbidden().equals("true")){
                                    showNotice("停止使用",startUpInfo.getEnergencyNoticeContent(),"退出","取消",1);
                                    return;
                                }
                                double newVersion=Double.valueOf(startUpInfo.version);
                                if(newVersion>2.1){
                                    newDownloadLink=startUpInfo.getDownloadUrl();
                                    showNotice("有新版本",startUpInfo.getPushContent(),"下载新版本","取消",OPCODE_NEWVERSION_STATUS);
                                    return;
                                }

                                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();



                            }
                        });
                    } catch (Exception e) {
                        //e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showNotice("连接失败","与服务器连接失败，请到公众号：从来不想 反馈问题","确定","取消",OPCODE_CANTCONNECT_STATUS);
                            }
                        });

                    }
                }
            }).start();
        }


    }

    public void showNotice(String noticeTitle, String noticeContent, String noticeOk, String noticeCancel, final int opcode){
        LayoutInflater inflater=LayoutInflater.from(this);
        View layout=inflater.inflate(R.layout.notice_important,null);
        final AlertDialog.Builder builder=new AlertDialog.Builder(StartupActivity.this);
        builder.setView(layout);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.show();
        TextView tv_notice_ok=(TextView) layout.findViewById(R.id.tv_notice_ok);
        TextView tv_notice_cancel=(TextView)layout.findViewById(R.id.tv_notice_cancel);
        TextView tv_notice_title=(TextView)layout.findViewById(R.id.tv_notice_title);
        TextView tv_notice_content=(TextView)layout.findViewById(R.id.tv_notice_content);
        tv_notice_ok.setText(noticeOk);
        tv_notice_cancel.setText(noticeCancel);
        tv_notice_title.setText(noticeTitle);
        tv_notice_content.setText(noticeContent);
        if(opcode==OPCODE_FORBIDDEN_STATUS||opcode==OPCODE_NEWVERSION_STATUS||opcode==OPCODE_CANTCONNECT_STATUS){
            tv_notice_cancel.setVisibility(View.GONE);
        }
        tv_notice_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opcode==OPCODE_AGREEMENT_STATUS){
                    PersistHelper.saveToSharedPreference(StartupActivity.this,PersistHelper.SP_USER_AGREEMENT_STATUS,"agree");
                    dialog.dismiss();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                if(opcode==OPCODE_FORBIDDEN_STATUS){
                    dialog.dismiss();
                    finish();
                    System.exit(0);
                }
                if(opcode==OPCODE_NEWVERSION_STATUS){
                    downloadNewVersion();
                }
                if(opcode==OPCODE_CANTCONNECT_STATUS){
                    dialog.dismiss();
                    finish();
                    System.exit(0);
                }


            }
        });
        tv_notice_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                System.exit(0);
            }
        });

    }
    /**
     * 检查权限，网络读写等
     */
    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.FOREGROUND_SERVICE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.FOREGROUND_SERVICE},3);
        }

    }



    public String downloadWebSite(String website) {
        try {
            URL url=new URL(website);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Mobile Safari/537.36");
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
            return "error";
        }
    }

    private void downloadNewVersion(){
        Uri uri = Uri.parse(newDownloadLink);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    static class StartUpInfo{
        private String appname;
        private String version;
        private String forbidden;
        private String downloadUrl;
        private String pushContent;
        private String downloadEngineer;
        private String energencyNotice;
        private String energencyNoticeContent;

        public StartUpInfo() {
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getForbidden() {
            return forbidden;
        }

        public void setForbidden(String forbidden) {
            this.forbidden = forbidden;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getPushContent() {
            return pushContent;
        }

        public void setPushContent(String pushContent) {
            this.pushContent = pushContent;
        }

        public String getDownloadEngineer() {
            return downloadEngineer;
        }

        public void setDownloadEngineer(String downloadEngineer) {
            this.downloadEngineer = downloadEngineer;
        }

        public String getEnergencyNotice() {
            return energencyNotice;
        }

        public void setEnergencyNotice(String energencyNotice) {
            this.energencyNotice = energencyNotice;
        }

        public String getEnergencyNoticeContent() {
            return energencyNoticeContent;
        }

        public void setEnergencyNoticeContent(String energencyNoticeContent) {
            this.energencyNoticeContent = energencyNoticeContent;
        }
    }
}
