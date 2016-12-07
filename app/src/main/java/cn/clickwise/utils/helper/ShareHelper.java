package cn.clickwise.utils.helper;

import android.content.Context;

import cn.clickwise.onekeyshare.OnekeyShare;
import cn.clickwise.onekeyshare.OnekeyShareTheme;

/**
 * Created by T420s on 2016/10/21.
 */
public class ShareHelper {
    /**
     * 演示调用ShareSDK执行分享
     *
     * @param context
     * @param platformToShare  指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
     * @param showContentEdit  是否显示编辑页
     */
    public static void showShare(Context context, String platformToShare, boolean showContentEdit) {
        /**
         * 指定平台分享
         */
        /*Platform.ShareParams shareParams=new Platform.ShareParams();
        shareParams.setTitle(SDHelper.getFileName());
        shareParams.setImageUrl(randomPic()[0]);
        shareParams.setFilePath(SDHelper.getSaveLogPath());
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                ToastUtil.make("onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtil.make("onError");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtil.make("onCancel");
            }
        });
        wechat.share(shareParams);*/
        /**
         * 群分享
         */
        OnekeyShare oks = new OnekeyShare();
        //oks.set
        oks.setSilent(showContentEdit);

        //oks.setPlatform(Wechat.NAME);
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
        //oks.setDialogMode();
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        //oks.setAddress("12345678901"); //分享短信的号码和邮件的地址
        oks.setTitle(SDHelper.getFileName());
        //oks.setTitleUrel(Urel.clickWiseLogUrel);
        //oks.setText("路由器调试日志，技术人员辛苦啦！");
        oks.setImageUrl(randomPic()[0]);
        //oks.setImagePath(context.getResources().getDrawable(R.drawable.clickwise).toString());
        oks.setFilePath(SDHelper.getSaveLogPath());
        //oks.setUrl(AppContext.SAVE_LOG_PATH);
        //oks.setImagePath(Urle.clickWiseLogUrle);
        //oks.setImageArray();

        //oks.setImagePath("/sdcard/test-pic.jpg");  //分享sdcard目录下的图片
        //oks.setImageUrle(randomPic()[0]);
        //oks.setUrle("http://www.mob.com"); //微信不绕过审核分享链接
        //oks.setFilePath("/sdcard/test-pic.jpg");  //filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
//        oks.setComment("分享"); //我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
//        oks.setSite("ShareSDK");  //QZone分享完之后返回应用时提示框上显示的名称
//        oks.setSiteUrle("http://mob.com");//QZone分享参数
//        oks.setVenueName("ShareSDK");
//        oks.setVenueDescription("This is a beautiful place!");
        // 将快捷分享的操作结果将通过OneKeyShareCallback回调
        //oks.setCallback(new OneKeyShareCallback());
        // 去自定义不同平台的字段内容
        //oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
        // 在九宫格设置自定义的图标
//        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
//        String label = "ShareSDK";
//        View.OnClickListener listener = new View.OnClickListener() {
//            public void onClick(View v) {
//
//            }
//        };
//        oks.setCustomerLogo(logo, label, listener);

        // 为EditPage设置一个背景的View
        //oks.setEditPageBackground(getPage());
        // 隐藏九宫格中的新浪微博
        // oks.addHiddenPlatform(SinaWeibo.NAME);
        // 启动分享
        oks.show(context);
    }
    public static String[] randomPic() {
        String url = "http://git.oschina.net/alexyu.yxj/MyTmpFiles/raw/master/kmk_pic_fld/";
        String urlSmall = "http://git.oschina.net/alexyu.yxj/MyTmpFiles/raw/master/kmk_pic_fld/small/";
        String[] pics = new String[] {
                "120.JPG",
                "127.JPG",
                "130.JPG",
                "18.JPG",
                "184.JPG",
                "22.JPG",
                "236.JPG",
                "237.JPG",
                "254.JPG",
                "255.JPG",
                "263.JPG",
                "265.JPG",
                "273.JPG",
                "37.JPG",
                "39.JPG",
                "IMG_2219.JPG",
                "IMG_2270.JPG",
                "IMG_2271.JPG",
                "IMG_2275.JPG",
                "107.JPG"
        };
        int index = (int) (System.currentTimeMillis() % pics.length);
        return new String[] {
                url + pics[index],
                urlSmall + pics[index]
        };
    }
}
