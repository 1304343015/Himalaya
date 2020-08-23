package com.example.himalaya.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscriptDao implements  ISubscriptDao{
    private SubscriptHelper helper;
    private ISubDaoCallback callback=null;
    private static SubscriptDao subscriptDao=new SubscriptDao();

    private SubscriptDao(){
        helper=new SubscriptHelper(BaseApplication.getContext());
    }
    public static SubscriptDao getInstance(){
        return subscriptDao;
    }

    public void setCallback(ISubDaoCallback callback){
        this.callback=callback;
    }
    public void addSubscript(Album album){
        SQLiteDatabase db=null;
        boolean isAddSuccess=false;
        try{
            db=helper.getWritableDatabase();
            db.beginTransaction();
            ContentValues cv=new ContentValues();
            cv.put(Constants.SUB_COVER_URL,album.getCoverUrlSmall());
            cv.put(Constants.SUB_TITLE,album.getAlbumTitle());
            cv.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            cv.put(Constants.SUB_TRACKS_COUNT,album.getIncludeTrackCount());
            cv.put(Constants.SUB_PLAY_COUNT,album.getPlayCount());
            cv.put(Constants.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            cv.put(Constants.SUB_ALBUM_ID,album.getId());
            db.insert(Constants.SUB_TABLE_NAME,null,cv);
            db.setTransactionSuccessful();
            isAddSuccess=true;
        }catch (Exception e){
            e.printStackTrace();
            isAddSuccess=false;
        }finally {
            if(db!=null){
                db.endTransaction();
               // db.close();
            }
            if(callback!=null){
                callback.onAddResult(isAddSuccess);
            }
        }

    }


    @Override
    public void delSubscript(Album album) {
        SQLiteDatabase db=null;
        boolean isDelSuccess=false;
        try{
            db=helper.getWritableDatabase();
            db.beginTransaction();
            db.delete(Constants.SUB_TABLE_NAME,Constants.SUB_ALBUM_ID+"=?",new String[]{album.getId()+""});
            db.setTransactionSuccessful();
            isDelSuccess=true;
        }catch (Exception e){
            e.printStackTrace();
            isDelSuccess=false;
        }finally {
            if (db != null) {
                db.endTransaction();
              //  db.close();
            }
            if (callback != null) {
                callback.onDelResult(isDelSuccess);
            }
        }

    }

    @Override
    public void getAllSubscript() {
        SQLiteDatabase db=null;
        List<Album> list=new ArrayList<>();
        try{
            db=helper.getReadableDatabase();
            db.beginTransaction();
            Cursor cursor=db.query(Constants.SUB_TABLE_NAME,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                Album album=new Album();
                album.setCoverUrlSmall(cursor.getString(cursor.getColumnIndex(Constants.SUB_COVER_URL)));
                album.setAlbumTitle(cursor.getString(cursor.getColumnIndex(Constants.SUB_TITLE)));
                album.setAlbumIntro(cursor.getString(cursor.getColumnIndex(Constants.SUB_DESCRIPTION)));
                album.setIncludeTrackCount(cursor.getInt(cursor.getColumnIndex(Constants.SUB_TRACKS_COUNT)));
                album.setPlayCount(cursor.getInt(cursor.getColumnIndex(Constants.SUB_PLAY_COUNT)));
                Announcer announcer=new Announcer();
                announcer.setNickname(cursor.getString(cursor.getColumnIndex(Constants.SUB_AUTHOR_NAME)));
                album.setAnnouncer(announcer);
                album.setId(cursor.getInt(cursor.getColumnIndex(Constants.SUB_ALBUM_ID)));
                list.add(album);
            }
            cursor.close();
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
              //  db.close();
            }
            if (callback != null) {
                callback.onGetSubList(list);
            }
        }

    }


}
