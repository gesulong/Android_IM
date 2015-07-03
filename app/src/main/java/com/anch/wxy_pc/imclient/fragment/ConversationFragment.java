package com.anch.wxy_pc.imclient.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.act.ChatAct;
import com.anch.wxy_pc.imclient.adapter.ConversationAdapter;
import com.anch.wxy_pc.imclient.bean.ConversationBean;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.ConversationDbUtil;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * 会话Frg
 * Created by wxy-pc on 2015/6/12.
 */
public class ConversationFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String CHANGE_MES_RECEIVER = "com.anch.wxy_pc.imclient.receiver.CHANGE_MES_RECEIVER";
    @ViewInject(R.id.conversation_lv)
    private static ListView conversationLv;
    private static ConversationAdapter conversationAdapter;
    private static List<ConversationBean> conversationBeans;
    private static Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constanst.CURRENT_PAGE = Constanst.CURRENT_PAGE_VALUE;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        ViewUtils.inject(this, view);
        context = getActivity();
        notifyDataSetChanged();
        setListener();
        return view;
    }

    private static void notifyDataSetChanged() {
        conversationBeans = ConversationDbUtil.select(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        conversationAdapter = new ConversationAdapter(context, conversationBeans);
        conversationLv.setAdapter(conversationAdapter);
        conversationAdapter.notifyDataSetChanged();

    }

    private void setListener() {
        conversationLv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ChatAct.class);
        intent.putExtra("friendId", conversationBeans.get(position).getWho_id());
        intent.putExtra("friendName", conversationBeans.get(position).getWho_name());
        intent.putExtra("currentUser", SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        getActivity().startActivityForResult(intent, Constanst.RESULT_CODE);
    }

    public static class ChangeMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CHANGE_MES_RECEIVER)) {
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constanst.RESULT_CODE:
                notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Constanst.CURRENT_PAGE = 0;
    }
}
