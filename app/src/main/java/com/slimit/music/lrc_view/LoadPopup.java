package com.slimit.music.lrc_view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;

import com.slimit.music.R;

public class LoadPopup extends PopupWindow {
    Context context;

    public LoadPopup(Activity context) {
        this(context, context.getWindow().getWindowManager().getDefaultDisplay().getWidth(), context.getWindow().getWindowManager().getDefaultDisplay().getHeight(), true);
        this.context = context;
    }

    public LoadPopup(Context context, int width, int height, boolean focusable) {
        super(context);
        View ContentView = LayoutInflater.from(context).inflate(R.layout.layout_load, null, false);
        setContentView(ContentView);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);
        ColorDrawable dw = new ColorDrawable(0x80000000);
        setBackgroundDrawable(dw);
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        setTouchable(true);
    }

    public void showAsDropDown() {
        Window anchor = ((Activity) context).getWindow();
        showAsDropDown(anchor.getDecorView(), (int) anchor.getDecorView().getX(), (int) anchor.getDecorView().getY());
    }
}
