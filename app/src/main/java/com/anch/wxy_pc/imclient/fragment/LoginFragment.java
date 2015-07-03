package com.anch.wxy_pc.imclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.act.HomeAct;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.xmpp.XmppCallBack;

/**
 * 登陆界面
 *
 * @author wxy-pc
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText userNameTv, userPassTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        userNameTv = (EditText) view.findViewById(R.id.user_name_tv);
        userPassTv = (EditText) view.findViewById(R.id.user_pass_tv);
        userNameTv.setText(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        userPassTv.setText(SharePrefUtils.getString(Constanst.PASSWORD, ""));
        view.findViewById(R.id.login_btn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if (userNameTv.length() == 0) {
                    Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userPassTv.length() == 0) {
                    Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //登陆
                login(userNameTv.getText().toString().trim(), userPassTv.getText().toString().trim());
                break;
        }
    }

    private void login(final String userName, final String userPass) {
        XmppManager.xmppManager.connectOpenfireSer(getActivity(), userName, userPass, new XmppCallBack(getActivity()) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess() {

                SharePrefUtils.saveString(Constanst.ACCOUNT, userName);
                SharePrefUtils.saveString(Constanst.PASSWORD, userPass);

                startActivity(new Intent(getActivity(), HomeAct.class));
                getActivity().finish();
                super.onSuccess();
            }

            @Override
            public void onFail(int result) {
                super.onFail(result);
            }
        });
    }
}
