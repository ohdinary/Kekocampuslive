package com.example.dinary.kekocampuslive.beans;

/**
 * 房间+观众
 */
public class Watcher{

  private Integer id = 0;
  private String userId;
  private Integer roomId = -1;

  public Watcher(){}

  public Watcher(Integer id, String userId, Integer roomId) {
    this.id = id;
    this.userId = userId;
    this.roomId = roomId;
  }

  public Watcher(Integer roomId, String userId) {
    this.roomId = roomId;
    this.userId = userId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getRoomId() {
    return roomId;
  }

  public void setRoomId(Integer roomId) {
    this.roomId = roomId;
  }


  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

}
