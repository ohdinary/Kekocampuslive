package com.example.dinary.kekocampuslive.beans;

import org.litepal.crud.DataSupport;

/**
 *  用户实体类
 */
public class User extends DataSupport{

  private int userId;
  private String userName;
  private String userRealName;
  private String userPassword;
  private String userHead;
  private String userPhone;
  private String userIdCard;
  private String userSex;
  private int schoolId;
  private int liveId;
  private String levelId;
  private String userSignature;
  private String userEmail;

  public User(){}

  public User(String userName, String userPassword) {
    this.userName = userName;
    this.userPassword = userPassword;
  }

  public User(String userName, String userRealName, String userPassword, String userHead, String userPhone, String userIdCard, String userSex, int schoolId, int liveId, String levelId, String userSignature, String userEmail) {
    this.userName = userName;
    this.userRealName = userRealName;
    this.userPassword = userPassword;
    this.userHead = userHead;
    this.userPhone = userPhone;
    this.userIdCard = userIdCard;
    this.userSex = userSex;
    this.schoolId = schoolId;
    this.liveId = liveId;
    this.levelId = levelId;
    this.userSignature = userSignature;
    this.userEmail = userEmail;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserRealName() {
    return userRealName;
  }

  public void setUserRealName(String userRealName) {
    this.userRealName = userRealName;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public String getUserHead() {
    return userHead;
  }

  public void setUserHead(String userHead) {
    this.userHead = userHead;
  }

  public String getUserPhone() {
    return userPhone;
  }

  public void setUserPhone(String userPhone) {
    this.userPhone = userPhone;
  }

  public String getUserIdCard() {
    return userIdCard;
  }

  public void setUserIdCard(String userIdCard) {
    this.userIdCard = userIdCard;
  }

  public String getUserSex() {
    return userSex;
  }

  public void setUserSex(String userSex) {
    this.userSex = userSex;
  }

  public int getSchoolId() {
    return schoolId;
  }

  public void setSchoolId(int schoolId) {
    this.schoolId = schoolId;
  }

  public int getLiveId() {
    return liveId;
  }

  public void setLiveId(int liveId) {
    this.liveId = liveId;
  }

  public String getLevelId() {
    return levelId;
  }

  public void setLevelId(String levelId) {
    this.levelId = levelId;
  }

  public String getUserSignature() {
    return userSignature;
  }

  public void setUserSignature(String userSignature) {
    this.userSignature = userSignature;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }
}