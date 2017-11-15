基于Google diff_match_patch 的增量热更新框架

###流程图

![Rn热更新流程图](http://ozckzyh1u.bkt.clouddn.com/rn_hot_update.png)

图中的几个概念

common.bundle:rn基础库打成的bundle文件，不包含业务代码

total.bundle:包含业务的全量包，和正常的打包流程一致（不同业务生成不同的total.bundle）

diff.bundle:common和total的差异文件，也就是业务相关代码

patch.bundle:和diff没有本质区别，用于区分是用于更新的补丁还是正式包里面的业务代码

这里是我的框架定义的，可以修改框架自己定义

###流程的具体说明

####开发完成之后：
1.首先我们要按照rn打包的命令生成common.bundle至assets文件夹下，要注意没有assets文件夹的需要手动创建，否则会报错。其中的blank.js是一个空的js文件只引用一定要使用的react核心类库。

```
react-native bundle --platform android --dev false --entry-file ./blank.js --bundle-output ../app/src/main/assets/common.bundle --assets-dest ../app/src/main/res/
```

2.然后是全量的total.bundle，可以看出两个文件是打在不同的目录的，因为common是需要被打入正式包的，total只是一个临时的文件，用于差异化比较。

```
react-native bundle --platform android --dev false --entry-file ./bus_a.js --bundle-output ./hot_update/bus_a_total.bundle
```

3.diff_match_patch的diff_main方法和patch_make方法生成差异文件，写入diff.bundle文件，至assets文件夹

4.按照正常流程打App的正式包即可

####当线上出现问题时：流程和开发时基本类似
1.修改对应的js文件，修正问题，测试完成后
2.和正式流程一样生成全量的total.bundle
3.和正式的一样生成patch.bundle并压缩成patch.zip
4.交由服务端提供下载更新服务

####用户使用时的流程解释
1.用于打开rn页面时我们在加载之前会开一个异步的线程，我们后续再说

2.不管需不需要更新，都需要读取_total（本地有去本地获取，如果没有则合并assets里面的common 和 diff文件 生成到本地存储）

```
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
```

3.最后加载出来

4.异步的过程，去服务端获取当前业务的版本信息看看是否需要进行更新，如果需要则下载对应业务的补丁包。

```
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
```

5.下载完成后解压到对应的文件夹，和原有的common.bundle文件合并生成新的total.bundle覆盖原有文件

```
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
```
6.清空为补丁包创建的patch文件夹所有内容

###如何接入热更新库

只需要把rnhotupdatelibrary拷贝到自己新建的一个model里面，或者打成aar引入到工程即可

在Application里面初始化

```
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Rn热更新需要上下文获取一些设备信息，建议这里需要初始化
        RnUpdateLogic.ins().init(this);
    }
}
```
在你的RnActivity加载bundle之前加入热更新的流程

```
RnUpdateLogic.ins().checkRNUpdateAndMergeBundle(RNHotUpdateConstant.BUS_A_BUSINESS_NAME);
```

加载bundle文件的时候设置好你的bundle路径即可，这两个方法只需要定义好你的业务名字即可

```
 mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                //2:bundle文件要从本地路径里面获取 而不是之前的assets目录
                .setJSBundleFile(RnUpdateLogic.ins().getTotalVersionBundlePath(RNHotUpdateConstant.BUS_A_BUSINESS_NAME))
```

淡然服务端要提供根据你的业务名，app版本给出最新的版本号，下载地址来判断是否需要更新。
