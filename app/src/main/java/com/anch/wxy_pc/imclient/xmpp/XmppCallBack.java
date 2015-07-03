package com.anch.wxy_pc.imclient.xmpp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.utils.DialogUtil;

/**
 * Created by wxy-pc on 2015/6/12.
 */
public class XmppCallBack implements XmppInterface {

    private Activity mContext;

    public XmppCallBack(Activity context) {
        this.mContext = context;
    }

    @Override
    public void onStart() {
        DialogUtil.showDialog(mContext, LayoutInflater.from(mContext).inflate(R.layout.dialog_pro, null), "登陆中", R.style.ProgressDialog, false);
    }

    @Override
    public void onSuccess() {
        DialogUtil.cancleDialog();
        Toast.makeText(mContext, "登陆成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(int result) {
        DialogUtil.cancleDialog();
        switch (result) {
            case XmppConstant.XMPP_CON_SER_FAILD:
                Toast.makeText(mContext, "连接失败,请检查网络", Toast.LENGTH_SHORT).show();
                break;
            case XmppConstant.XMPP_CON_SER_TIMEOUT:
                Toast.makeText(mContext, "连接超时", Toast.LENGTH_SHORT).show();
                break;
            case XmppConstant.XMPP_LOGIN_FAILD:
                Toast.makeText(mContext, "登陆失败,请检查账号或密码", Toast.LENGTH_SHORT).show();
                break;
            case XmppConstant.XMPP_LOGIN_TMOUT:
                Toast.makeText(mContext, "登陆超时,请检查网络", Toast.LENGTH_SHORT).show();
                break;
            case XmppConstant.XMPP_CON_SER_EXIT:
                Toast.makeText(mContext, "退出登录成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
