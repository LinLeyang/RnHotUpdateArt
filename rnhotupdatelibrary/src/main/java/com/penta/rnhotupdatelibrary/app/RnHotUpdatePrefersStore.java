package com.penta.rnhotupdatelibrary.app;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by linyueyang on 9/7/17.
 * <p>
 * 存储rn 各业务的版本号  用于判断是否进行热修复操作 不予任何其他类耦合
 */

public class RnHotUpdatePrefersStore extends BaseSharedPrefersStore {

    private static final String PREFERS_FILE_NAME = "rn_hot_update_sp";

    private static RnHotUpdatePrefersStore mInstance;

    private RnHotUpdatePrefersStore(Context context) {
        super(context, PREFERS_FILE_NAME);
    }

    public static RnHotUpdatePrefersStore getInstance() {
        if (mInstance == null) {
            synchronized (RnHotUpdatePrefersStore.class) {
                if (mInstance == null) {
                    mInstance = new RnHotUpdatePrefersStore(RnUpdateLogic.ins().getContext());
                }
            }
        }
        return mInstance;
    }

    public void setRnDiffVersion(String name, String version) {
        try {
            saveString(name + "_" + AppInfoUtils.getVersionCode(RnUpdateLogic.ins().getContext()), version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getRnDiffVersion(String name) {
        try {
            return getString(name + "_" + AppInfoUtils.getVersionCode(RnUpdateLogic.ins().getContext()), "0");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "0";
    }
}
