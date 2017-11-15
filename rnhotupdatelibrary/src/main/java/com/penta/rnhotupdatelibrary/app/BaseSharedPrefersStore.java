package com.penta.rnhotupdatelibrary.app;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * SharedPreferences数据存储方式的封装类的基类
 * SharedPreferences是一种轻型的数据存储方式，它的本质是基于XML文件存储key-value键值对数据，
 * 通常用来存储一些简单的配置信息，只支持存储boolean，int，float，long和String五种简单的数据类型。
 */

public abstract class BaseSharedPrefersStore {

    protected SharedPreferences mSharePrefers;
    protected SharedPreferences.Editor mEditor;

    public BaseSharedPrefersStore(Context context, String fileName) {
        context = context.getApplicationContext();
        mSharePrefers = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        mEditor = mSharePrefers.edit();
    }

    public void saveBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public void saveFloat(String key, float value) {
        mEditor.putFloat(key, value);
        mEditor.commit();
    }

    public void saveInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public void saveLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public void saveString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void saveShort(String key, short value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }


    public void clear(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public void clear() {
        mEditor.clear();
        mEditor.commit();
    }

    protected boolean getBoolean(String key, boolean defValue) {
        return mSharePrefers.getBoolean(key, defValue);
    }

    protected float getFloat(String key, float defValue) {
        return mSharePrefers.getFloat(key, defValue);
    }

    protected int getInt(String key, int defValue) {
        return mSharePrefers.getInt(key, defValue);
    }

    protected long getLong(String key, long defValue) {
        return mSharePrefers.getLong(key, defValue);
    }

    protected String getString(String key, String defValue) {
        return mSharePrefers.getString(key, defValue);
    }
}
