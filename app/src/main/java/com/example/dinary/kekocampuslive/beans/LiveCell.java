package com.example.dinary.kekocampuslive.beans;

/**
 * 直播房间信息
 */
public class LiveCell {

    private int liveRoomId;         //房间号
    private String hostId;      //直播id
    private String hostName;    //直播姓名
    private String hostAvatar;   //直播头像
    private String liveCover;    //直播封面
    private String liveTitle;    //直播主题
    private int watchNum; //观看人数

    public LiveCell() {
    }

    @Override
    public String toString() {
        return "LiveCell{" +
                "liveRoomId=" + liveRoomId +
                ", hostId='" + hostId + '\'' +
                ", hostName='" + hostName + '\'' +
                ", hostAvatar='" + hostAvatar + '\'' +
                ", liveCover='" + liveCover + '\'' +
                ", liveTitle='" + liveTitle + '\'' +
                ", watchNum=" + watchNum +
                '}';
    }

    public LiveCell(int liveRoomId, String hostId, String hostName, String hostAvatar, String liveCover, String liveTitle, int watchNum) {
        this.liveRoomId = liveRoomId;
        this.hostId = hostId;
        this.hostName = hostName;
        this.hostAvatar = hostAvatar;
        this.liveCover = liveCover;
        this.liveTitle = liveTitle;
        this.watchNum = watchNum;
    }

    public int getLiveRoomId() {
        return liveRoomId;
    }

    public void setLiveRoomId(int liveRoomId) {
        this.liveRoomId = liveRoomId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostAvatar() {
        return hostAvatar;
    }

    public void setHostAvatar(String hostAvatar) {
        this.hostAvatar = hostAvatar;
    }

    public String getLiveCover() {
        return liveCover;
    }

    public void setLiveCover(String liveCover) {
        this.liveCover = liveCover;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public int getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(int watchNum) {
        this.watchNum = watchNum;
    }
}