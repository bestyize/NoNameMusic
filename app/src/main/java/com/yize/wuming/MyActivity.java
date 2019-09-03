package com.yize.wuming;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyActivity extends AppCompatActivity {
    private LinearLayout ll_user_setting,ll_user_guide,ll_weixin_platform,ll_offical_site,ll_support;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        intView();
    }

    private void intView(){
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ll_user_guide=(LinearLayout)findViewById(R.id.ll_user_guide);
        ll_user_setting=(LinearLayout)findViewById(R.id.ll_user_setting);
        ll_weixin_platform=(LinearLayout)findViewById(R.id.ll_weixin_platform);
        ll_offical_site=(LinearLayout)findViewById(R.id.ll_offical_site);
        ll_support=(LinearLayout)findViewById(R.id.ll_support);


        ll_user_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager1=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData1=ClipData.newPlainText("公众号","从来不想");
                clipboardManager1.setPrimaryClip(clipData1);
                Toast.makeText(MyActivity.this,"请到公众号后台回复：使用教程",Toast.LENGTH_LONG).show();
                goToWeChat();
            }
        });

        ll_user_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyActivity.this,"功能正在开发中...",Toast.LENGTH_SHORT).show();
            }
        });

        ll_weixin_platform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager1=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData1=ClipData.newPlainText("公众号","从来不想");
                clipboardManager1.setPrimaryClip(clipData1);
                Toast.makeText(MyActivity.this,"已复制到剪切板，请到回台回复：使用教程",Toast.LENGTH_SHORT).show();
                goToWeChat();
            }
        });
        ll_offical_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOfficalSite();
            }
        });

        ll_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportAlipay();
            }
        });
    }

    /**
     * 跳转到微信
     */
    private void goToWeChat(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            //ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.plugin.base.stub.WXCustomSchemeEntryActivity");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception

        }
    }

    private void goToOfficalSite(){
        Uri uri = Uri.parse("http://www.freedraw.xyz");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //https://qr.alipay.com/fkx026938btysbgu4ajuhd6

    private void supportAlipay(){
        Uri uri = Uri.parse("https://qr.alipay.com/fkx026938btysbgu4ajuhd6");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
