package cn.clickwise.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.clickwise.R;
import cn.clickwise.utils.sshutils.ConnectionStatusListener;
import cn.clickwise.utils.sshutils.SessionController;
import cn.clickwise.utils.sshutils.SessionUserInfo;


public class SshConnectFragmentDialog  extends DialogFragment implements View.OnClickListener {

    private EditText mUserEdit;
    private EditText mHostEdit;
    private EditText mPasswordEdit;
    private EditText mPortNumEdit;
    private SessionUserInfo mSUI;
    private ConnectionStatusListener mListener;
    private Button mBtnDialogExit;
    public Button mBtnDialogTest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setListener(ConnectionStatusListener listener){
        mListener = listener;
    }

    public static SshConnectFragmentDialog newInstance() {
        return new SshConnectFragmentDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = initView(inflater, container);
        setListener();
        return v;
    }

    private void setListener() {
        mBtnDialogTest.setOnClickListener(this);
        mBtnDialogExit.setOnClickListener(this);
    }

    @NonNull
    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mUserEdit = (EditText) v.findViewById(R.id.username);
        mHostEdit = (EditText) v.findViewById(R.id.hostname);
        mPasswordEdit = (EditText) v.findViewById(R.id.password);
        mPortNumEdit = (EditText) v.findViewById(R.id.portnum);
        mBtnDialogTest = (Button) v.findViewById(R.id.btn_dialog_test);
        mBtnDialogExit = (Button) v.findViewById(R.id.btn_dialog_exit);
        return v;
    }

    /**
     * Checks if the EditText is empty.
     *
     * @param editText
     * @return true if empty
     */
    private boolean isEditTextEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    /**
     * 连接路由器
     */
    public void connRouter(){
        int port = Integer.valueOf(mPortNumEdit.getText().toString());
        mSUI = new SessionUserInfo(mUserEdit.getText().toString().trim(), mHostEdit.getText()
                .toString().trim(),
                mPasswordEdit.getText().toString().trim(), port);
        SessionController.getSessionController().setUserInfo(mSUI);
        SessionController.getSessionController().connect(false);
        if(mListener != null)
            SessionController.getSessionController().setConnectionStatusListener(mListener);
        this.dismiss();
    }
    @Override
    public void onClick(View v) {
        if (v == mBtnDialogTest) {//点击开始测试
            if (isEditTextEmpty(mUserEdit) || isEditTextEmpty(mHostEdit)
                    || isEditTextEmpty(mPasswordEdit) || isEditTextEmpty(mPortNumEdit)) {
                Snackbar.make(mBtnDialogTest,"亲，信息输入不完整",Snackbar.LENGTH_SHORT).show();
                return;
            }
            connRouter();
        }else if (v == mBtnDialogExit) {//退出
            this.dismiss();
        }
    }
}


