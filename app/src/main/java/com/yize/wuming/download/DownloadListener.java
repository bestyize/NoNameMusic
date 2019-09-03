package com.yize.wuming.download;

public interface DownloadListener {
    void onProgress(Long progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
