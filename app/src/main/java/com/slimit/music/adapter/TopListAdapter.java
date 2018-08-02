package com.slimit.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.slimit.music.R;
import com.slimit.music.bean.OnlineMusicBean;
import com.slimit.music.help.FormatHelper;

import java.util.List;


/**
 * 在线音乐top100排行音乐
 * Created by Idea on 2016/6/24.
 */
public class TopListAdapter extends BaseAdapter
{
    private static final String TAG = "RhymeMusic";
    private static final String SUB = "[TopListAdapter]#";

    private Context context;

    private List<OnlineMusicBean.DataBean> audioList;

//    private MusicApplication application;


    public TopListAdapter(Context context, List<OnlineMusicBean.DataBean> audioList)
    {
        this.context     = context;
        this.audioList   = audioList;
//        this.application = application;
    }

    @Override
    public int getCount()
    {
        return audioList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return audioList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if ( convertView == null )
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_top_music, null);
            viewHolder = new ViewHolder();

            viewHolder.musicIndex = (TextView) convertView.
                    findViewById(R.id.text_online_music_index);
            viewHolder.musicTitle = (TextView) convertView.
                    findViewById(R.id.text_online_music_title);
            viewHolder.musicArtist = (TextView) convertView.
                    findViewById(R.id.text_online_music_artist);
            viewHolder.musicDuration = (TextView) convertView.
                    findViewById(R.id.text_online_music_duration);
            convertView.setTag(viewHolder);

//            application.setTextAudioIndex(viewHolder.musicIndex);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        OnlineMusicBean.DataBean audio = audioList.get(position);
        viewHolder.musicIndex.setText(FormatHelper.formatIndex(position + 1));
        viewHolder.musicTitle.setText(audio.getTitle());
        viewHolder.musicArtist.setText(audio.getAuthor());
        viewHolder.musicDuration.setText("3:00");

        return convertView;
    }


    /**
     * 创建内部类ViewHolder，用于对控件的实例进行缓存。
     */
    class ViewHolder
    {
        TextView musicIndex;
        TextView  musicTitle;
        TextView  musicArtist;
        TextView  musicDuration;
    }
}
