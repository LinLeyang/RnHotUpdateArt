package com.penta.rnhotupdatelibrary.app;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import com.penta.rnhotupdatelibrary.http.Data;
import com.penta.rnhotupdatelibrary.http.HttpCallBack;
import com.penta.rnhotupdatelibrary.http.HttpUtil;

import java.io.File;

/**
 * Created by linyueyang on 11/2/17.
 * <p>
 * 初始化逻辑 传递上下文信息
 *
 * 现阶段对外的唯一入口
 */

public class RnUpdateLogic {

    private static Context context;

    private static RnUpdateLogic self;

    private RnUpdateLogic() {

    }

    public void init(Context context) {
        this.context = context;
    }

    public static RnUpdateLogic ins() {
        if (self == null) {
            synchronized (RnUpdateLogic.class) {
                if (self == null) {
                    self = new RnUpdateLogic();
                }
            }
        }
        return self;
    }

    public Context getContext() {
        return context;
    }

    public String getTotalVersionBundlePath(String businessName) {
        return PathUtils.getTotalVersionBundlePath(businessName);
    }

    public void checkRNUpdateAndMergeBundle(final String businessName) {

        //网络请求服务端 rap提供mock服务 zip包是放在了七牛云上 实际开发时一定是换成自己的服务端
        HttpUtil.get("http://rapapi.org/mockjsdata/29016/rnhot", new HttpCallBack<Data>() {
            @Override
            public void onSuccess(Data data) {
                //根据版本号判断当前的业务是否需要更新 需要更新则直接下载
                String rnVersion = RnHotUpdatePrefersStore.getInstance().getRnDiffVersion(businessName);
                int remoteVersion = data.getVersion();
                int localVersion = Integer.parseInt(rnVersion);
                if ((localVersion < remoteVersion) && data.getResourceUrl() != null) {
                    downLoadBundle(data.getResourceUrl(), data.getVersion() + "", businessName);
                }
            }
        });
        //不管需不需要更新都需要进行的：读取_total（本地有去本地获取，没有从Assets读取）
        File total_bundle = new File(PathUtils.getTotalVersionBundlePath(businessName));
        if (total_bundle != null && total_bundle.exists()) {

        } else {
            //删除上前目录的所有文件 防止之前版本的文件导致文件目录过大
            File total_folder = new File(PathUtils.getTotalVersionFolderPath());
            if (total_folder != null && total_folder.exists()) {

            } else {
                FileUtils.deleteDir(PathUtils.getBundleFileDir(), false);
            }
            //如果没有则合并assets里面的common 和 diff文件 生成到本地存储
            FileUtils.mergePatAndAsset(context, RNHotUpdateConstant.COMMON_BUNDLE_NAME,
                    PathUtils.getBusinessBundleName(businessName),
                    PathUtils.getTotalVersionBundlePath(businessName));
        }
    }


    /**
     * 下载最新Bundle
     */
    private void downLoadBundle(String url, String version, String businessName) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager
                .Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationUri(Uri.parse("file://" + PathUtils.getPatchZipPath(businessName)));
        //监听到下载完成我们会通过监听
        registerDownloadReceive(downloadManager.enqueue(request), version, businessName);
    }

    /**
     * 注册下载之后的监听
     *
     * @param mDownLoadId
     * @param version
     * @param businessName
     */
    private void registerDownloadReceive(final long mDownLoadId, final String version, final String businessName) {

        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                long completeId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completeId == mDownLoadId) {
                    //新线程处理：压缩包解压，bundle合并 删除下载的补丁包资源等操作
                    mergePatch(businessName, version);
                }
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 新线程处理：压缩包解压，bundle合并 删除下载的补丁包资源等操作
     *
     * @param businessName
     * @param version
     */
    private void mergePatch(final String businessName, final String version) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //这里是指定的zip包的名字
                FileUtils.decompression(PathUtils.getPatchFileDir(), PathUtils.getPatchZipPath(businessName));
                FileUtils.mergePatAndBundle(context, RNHotUpdateConstant.COMMON_BUNDLE_NAME, PathUtils.getPatchBundlePath(businessName), PathUtils.getTotalVersionBundlePath(businessName));

                if (null != version)
                    RnHotUpdatePrefersStore.getInstance().setRnDiffVersion(businessName, version);
                //最后删除本次下载的全部补丁包
                FileUtils.deleteDir(PathUtils.getPatchFileDir(), false);
            }
        }).start();

    }


}
