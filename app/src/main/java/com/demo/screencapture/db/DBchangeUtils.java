package com.demo.screencapture.db;

import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Picture;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/18
 */

public class DBchangeUtils {
    private DatabaseSqlHelper db;

    public DBchangeUtils(DatabaseSqlHelper db) {
        this.db = db;
    }

    /**
     * 插入一个点到tab_point表
     */
    public boolean insert(DBPictureVO pictureVO) {
        String INSERT_POINT = "INSERT INTO tab_picture(timeTemp,isChange,picBegin,picStop,cleanAllPic) VALUES (" + pictureVO.timeTemp + "," + pictureVO.isChange + "," + pictureVO.picBegin + "," + pictureVO.picStop + "," + pictureVO.cleanAllPic + ")";
        try {
            db.execSQL(INSERT_POINT);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 查询出数据库中所有的Points
     * public long timeTemp;// 时间戳
     * public boolean isChange;
     * public boolean picBegin;
     * public boolean picStop;
     * public boolean cleanAllPic;//删除所有图片
     */
    public List<DBPictureVO> findAllTabPic() {
        String sql_select = "select * from tab_point order by id";
        //请补充sql执行语句
        Cursor cursor = db.query(sql_select);
        int IDIndex = cursor.getColumnIndex("id");
        int timeTempIndex = cursor.getColumnIndex("timeTemp");
        int isChangeIndex = cursor.getColumnIndex("isChange");
        int picBeginIndex = cursor.getColumnIndex("picBegin");
        int picStopIndex = cursor.getColumnIndex("picStop");
        int cleanAllPicIndex = cursor.getColumnIndex("cleanAllPic");
        List<DBPictureVO> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            DBPictureVO pic = new DBPictureVO();
            pic.id = cursor.getInt(IDIndex);
            pic.timeTemp = cursor.getLong(timeTempIndex);
            pic.isChange = cursor.getInt(isChangeIndex);
            pic.picBegin = cursor.getInt(picBeginIndex);
            pic.picStop = cursor.getInt(picStopIndex);
            pic.cleanAllPic = cursor.getInt(cleanAllPicIndex);
            list.add(pic);
        }
        return list;
    }
}
