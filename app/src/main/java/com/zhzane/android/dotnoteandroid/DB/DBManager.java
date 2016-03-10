package com.zhzane.android.dotnoteandroid.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KWENS on 2016/2/16.
 */

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;
    private JSONArray jsons;
    public User currentUser;

    public DBManager(Context context){
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();

        List<User> users = queryCurrentUser();
        if (users.size() > 0){
            for (User user : users){
                currentUser = user;
            }
        }else {
            currentUser = new User();
            currentUser.UserId = 1;
            currentUser.UserName = "username";
            currentUser.TotalMoney = 0.0;
            currentUser.RelatedUserId = "";
            currentUser.MAC = "";
            addUser(currentUser);
        }
    }
    /*添加账单*/

    public void addBill(Bill bill){
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO Bill (UserId,Money,CreateTime,LastModifiedTime,ExternalId,TagId,Describe) values (?,?,?,?,?,?,?)",
                    new Object[]{bill.UserId,bill.Money,bill.CreateTime,bill.LastModifiedTime,bill.ExternalId,bill.TagId,bill.Describe});
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    public void addBills(List<Bill> bills){
        db.beginTransaction();
        try {
            for (Bill bill : bills) {
                db.execSQL("INSERT INTO Bill (UserId,Money,CreateTime,LastModifiedTime,ExternalId,TagId,Describe) values (?,?,?,?,?,?,?)",
                        new Object[]{bill.UserId,bill.Money,bill.CreateTime,bill.LastModifiedTime,bill.ExternalId,bill.TagId,bill.Describe});
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }
    /*添加用户*/
    public void addUser(User user){
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO User (UserId,UserName,TotalMoney,RelatedUserId,MAC) VALUES(?,?,?,?,?)",
                    new Object[]{user.UserId, user.UserName, user.TotalMoney, user.RelatedUserId, user.MAC});
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    public void addUser(List<User> users){
        db.beginTransaction();
        try {
            for (User user : users) {
                db.execSQL("INSERT INTO User (UserId,UserName,TotalMoney,RelatedUserId,MAC) VALUES(?,?,?,?,?)",
                        new Object[]{user.UserId,user.UserName,user.TotalMoney,user.RelatedUserId,user.MAC});
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }
    /*添加标签*/
    public void addTag(Tag tag){
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO Tag (TagId,TagName,UseNum,Describe) VALUES(?,?,?,?)",
                    new Object[]{tag.TagId, tag.TagName, tag.UseNum, tag.Describe});
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    public void addTag(List<Tag> tags){
        db.beginTransaction();
        try {
            for (Tag tag : tags) {
                db.execSQL("INSERT INTO Tag (TagId,TagName,UseNum,Describe) VALUES(?,?,?,?)",
                        new Object[]{tag.TagId,tag.TagName,tag.UseNum,tag.Describe});
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }
    /*账单修改*/
    public void updateBill(Bill bill){
        ContentValues cv = new ContentValues();
        cv.put("UserId",bill.UserId);
        cv.put("Money",bill.Money);
        cv.put("CreateTime",bill.CreateTime);
        cv.put("LastModifiedTime",bill.LastModifiedTime);
        cv.put("ExternalId",bill.ExternalId);
        cv.put("TagId", bill.TagId);
        cv.put("Describe",bill.Describe);
        db.update("Bill", cv, "_id = ?", new String[]{Integer.toString(bill._id)});
    }
    /*用户修改*/
    public void updateUser(User user){
        ContentValues cv = new ContentValues();
        cv.put("UserId",user.UserId);
        cv.put("UserName",user.UserName);
        cv.put("TotalMoney",user.TotalMoney);
        cv.put("RelatedUserId",user.RelatedUserId);
        cv.put("MAC",user.MAC);
        db.update("User", cv, "MAC = ?", new String[]{user.MAC});
    }
    /*标签修改*/
    public void updateTag(Tag tag){
        ContentValues cv = new ContentValues();
        cv.put("TagID",tag.TagId);
        cv.put("TagName",tag.TagName);
        cv.put("UseNum",tag.UseNum);
        cv.put("Describe",tag.Describe);
        db.update("Tag", cv, "TagId = ?", new String[]{Integer.toString(tag.TagId)});
    }
    /*删除账单*/
    public void deleteBill(Bill bill){
        db.beginTransaction();
        try {
            db.delete("Bill","_id = ?",new String[]{Integer.toString(bill._id)});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }
    /*删除用户*/
    public void deleteUser(User user){
        db.beginTransaction();
        try {
            db.delete("User","UserId = ?",new String[]{Integer.toString(user.UserId)});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }
    /*查询全部*/
    public Cursor queryTheCursor(String tableName){
        Cursor c = db.rawQuery("SELECT * FROM " +tableName,null);
        return c;
    }

    public Cursor queryTheCursorByWhere(String tableName,String column,String where){
        Cursor c = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + column + " = ?",new String[]{where});
        return c;
    }

    public Cursor queryTheCursor(String tableName,String whereStr){
        Cursor c = db.rawQuery("SELECT * FROM " +tableName + " ORDER BY "+whereStr,null);
        return c;
    }
    /*查询账单列表*/
    public List<Bill> queryBill() throws JSONException {
        ArrayList<Bill> bills = new ArrayList<Bill>();
        jsons = new JSONArray();
        Cursor c = queryTheCursor("Bill");
        while (c.moveToNext()){
            Bill bill = new Bill();
            bill._id = c.getInt(c.getColumnIndex("_id"));
            bill.UserId = c.getString(c.getColumnIndex("UserId"));
            bill.Money = c.getDouble(c.getColumnIndex("Money"));
            bill.CreateTime = c.getString(c.getColumnIndex("CreateTime"));
            bill.LastModifiedTime = c.getString(c.getColumnIndex("LastModifiedTime"));
            bill.ExternalId = c.getString(c.getColumnIndex("ExternalId"));
            bill.TagId = c.getString(c.getColumnIndex("TagId"));
            bill.Describe = c.getString(c.getColumnIndex("Describe"));

            if (bill.UserId.equals(String.valueOf(currentUser.UserId))) {
                jsons.put(bill.toJSON(String.valueOf(currentUser.UserId)));
            }

            bills.add(bill);
        }
        c.close();
        return bills;
    }
    /*查询用户列表*/
    public List<User> queryCurrentUser(){
        ArrayList<User> users = new ArrayList<User>();
        Cursor c = queryTheCursorByWhere("User", "UserId", "1");
        while (c.moveToNext()){
            User user = new User();
            user._id = c.getInt(c.getColumnIndex("_id"));
            user.UserId = c.getInt(c.getColumnIndex("UserId"));
            user.UserName = c.getString(c.getColumnIndex("UserName"));
            user.TotalMoney = c.getDouble(c.getColumnIndex("TotalMoney"));
            user.RelatedUserId = c.getString(c.getColumnIndex("RelatedUserId"));
            user.MAC = c.getString(c.getColumnIndex("MAC"));
            users.add(user);
        }
        c.close();
        return users;
    }

    public List<User> queryUser() {
        ArrayList<User> users = new ArrayList<User>();
        Cursor c = queryTheCursor("User");
        while (c.moveToNext()){
            User user = new User();
            user._id = c.getInt(c.getColumnIndex("_id"));
            user.UserId = c.getInt(c.getColumnIndex("UserId"));
            user.UserName = c.getString(c.getColumnIndex("UserName"));
            user.TotalMoney = c.getDouble(c.getColumnIndex("TotalMoney"));
            user.RelatedUserId = c.getString(c.getColumnIndex("RelatedUserId"));
            user.MAC = c.getString(c.getColumnIndex("MAC"));
            users.add(user);
        }
        c.close();
        return users;
    }
    /*查询标签列表*/
    public List<Tag> queryTag(){
        ArrayList<Tag> tags = new ArrayList<Tag>();
        Cursor c = queryTheCursor("Tag","UseNum");
        while (c.moveToNext()){
            Tag tag = new Tag();
            tag.TagId = c.getInt(c.getColumnIndex("TagId"));
            tag.TagName = c.getString(c.getColumnIndex("TagName"));
            tag.UseNum = c.getInt(c.getColumnIndex("UseNum"));
            tag.Describe = c.getString(c.getColumnIndex("Describe"));
            tags.add(tag);
        }
        c.close();
        return tags;
    }

    public void toJSON(String userId){

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "BillJson.json";
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()){
            dir.mkdirs();
        }

        File file = new File(path,fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                file.delete();
                file.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try{
            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(jsons.toString());
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*关闭数据库*/
    public void CloseDB(){
        db.close();
    }

}
