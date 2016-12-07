package cn.clickwise.utils.sshutils;


public interface ExecTaskCallbackHandler {

    void onFail();

    void onComplete(String completeString);
}
