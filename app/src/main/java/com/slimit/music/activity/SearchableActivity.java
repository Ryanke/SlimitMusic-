package com.slimit.music.activity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.slimit.music.R;
import com.slimit.music.application.MusicApplication;
import com.slimit.music.bean.OnlineMusicBean;
import com.slimit.music.fragment.OnlineMusicFragment;
import com.slimit.music.util.OnlineAudioUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class SearchableActivity extends BaseActivity
        implements View.OnClickListener, SearchView.OnQueryTextListener,
        AdapterView.OnItemClickListener {
    private static final String TAG = "RhymeMusic";
    private static final String SUB = "[SearchableActivity]#";

    private ListView listView;
    private ImageButton searchBack;
    private ImageButton searchOption;
    private SearchView searchView;
    private TextView textTips;

    private List<String> stringList;
    private List<String> translate;
    private MusicApplication application;

    private int currentMusic;

    private String content = "";
    private String type = "qq";
    ArrayAdapter<String> adapter;
    private PopupWindow window;
    private boolean isFirst = true;
    List<OnlineMusicBean.DataBean> onlineAudiolist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // 隐藏掉标题栏
        setContentView(R.layout.activity_searchable);
        try {
            initComponents();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*获得Intent对象，核实action后，获取查询结果*/
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_search_back:
                finish();
                break;

            case R.id.image_search_option:

                break;

            default:
                break;
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() != 0) {
            listView.setFilterText(newText);
            listView.dispatchDisplayHint(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.clearTextFilter();
            listView.setVisibility(View.INVISIBLE);
        }
        content = newText;
        if (!isFirst) {
            showPopup();
            startThread();
        }
        isFirst = false;
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = onlineAudiolist.get(position).getUrl();
        Snackbar.make(view, "歌曲正在缓冲，请耐心等待！", Snackbar.LENGTH_SHORT).show();
        application.getMusicBinder().startPlay(url);

//        Intent intent = new Intent(SearchableActivity.this, PlaybackActivity.class);
//        startActivity(intent);
    }

    private void initComponents() throws IOException {
        application = (MusicApplication) getApplication();

        /*搜索结果为空时，显示的提示信息*/
        textTips = (TextView) findViewById(R.id.text_tips);

        /*按钮类组件初始化*/
        searchBack = (ImageButton) findViewById(R.id.image_search_back);
        searchOption = (ImageButton) findViewById(R.id.image_search_option);
        searchBack.setOnClickListener(this);
        searchOption.setOnClickListener(this);

        /*列表组件初始化*/
        listView = (ListView) findViewById(R.id.list_search_result);
        listView.setOnItemClickListener(this);
        listView.setVisibility(View.INVISIBLE);
//        new Thread(runnable).start();
        /*搜索组件初始化*/
        searchView = (SearchView) findViewById(R.id.view_search);
        searchView.setOnQueryTextListener(this);
        searchView.onActionViewExpanded(); // 默认出现点击输入状态
        searchView.setFocusable(false);
        searchView.clearFocus();
        searchView.setSubmitButtonEnabled(true);

    }

    /**
     * 加载数据
     */
    private void initListView() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, stringList);
            listView.setAdapter(adapter);
            listView.setTextFilterEnabled(true);
        }

        if (0 == stringList.size()) {
            String tips = "暂无数据";
            textTips.setText(tips);
        }
    }

    private void startThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = OnlineMusicFragment.GET_DATA;
                if (true) {
                    try {
                        OnlineAudioUtil onlineAudioUtil = new OnlineAudioUtil(SearchableActivity.this);
                        onlineAudiolist = onlineAudioUtil.handleJsonData(content, type);
                        stringList = new ArrayList<>();
                        for (OnlineMusicBean.DataBean item : onlineAudiolist) {
                            String title = item.getTitle();
                            String artist = item.getAuthor();
                            String combine = artist + "——" + title;
                            stringList.add(combine);
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendMessage(message);
            }
        }).start();

        Log.d(TAG, SUB + "线程执行结束。");
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OnlineMusicFragment.GET_DATA:
                    window.dismiss();
                    initListView();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showPopup() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_load, null, false);
        // 创建PopupWindow对象，其中：
        // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
        // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
        window = new PopupWindow(contentView, this.getWindow().getWindowManager().getDefaultDisplay().getWidth(), this.getWindow().getWindowManager().getDefaultDisplay().getHeight(), true);
        // 设置PopupWindow的背景
        ColorDrawable dw = new ColorDrawable(0x80000000);
        window.setBackgroundDrawable(dw);
        // 设置PopupWindow是否能响应外部点击事件
        window.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        window.setTouchable(true);
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
        window.showAsDropDown(this.getWindow().getDecorView(), (int) this.getWindow().getDecorView().getX(), (int) this.getWindow().getDecorView().getY());
        // 或者也可以调用此方法显示PopupWindow，其中：
        // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
        // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
        // window.showAtLocation(parent, gravity, x, y);
    }
}