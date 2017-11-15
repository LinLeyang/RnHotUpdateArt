package com.penta.rnhotupdateart;

import android.app.Application;

import com.penta.rnhotupdatelibrary.app.RnUpdateLogic;

/**
 * Created by linyueyang on 11/9/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Rn热更新需要上下文获取一些设备信息，建议这里需要初始化
        RnUpdateLogic.ins().init(this);
    }
}
