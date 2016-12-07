package cn.clickwise;

import android.util.Log;

import cn.clickwise.config.Constants;
import cn.clickwise.utils.helper.SDHelper;
import cn.clickwise.utils.other.AES_256Crypto;
import cn.clickwise.utils.other.IDEACrypto;

/**
 * Created by T420s on 2016/11/12.
 */
public class CommandThread extends Thread {
    @Override
    public void run() {
        super.run();
        AppContext.sshAll.append(Constants.LOG_LINE + "路由信息" + Constants.LOG_LINE + Constants.SPAN);
        AppContext.sshAll.append(Constants.LOG_LINE + "------" + Constants.LOG_LINE + Constants.SPAN + Constants.SPAN);
        AppContext.sshAll.append(AppContext.wifiBuffer);
        AppContext.sshAll.append(Constants.LOG_LINE + "SSH登录前执行命令" + Constants.LOG_LINE);
        AppContext.sshAll.append(Constants.LOG_LINE + "--------------" + Constants.LOG_LINE);
        AppContext.sshAll.append(AppContext.pingBeforeBuffer);
        AppContext.sshAll.append(Constants.LOG_LINE + "SSH登录后执行命令" + Constants.LOG_LINE + Constants.SPAN);
        AppContext.sshAll.append(Constants.LOG_LINE + "---------------" + Constants.LOG_LINE + Constants.SPAN);
        AppContext.sshAll.append(AppContext.pingCommandBuffer);

        SDHelper.saveFileToSD(SDHelper.getFile(SDHelper.filePath, SDHelper.fileName, SDHelper.fileType), AppContext.sshAll.toString());
        AppContext.wifiBuffer.setLength(0);
        AppContext.pingBeforeBuffer.setLength(0);
        AppContext.pingCommandBuffer.setLength(0);
        AppContext.sshAll.setLength(0);
//        AppContext.wifiBuffer.delete(0, AppContext.wifiBuffer.length() - 1);
//        AppContext.pingBeforeBuffer.delete(0, AppContext.pingBeforeBuffer.length() - 1);
//        AppContext.pingCommandBuffer.delete(0, AppContext.pingCommandBuffer.length() - 1);
//        AppContext.sshAll.delete(0, AppContext.sshAll.length() - 1);
    }
}
