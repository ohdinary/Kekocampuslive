package com.example.dinary.kekocampuslive.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.beans.LiveCell;
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.example.dinary.kekocampuslive.util.UtilImg;
import com.example.dinary.kekocampuslive.watchar.WatchLiveActivity;

import java.util.List;

/**
 * 直播列表适配器
 */
public class LiveCellAdapter extends RecyclerView.Adapter<LiveCellAdapter.ViewHolder> {

    private Context context;
    private List<LiveCell> mListCell;

    private ViewHolder holder;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView liveTitle;
        ImageView liveCover;
        ImageView hostAvatar;
        TextView hostName;
        TextView watchNums;

        public ViewHolder(View view){
            super(view);
            itemView = view;
            liveTitle = (TextView) view.findViewById(R.id.live_title);
            liveCover = (ImageView) view.findViewById(R.id.live_cover);
            hostName = (TextView) view.findViewById(R.id.live_host_name);
            hostAvatar = (ImageView) view.findViewById(R.id.live_host_avatar);
            watchNums = (TextView) view.findViewById(R.id.watch_num);
        }
    }

    public LiveCellAdapter(List<LiveCell> mListCell) {
        this.mListCell = mListCell;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.live_cell_item, parent, false);
        holder = new ViewHolder(view);
        //注册监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinLive();
            }
        });
        return holder;
    }

    /*观众进入直播*/
    private void joinLive() {
        int position = holder.getAdapterPosition();
        LogUtil.d("position",String.valueOf(position));
        if (position == -1){
            Toast.makeText(MyApplication.getContext(),"获取直播信息错误！",Toast.LENGTH_LONG).show();
            return;
        }
        LiveCell cell = mListCell.get(position);
        Intent intent = new Intent(MyApplication.getContext(), WatchLiveActivity.class);
        intent.putExtra("roomId", cell.getLiveRoomId());
        intent.putExtra("hostId", cell.getHostId());
        LogUtil.d("hostId+roomId:",cell.getLiveRoomId()+"  "+cell.getHostId());
        context.startActivity(intent);
    }

    /*加载布局*/
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LiveCell liveCell = mListCell.get(position);
        holder.hostName.setText(liveCell.getHostName());
        holder.liveTitle.setText(liveCell.getLiveTitle());
        holder.watchNums.setText(liveCell.getWatchNum() + "人\r\n正在看");
        UtilImg.loadRound(R.drawable.default_avatar, holder.hostAvatar);
        UtilImg.load(R.drawable.default_cover, holder.liveCover);
        if (!TextUtils.isEmpty(liveCell.getHostAvatar())) {
            UtilImg.loadRound(liveCell.getHostAvatar(), holder.hostAvatar);
        }
        if (!TextUtils.isEmpty(liveCell.getLiveCover()))
            UtilImg.load(liveCell.getLiveCover(), holder.liveCover);
    }

    public void removeAllLiveInfo(){
        if (mListCell != null)
            mListCell.clear();
    }
    public void pasteLivelistInfo(List<LiveCell> liveCells){
        if (mListCell != null){
            mListCell.clear();
        }
        mListCell = liveCells;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mListCell == null ? 0 : mListCell.size();
    }
}
