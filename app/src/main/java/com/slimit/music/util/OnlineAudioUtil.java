package com.slimit.music.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.slimit.music.ImNetWorkListener;
import com.slimit.music.bean.OnlineMusicBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 发送在线音乐请求，并解析出来，放在一个列表中。
 * Created by Idea on 2016/6/23.
 */
public class OnlineAudioUtil<T> {
    private Context context;
    private T bean;
    private static final String TAG = "RhymeMusic";
    private static final String SUB = "[OnlineAudioUtil]#";
    private ImNetWorkListener imNetWorkListener;

    public OnlineAudioUtil(Context context, T bean, ImNetWorkListener imNetWorkListener) {
        this.context = context;
        this.bean = bean;
        this.imNetWorkListener = imNetWorkListener;
    }


    /**
     * 接收在线服务器发过来的数据。
     */
    public List<OnlineMusicBean.DataBean> handleJsonData(String content, String type) throws IOException {
        Log.d(TAG, SUB + "startThread");
        List<OnlineMusicBean.DataBean> audioBeen = new ArrayList<>();
        String json = SendGetRequest(content, type);
        Log.d(TAG, json);
        Gson gson = new Gson();
        OnlineMusicBean musicBean = gson.fromJson(json, OnlineMusicBean.class);
        if (musicBean != null) {
            audioBeen.addAll(musicBean.getData());
        }
        return audioBeen;
    }


    public static String submitPostData(String strUrlPath, Map<String, String> params, String encode) {

        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        try {

            //String urlPath = "http://192.168.1.9:80/JJKSms/RecSms.php";
            URL url = new URL(strUrlPath);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return "err: " + e.getMessage().toString();
        }
        return "-1";
    }

    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }


    /**
     * 使用get方式与服务器通信
     *
     * @param content
     * @return
     */
    public String SendGetRequest(String content, String type) {
        String backContent = "";
        HttpURLConnection conn = null;
        try {
            StringBuilder requestUrl = new StringBuilder();
            requestUrl.append("https://www.showfree.cn/download/d?name=");
            requestUrl.append(content);
            requestUrl.append("&type=" + type);
            URL url = new URL(requestUrl.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                Log.i("PostGetUtil", "get请求成功");
                Log.i("ip", requestUrl.toString());
                InputStream in = conn.getInputStream();
                backContent = dealResponseResult(in);
                backContent = URLDecoder.decode(backContent, "UTF-8");
                Log.i("PostGetUtil", backContent);
                if (imNetWorkListener != null) {
                    String json = backContent;
                    Log.d(TAG, json);
                    Class clazz = (Class) OnlineAudioUtil.this.bean;
                    Gson gson = new Gson();
                    Object t = gson.fromJson(json,clazz);
                    if (t != null) {
                        imNetWorkListener.succeed(t);
                    } else {
                        imNetWorkListener.noData();
                    }
                }

                in.close();
            } else {
                Log.i("PostGetUtil", "get请求失败");
                if (imNetWorkListener != null) {
                    imNetWorkListener.failed();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return backContent;
    }

}
