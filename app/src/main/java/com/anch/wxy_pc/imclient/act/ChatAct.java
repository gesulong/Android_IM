package com.anch.wxy_pc.imclient.act;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.adapter.ChatAdapter;
import com.anch.wxy_pc.imclient.bean.ConversationBean;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.ConversationDbUtil;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

/**
 * Created by wxy-pc on 2015/6/16.
 */
@ContentView(R.layout.act_chat)
public class ChatAct extends Activity implements View.OnClickListener {
    public static final String UPDATE_MES_RECEIVER = "com.anch.wxy_pc.imclient.act.UPDATE_MES_RECEIVER";
    @ViewInject(R.id.who_username_tv)
    private TextView whoUserNameTv;
    @ViewInject(R.id.chat_lv)
    private static ListView chatLv;
    @ViewInject(R.id.edit_msg_et)
    private EditText editMsgEt;
    private static ChatAdapter chatAdapter;
    private static String friendId, friendName, currentUser;
    private static ChatAct chatAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        Constanst.CHAT_PAGE = Constanst.CHAT_PAGE_VALUE;
        chatAct = this;
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        getData();
        notifyDataSetChanged();
        setData();
    }

    private static void notifyDataSetChanged() {
        chatAdapter = new ChatAdapter(chatAct, ConversationDbUtil.selectMssage(friendId, currentUser));
        chatLv.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            friendId = intent.getStringExtra("friendId");
            friendName = intent.getStringExtra("friendName");
            currentUser = intent.getStringExtra("currentUser");
        }
    }

    private void setData() {
        whoUserNameTv.setText(UtilTools.splitStr(friendId, 0));
    }

    @OnClick(value = {R.id.send_btn, R.id.chat_title_back_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                /***TODO******************/
//                Toast.makeText(this, "服务未连接,发送不出去的", Toast.LENGTH_SHORT).show();
                String bodyStr = editMsgEt.getText().toString();
                if (!TextUtils.isEmpty(bodyStr)) {
                    XmppManager.xmppManager.sendMessage(XmppManager.xmppManager.createXmppConnect(),friendId, friendName, bodyStr, "chat");
                }
                editMsgEt.getText().clear();
                chatLv.setSelection(chatLv.getCount() - 1);
                break;
            case R.id.chat_title_back_btn:
                setResult(Constanst.RESULT_CODE);
                finish();
                break;
        }
    }

    public static class UpdateMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UPDATE_MES_RECEIVER)) {
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Constanst.RESULT_CODE);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constanst.CHAT_PAGE = 0;
    }
}
