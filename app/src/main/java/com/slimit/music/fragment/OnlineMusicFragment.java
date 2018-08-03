package com.slimit.music.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.slimit.music.ImNetWorkListener;
import com.slimit.music.R;
import com.slimit.music.adapter.TopListAdapter;
import com.slimit.music.application.MusicApplication;
import com.slimit.music.bean.OnlineMusicBean;
import com.slimit.music.lrc_view.LoadPopup;
import com.slimit.music.service.MusicService;
import com.slimit.music.util.OnlineAudioUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 在线音乐
 * Created by Idea on 2016/5/3.
 */
public class OnlineMusicFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "RhymeMusic";
    private static final String SUB = "[OnlineMusicFragment]#";
    public static final int GET_DATA = 1;
    private List<OnlineMusicBean.DataBean> audioList = new ArrayList<>();
    private TopListAdapter topListAdapter;
    private ListView listView;
    private ImageView folkRhyme; // 民谣
    private ImageView hotAudio; // 热榜搜
    private ImageView imgAwait; // 敬请期待
    private boolean transform;
    private MusicApplication application;
    private MusicService.MusicBinder musicBinder;
    private LoadPopup loadPopup;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    audioList = ((OnlineMusicBean) msg.obj).getData();
                    setAdapter(audioList);
                    loadPopup.dismiss();
                    Log.d(TAG, SUB + "+++++++++++++++" + audioList.size());
                    break;

                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_online_music, container, false);
        initComponents(view);


        return view;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, SUB + "onDetach");
        super.onDetach();
    }

    private void initComponents(View view) {
        application = (MusicApplication) getActivity().getApplication();
        listView = (ListView) view.findViewById(R.id.list_top_music);
        listView.setOnItemClickListener(this);
        folkRhyme = (ImageView) view.findViewById(R.id.image_folk_rhyme);
        hotAudio = (ImageView) view.findViewById(R.id.image_top_list);
        imgAwait = (ImageView) view.findViewById(R.id.image_goon);
        folkRhyme.setOnClickListener(this);
        hotAudio.setOnClickListener(this);
        imgAwait.setOnClickListener(this);
        loadPopup = new LoadPopup(getActivity());
        startThread(); // 开始线程
    }


    /**
     * 通过新开一个线程来加载网络数据
     */
    private void startThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImNetWorkListener imNetWorkListener = new ImNetWorkListener<OnlineMusicBean>() {
                    @Override
                    public void failed() {

                    }

                    @Override
                    public void succeed(OnlineMusicBean response) {
                        Message message = new Message();
                        message.what = GET_DATA;
                        message.obj = response;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void noData() {

                    }

                    @Override
                    public void prepare() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadPopup.showAsDropDown();

                            }
                        });
                    }
                };

                OnlineAudioUtil audioUtil = new OnlineAudioUtil(getContext(), OnlineMusicBean.class, imNetWorkListener);
                audioUtil.SendGetRequest("毛不易", "qq");
            }
        }).start();
        Log.d(TAG, SUB + "线程执行结束。");
    }

    private void setAdapter(List<OnlineMusicBean.DataBean> list) {
        Log.d(TAG, SUB + list.size());
        topListAdapter = new TopListAdapter(getActivity(), list);
        listView.setAdapter(topListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = audioList.get(position).getUrl();
        Log.d(TAG, SUB + "onItemClick" + url);
        musicBinder = application.getMusicBinder();
        musicBinder.startPlay(url);
        Snackbar.make(view, "歌曲正在缓冲，请耐心等待！", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_folk_rhyme:
                transform = false;
                startThread();
                break;

            case R.id.image_top_list:
                transform = true;
                startThread();
                break;

            case R.id.image_goon:
                Snackbar.make(v, "精彩即将呈现，敬请期待", Snackbar.LENGTH_LONG).show();
                break;

            default:
                break;

        }
    }
}
