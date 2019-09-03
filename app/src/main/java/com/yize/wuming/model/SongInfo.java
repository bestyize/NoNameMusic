package com.yize.wuming.model;

import java.io.Serializable;

public class SongInfo implements Serializable {

    private String songid;//歌曲ID
    private String songName;//歌曲名
    private String singerName;//歌手名
    private String albumName;//专辑名
    private String picUrl;//歌曲封面
    private String lrcUrl;//歌词链接
    private String mvUrl;//MV链接
    private String quality;//最高质量
    private String lqDownloadLink;//128或者最低品质音乐
    private String pqDownloadLink;//128或者192的MP3
    private String hqDownloadLink;//320的音乐
    private String sqDownloadLink;//FLAC格式的音乐
    private String source;//音乐源

    private long downloadProgress;

    public SongInfo() {
    }

    public SongInfo(String songid) {
        this.songid = songid;
    }

    public SongInfo(String songid, String songName, String singerName, String albumName, String quality) {
        this.songid = songid;
        this.songName = songName;
        this.singerName = singerName;
        this.albumName = albumName;
        this.quality = quality;
    }

    public SongInfo(String lqDownloadLink, String pqDownloadLink, String hqDownloadLink, String sqDownloadLink) {
        this.lqDownloadLink = lqDownloadLink;
        this.pqDownloadLink = pqDownloadLink;
        this.hqDownloadLink = hqDownloadLink;
        this.sqDownloadLink = sqDownloadLink;
    }

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songname) {
        this.songName = songname;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getLrcUrl() {
        return lrcUrl;
    }

    public void setLrcUrl(String lrcUrl) {
        this.lrcUrl = lrcUrl;
    }

    public String getMvUrl() {
        return mvUrl;
    }

    public void setMvUrl(String mvUrl) {
        this.mvUrl = mvUrl;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getLqDownloadLink() {
        return lqDownloadLink;
    }

    public void setLqDownloadLink(String lqDownloadLink) {
        this.lqDownloadLink = lqDownloadLink;
    }

    public String getPqDownloadLink() {
        return pqDownloadLink;
    }

    public void setPqDownloadLink(String pqDownloadLink) {
        this.pqDownloadLink = pqDownloadLink;
    }

    public String getHqDownloadLink() {
        return hqDownloadLink;
    }

    public void setHqDownloadLink(String hqDownloadLink) {
        this.hqDownloadLink = hqDownloadLink;
    }

    public String getSqDownloadLink() {
        return sqDownloadLink;
    }

    public void setSqDownloadLink(String sqDownloadLink) {
        this.sqDownloadLink = sqDownloadLink;
    }


    public long getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(long downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "{" +
                "\"songid\":\""+ songid +
                "\",\"songName\":\""+ songName +
                "\",\"singerName\":\""+ singerName+
                "\",\"albumName\":\""+ albumName+
                "\",\"picUrl\":\"" + picUrl +
                "\",\"lrcUrl\":\"" + lrcUrl +
                "\",\"mvUrl\":\""+ mvUrl +
                "\",\"quality\":\"" + quality +
                "\",\"lqDownloadLink\":\"" + lqDownloadLink +
                "\",\"pqDownloadLink\":\""+ pqDownloadLink +
                "\",\"hqDownloadLink\":\""+ hqDownloadLink +
                "\",\"sqDownloadLink\":\""+ sqDownloadLink +
                "\"}";
    }
}
