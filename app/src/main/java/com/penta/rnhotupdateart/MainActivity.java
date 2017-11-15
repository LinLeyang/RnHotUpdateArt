package com.penta.rnhotupdateart;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;
import com.penta.rnhotupdatelibrary.app.BaseRnActivity;
import com.penta.rnhotupdatelibrary.app.RNHotUpdateConstant;
import com.penta.rnhotupdatelibrary.app.RnUpdateLogic;

/**
 * 此Rn热更新库只需要
 * 在我们正常接入Rn的基础上添加两行代码即可实现 热更新的功能
 * <p>
 * 即下面的1和2
 * <p>
 * BaseRnActivity是封装的一个简化Rn Activity 开发流程的类 集成直接可用
 * 当然自己写也完全没有问题
 */

public class MainActivity extends BaseRnActivity {

    FrameLayout rn_frame;

    @Override
    protected void onCreateRnView() {
        setContentView(R.layout.activity_main);
        rn_frame = (FrameLayout) findViewById(R.id.rn_frame);
        //1:判断是否需要热更新，并合并common和business bundle文件
        RnUpdateLogic.ins().checkRNUpdateAndMergeBundle(RNHotUpdateConstant.BUS_A_BUSINESS_NAME);
    }

    @Override
    protected void initRnView() {
        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                //2:bundle文件要从本地路径里面获取 而不是之前的assets目录
                .setJSBundleFile(RnUpdateLogic.ins().getTotalVersionBundlePath(RNHotUpdateConstant.BUS_A_BUSINESS_NAME))
                .setJSMainModuleName("bus_a")
                .addPackage(new MainReactPackage())
                .setUseDeveloperSupport(false)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();

        Bundle bundle = new Bundle();
        bundle.putInt("entrance", 4);

        mReactRootView.startReactApplication(mReactInstanceManager, "RnHotUpdateArt", bundle);
        rn_frame.addView(mReactRootView);

    }
}
