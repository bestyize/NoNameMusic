package com.yize.wuming.model;

import java.util.List;

public interface MusicHelper {
    /**
     * 搜索歌曲列表
     * @param keyword  关键词
     * @param num  数量
     * @return 搜到的歌曲列表
     */
    List<SongInfo> searchMusic(String keyword, int num);
    /**
     * 至少应该有个songid信息，获取全部链接
     * @param songInfo 歌曲信息，必须包含songid
     * @return
     */
    SongInfo getDownloadLink(SongInfo songInfo);

    /**
     * 获取指定质量的下载链接
     * @param songInfo 歌曲信息，要包含songid
     * @param quality 歌曲质量，包括SQ、HQ、PQ、LQ，品质依次递减
     * @return
     */
    SongInfo getDownloadLink(SongInfo songInfo,String quality);

    /**
     * 下载网页源码
     * @param website
     * @return
     */
    String downloadWebSite(String website);

    /**
     * 替换字段名，比如把qq音乐的字段songmid更改成songid,以进行统一化处理
     * @param info   需要处理的字符串
     * @param field  字段名
     * @param newField 新的字段名
     * @return  处理后的字符串
     */
    String replaceFieldName(String info,String field,String newField);


}