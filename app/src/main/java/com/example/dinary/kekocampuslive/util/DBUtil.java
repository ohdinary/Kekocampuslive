package com.example.dinary.kekocampuslive.util;

import android.database.sqlite.SQLiteDatabase;

import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.beans.User;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

/**
 * 数据库操作工具类
 */
public class DBUtil {

    /**
     * 判断数据表中有没有用户信息,如果没有再将用户信息保存到用户表中
     * @return
     */
    public static boolean saveNameAndPsw(String accountStr, String passwordStr){
        List<User> users = DataSupport.where("userName = ? and userPassword = ?", accountStr, passwordStr)
                .limit(1)
                .find(User.class);

        User user;
        if (users != null && users.size() != 0){
            user = users.get(0);
            return false;
        }else {
            user = new User(accountStr,passwordStr);
            user.save();
            MyApplication.user = user;
            return true;
        }
    }
}
