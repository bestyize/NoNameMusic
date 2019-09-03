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

import sun.BASE64Encoder;

class KuwoInfo {
    private int rid;
    private String name;
    private String artist;
    private String album;
    private String pic;
    private boolean hasLossless;

    public KuwoInfo() {
    }

    public KuwoInfo(int rid, String name, String artist, String album, String pic, boolean hasLossless) {
        this.rid = rid;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.pic = pic;
        this.hasLossless = hasLossless;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean isHasLossless() {
        return hasLossless;
    }

    public void setHasLossless(boolean hasLossless) {
        this.hasLossless = hasLossless;
    }
}
public class KwMusic implements MusicHelper {

    @Override
    public List<SongInfo> searchMusic(String keyword, int num) {
        List<SongInfo> kuwoMusicInfoList=new ArrayList<>();

        try {
            String requestLink="http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key="+ URLEncoder.encode(keyword,"utf-8") +"&pn=1&rn="+num;
            String response=downloadWebSite(requestLink);
            response=response.substring(response.indexOf("["),response.lastIndexOf("]")+1);
            Gson gson=new Gson();
            List<KuwoInfo> kuwoInfoList=gson.fromJson(response,new TypeToken<List<KuwoInfo>>(){}.getType());
            for(KuwoInfo kuwoInfo:kuwoInfoList){
                SongInfo songInfo=new SongInfo();
                songInfo.setSource("Kw");
                songInfo.setSongid(String.valueOf(kuwoInfo.getRid()));
                songInfo.setSongName(kuwoInfo.getName());
                songInfo.setAlbumName(kuwoInfo.getAlbum());
                songInfo.setSingerName(kuwoInfo.getArtist());
                songInfo.setPicUrl(kuwoInfo.getPic());
                if(kuwoInfo.isHasLossless()){
                    songInfo.setQuality("SQ");
                }else {
                    songInfo.setQuality("HQ");
                }
                kuwoMusicInfoList.add(songInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return kuwoMusicInfoList;
        }
    }

    @Override
    public SongInfo getDownloadLink(SongInfo songInfo) {
        songInfo=getDownloadLink(songInfo,"SQ");
        songInfo=getDownloadLink(songInfo,"HQ");
        songInfo=getDownloadLink(songInfo,"PQ");
        songInfo=getDownloadLink(songInfo,"LQ");
        return songInfo;
    }

    @Override
    public SongInfo getDownloadLink(SongInfo songInfo, String quality) {
        String downloadLink=getDownloadLinkById(songInfo.getSongid(),quality);
        if(downloadLink==null){
            return songInfo;
        }
        if(downloadLink.contains(".flac")){
            songInfo.setSqDownloadLink(downloadLink);
        }else {
            if(quality.equals("HQ")){
                songInfo.setHqDownloadLink(downloadLink);
            }else if(quality.equals("PQ")){
                songInfo.setPqDownloadLink(downloadLink);

            }else if(quality.equals("LQ")){
                songInfo.setLqDownloadLink(downloadLink);
            }
        }
        return songInfo;
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
    public String replaceFieldName(String info, String field, String newField) {
        return info.replaceAll("\""+field+"\"","\""+newField+"\"");
    }


    private String getDownloadLinkById(String songid, String quality){
        return null;
    }

    /**
     * 酷我音乐解密算法
     *
     */


    private static long bit_transform(long[] arr_int,int n,long l){
        long l2 = 0;
        for(int i = 0; i < n; i++){
            if(arr_int[i] < 0 || (l & arrayMask[(int)arr_int[i]]) == 0){
                continue;
            }
            l2 |= arrayMask[i];
        }
        return l2;
    }

    private static long DES64(long[] longs,long l){
        long out = 0;
        long SOut = 0;
        long[] pR = {0,0,0,0,0,0,0,0};
        long[] pSource = {0,0};
        int sbi = 0;
        int t = 0;
        long L = 0;
        long R = 0;
        out = bit_transform(arrayIP,64,l);
        pSource[0] = 4294967295L & out;
        //System.out.println(pSource[0]);
        pSource[1] = (-4294967296L & out) >> 32;
        //System.out.println(pSource[1]);
        for(int i = 0; i < 16; i++){
            R = pSource[1];
            R = bit_transform(arrayE, 64, R);
            R ^= longs[i];
            for(int j = 0; j < 8; j++){
                pR[j] = 255 & R >> j * 8;
            }

            SOut = 0;
            for(sbi = 7; sbi > -1; sbi--){
                SOut <<= 4;
                SOut |= matrixNSBox[sbi][(int)pR[sbi]];
            }
            R = bit_transform(arrayP, 32, SOut);
            L = pSource[0];
            pSource[0] = pSource[1];
            pSource[1] = L ^ R;

        }
        pSource = Reserve(pSource);
        //System.out.println(Arrays.toString(pSource));
        out = (-4294967296L) & pSource[1] << 32 | 4294967295L & pSource[0];
        out = bit_transform(arrayIP_1, 64, out);
        //System.out.println("hh===="+out);
        return out;
    }

    private static void sub_keys(long l,long[] longs,long n){
        long l2 = bit_transform(arrayPC_1, 56, l);
        for(int i = 0; i < 16; i++){
            l2 = ((l2 & arrayLsMask[(int)arrayLs[i]]) << 28 -
                    arrayLs[i] | (l2 & ~arrayLsMask[(int)arrayLs[i]]) >> arrayLs[i]);
            longs[i] = bit_transform(arrayPC_2, 64, l2);
        }
        int j = 0;

        while (n == 1 && j < 8){
            long l3 = longs[j];
            longs = replace(longs,j,15-j);
            j += 1;
        }

    }

    private static long[] encrypt(String msg) {
        String key = SECRET_KEY;
        byte[] msg2 = msg.getBytes();
        //System.out.println(Arrays.toString(msg2));
        String msg3 = new String(msg2);
        //System.out.println(msg3);
        byte[] key2 = key.getBytes();
        //String key3 = new String(key2);
        long l = 7887891437440363641L;

		/*for(int i = 0; i < 8; i++){
			l = l | key2[i] << i * 8;
			System.out.println(l);
			System.out.println(key2[i]);
		}*/
        //System.out.println(l);
        int j = (msg.getBytes().length)/8;
        //System.out.println(msg.getBytes().length);
        long[] arrLong1 = ab(16);
        sub_keys(l, arrLong1, 0);
        long[] arrLong2 = ab(j);

        for(int m = 0; m < j; m++){
            for(int n = 0; n < 8; n++){
                arrLong2[m] |= (long)msg2[n + m * 8] << n * 8;
                //System.out.println("long2=="+arrLong2[m]);
            }
        }

        long[] arrLong3 = ab((1 + 8 * (j + 1)) / 8);
        //System.out.println(arrLong3.length);
        for(int i1 = 0; i1 < j; i1++){
            //System.out.println(Arrays.toString(arrLong1));

            arrLong3[i1] = DES64(arrLong1, arrLong2[i1]);
            //System.out.println(Arrays.toString(arrLong1));
            //System.out.println(arrLong2[i1]);
            //System.out.println(arrLong3[i1]);
        }

        byte[] arrByte1 = msg3.substring(j*8).getBytes();
        //System.out.println(Arrays.toString(arrByte1));

        long l2 = 0;
        for(int i1 = 0; i1 < msg2.length % 8; i1++){
            l2 |= arrByte1[i1] << i1 * 8;
            //System.out.println(""+l2);
        }


        arrLong3[j] = DES64(arrLong1, l2);
        //System.out.println("hhh===="+arrLong3[j]);

        long[] arrByte2 = ab((8 * arrLong3.length));


        int i4 = 0;

        for(int l3 = 0; l3 < arrLong3.length; l3++){
            for(int i6 = 0; i6 < 8; i6++){
                arrByte2[i4] = (255 & arrLong3[l3] >> i6 * 8);

                i4 += 1;
            }
        }

        return arrByte2;
    }



    private static String base64_encrypt(String msg) {
        long[] b1 = encrypt(msg);
        byte[] b2 = longTobyte(b1);

//        String en = Base64.getEncoder().encodeToString(b2);
        String str= new BASE64Encoder().encode(b2);
        return str;
    }
    private static long[] Reserve(long[] array){
        long[] newArray = new long[array.length];
        for(int i = 0; i < array .length; i++){
            newArray[i] = array[array.length - i - 1];

        }
        return newArray;
    }

    private static long[] replace(long[] array,int start,int end){
        long[] newarray = new long[array.length];
        for(int i = 0; i < array.length; i++){
            if(i == start){
                newarray[i] = array[end];
            }else if(i == end){
                newarray[i] = array[start];
            }else{
                newarray[i] = array[i];
            }
        }
        return newarray;
    }

    private static long[] ab(int number){
        long[] array = new long[number];
        for(int i = 0; i < number; i++){
            array[i] = 0;
        }
        return array;
    }

    private static byte[] longTobyte(long[] longs){
        byte[] b = new byte[longs.length];
        for(int i = 0; i < b.length; i++){
            b[i] = (byte)longs[i];
        }
        return b;
    }

    private static long[] arrayE = {
            31,  0,  1,  2,  3,  4, -1, -1,
            3,  4,  5,  6,  7,  8, -1, -1,
            7,  8,  9, 10, 11, 12, -1, -1,
            11, 12, 13, 14, 15, 16, -1, -1,
            15, 16, 17, 18, 19, 20, -1, -1,
            19, 20, 21, 22, 23, 24, -1, -1,
            23, 24, 25, 26, 27, 28, -1, -1,
            27, 28, 29, 30, 31, 30, -1, -1,
    };

    private static long[] arrayIP = {
            57, 49, 41, 33, 25, 17,  9,  1,
            59, 51, 43, 35, 27, 19, 11,  3,
            61, 53, 45, 37, 29, 21, 13,  5,
            63, 55, 47, 39, 31, 23, 15,  7,
            56, 48, 40, 32, 24, 16,  8,  0,
            58, 50, 42, 34, 26, 18, 10,  2,
            60, 52, 44, 36, 28, 20, 12,  4,
            62, 54, 46, 38, 30, 22, 14,  6,
    };

    private static long[] arrayIP_1 = {
            39,  7, 47, 15, 55, 23, 63, 31,
            38,  6, 46, 14, 54, 22, 62, 30,
            37,  5, 45, 13, 53, 21, 61, 29,
            36,  4, 44, 12, 52, 20, 60, 28,
            35,  3, 43, 11, 51, 19, 59, 27,
            34,  2, 42, 10, 50, 18, 58, 26,
            33,  1, 41,  9, 49, 17, 57, 25,
            32,  0, 40,  8, 48, 16, 56, 24,
    };

    private static long[] arrayLs = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

    private static long[] arrayLsMask = {0, 0x100001, 0x300003};

    private static long[] arrayMask = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096,
            8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432,
            67108864, 134217728, 268435456, 536870912, 1073741824, (long)2<<(31-1), (long)2<<(32-1), (long)2<<(33-1), (long)2<<(34-1), (long)2<<(35-1),
            (long)2<<(36-1), (long)2<<(37-1), (long)2<<(38-1), (long)2<<(39-1), (long)2<<(40-1), (long)2<<(41-1), (long)2<<(42-1), (long)2<<(43-1), (long)2<<(44-1),
            (long)2<<(45-1), (long)2<<(46-1), (long)2<<(47-1), (long)2<<(48-1), (long)2<<(49-1), (long)2<<(50-1), (long)2<<(51-1), (long)2<<(52-1),
            (long)2<<(53-1), (long)2<<(54-1), (long)2<<(55-1), (long)2<<(56-1), (long)2<<(57-1), (long)2<<(58-1), (long)2<<(59-1), (long)2<<(60-1),
            (long)2<<(61-1), (long)2<<(62-1), (long)2<<(63-1)};



    private static long[] arrayP = {
            15,  6, 19, 20, 28, 11, 27, 16,
            0, 14, 22, 25,  4, 17, 30,  9,
            1,  7, 23, 13, 31, 26,  2,  8,
            18, 12, 29,  5, 21, 10,  3, 24,
    };

    private static long[] arrayPC_1 = {
            56, 48, 40, 32, 24, 16,  8,  0,
            57, 49, 41, 33, 25, 17,  9,  1,
            58, 50, 42, 34, 26, 18, 10,  2,
            59, 51, 43, 35, 62, 54, 46, 38,
            30, 22, 14,  6, 61, 53, 45, 37,
            29, 21, 13,  5, 60, 52, 44, 36,
            28, 20, 12,  4, 27, 19, 11,  3,
    };

    private static long[] arrayPC_2 = {
            13, 16, 10, 23,  0,  4, -1, -1,
            2, 27, 14,  5, 20,  9, -1, -1,
            22, 18, 11,  3, 25,  7, -1, -1,
            15,  6, 26, 19, 12,  1, -1, -1,
            40, 51, 30, 36, 46, 54, -1, -1,
            29, 39, 50, 44, 32, 47, -1, -1,
            43, 48, 38, 55, 33, 52, -1, -1,
            45, 41, 49, 35, 28, 31, -1, -1,
    };

    private static long[][] matrixNSBox = {{
            14,  4,  3, 15,  2, 13,  5,  3,
            13, 14,  6,  9, 11,  2,  0,  5,
            4,  1, 10, 12, 15,  6,  9, 10,
            1,  8, 12,  7,  8, 11,  7,  0,
            0, 15, 10,  5, 14,  4,  9, 10,
            7,  8, 12,  3, 13,  1,  3,  6,
            15, 12,  6, 11,  2,  9,  5,  0,
            4,  2, 11, 14,  1,  7,  8, 13, }, {
            15,  0,  9,  5,  6, 10, 12,  9,
            8,  7,  2, 12,  3, 13,  5,  2,
            1, 14,  7,  8, 11,  4,  0,  3,
            14, 11, 13,  6,  4,  1, 10, 15,
            3, 13, 12, 11, 15,  3,  6,  0,
            4, 10,  1,  7,  8,  4, 11, 14,
            13,  8,  0,  6,  2, 15,  9,  5,
            7,  1, 10, 12, 14,  2,  5,  9, }, {
            10, 13,  1, 11,  6,  8, 11,  5,
            9,  4, 12,  2, 15,  3,  2, 14,
            0,  6, 13,  1,  3, 15,  4, 10,
            14,  9,  7, 12,  5,  0,  8,  7,
            13,  1,  2,  4,  3,  6, 12, 11,
            0, 13,  5, 14,  6,  8, 15,  2,
            7, 10,  8, 15,  4,  9, 11,  5,
            9,  0, 14,  3, 10,  7,  1, 12, }, {
            7, 10,  1, 15,  0, 12, 11,  5,
            14,  9,  8,  3,  9,  7,  4,  8,
            13,  6,  2,  1,  6, 11, 12,  2,
            3,  0,  5, 14, 10, 13, 15,  4,
            13,  3,  4,  9,  6, 10,  1, 12,
            11,  0,  2,  5,  0, 13, 14,  2,
            8, 15,  7,  4, 15,  1, 10,  7,
            5,  6, 12, 11,  3,  8,  9, 14, }, {
            2,  4,  8, 15,  7, 10, 13,  6,
            4,  1,  3, 12, 11,  7, 14,  0,
            12,  2,  5,  9, 10, 13,  0,  3,
            1, 11, 15,  5,  6,  8,  9, 14,
            14, 11,  5,  6,  4,  1,  3, 10,
            2, 12, 15,  0, 13,  2,  8,  5,
            11,  8,  0, 15,  7, 14,  9,  4,
            12,  7, 10,  9,  1, 13,  6,  3, }, {
            12,  9,  0,  7,  9,  2, 14,  1,
            10, 15,  3,  4,  6, 12,  5, 11,
            1, 14, 13,  0,  2,  8,  7, 13,
            15,  5,  4, 10,  8,  3, 11,  6,
            10,  4,  6, 11,  7,  9,  0,  6,
            4,  2, 13,  1,  9, 15,  3,  8,
            15,  3,  1, 14, 12,  5, 11,  0,
            2, 12, 14,  7,  5, 10,  8, 13, }, {
            4,  1,  3, 10, 15, 12,  5,  0,
            2, 11,  9,  6,  8,  7,  6,  9,
            11,  4, 12, 15,  0,  3, 10,  5,
            14, 13,  7,  8, 13, 14,  1,  2,
            13,  6, 14,  9,  4,  1,  2, 14,
            11, 13,  5,  0,  1, 10,  8,  3,
            0, 11,  3,  5,  9,  4, 15,  2,
            7,  8, 12, 15, 10,  7,  6, 12, }, {
            13,  7, 10,  0,  6,  9,  5, 15,
            8,  4,  3, 10, 11, 14, 12,  5,
            2, 11,  9,  6, 15, 12,  0,  3,
            4,  1, 14, 13,  1,  2,  7,  8,
            1,  2, 12, 15, 10,  4,  0,  3,
            13, 14,  6,  9,  7,  8,  9,  6,
            15,  1,  5, 12,  3, 10, 14,  5,
            8,  7, 11,  0,  4, 13,  2, 11, },
    };

    private static String SECRET_KEY = "ylzsxkwm";

}