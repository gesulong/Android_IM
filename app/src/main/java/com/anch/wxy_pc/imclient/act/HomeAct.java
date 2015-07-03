package com.anch.wxy_pc.imclient.act;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.fragment.ContactsFragment;
import com.anch.wxy_pc.imclient.fragment.ConversationFragment;
import com.anch.wxy_pc.imclient.service.OnlineService;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.ContactsDbUtil;
import com.anch.wxy_pc.imclient.utils.DateUtils;
import com.anch.wxy_pc.imclient.utils.IntentUtils;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * Created by wxy-pc on 2015/6/12.
 */
@ContentView(R.layout.act_home)
public class HomeAct extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    @ViewInject(R.id.pager)
    private ViewPager pager;
    @ViewInject(R.id.title_name_tv)
    private TextView currentUserTv;
    @ViewInject(R.id.conversationTv)
    private TextView conversationTv;//会话
    @ViewInject(R.id.contactsTv)
    private TextView contactsTv;//联系人
    @ViewInject(R.id.conversationTab)
    private TextView conversationTab;//会话Tab
    @ViewInject(R.id.contactsTab)
    private TextView contactsTab;//联系人Tab
    @ViewInject(R.id.right_btn)
    private ImageButton addFriendIb;

    private List<Fragment> list = new ArrayList<>();
    private List<TextView> tabList = new ArrayList<>();

    private ConversationFragment conversationFragment;
    private ContactsFragment contactsFragment;
    private PopupWindow mPopupWindow;
    private View view;
    public static HomeAct homeAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        homeAct = this;
        addData();
        setAdapter();
        setListener();
    }

    private void setAdapter() {
        pager.setAdapter(new MFramPagerAdt(getSupportFragmentManager()));
    }

    private void addData() {
        conversationFragment = new ConversationFragment();
        contactsFragment = new ContactsFragment();
        list.add(conversationFragment);
        list.add(contactsFragment);

        tabList.add(conversationTab);
        tabList.add(contactsTab);

        currentUserTv.setText(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        currentUserTv.setTextColor(Color.YELLOW);
    }

    private void setListener() {
        pager.setOnPageChangeListener(this);
        conversationTv.setOnClickListener(this);
        contactsTv.setOnClickListener(this);
        addFriendIb.setOnClickListener(this);
    }

    private void setTab(int index) {

        for (int i = 0; i < tabList.size(); i++) {
            if (i == index)
                tabList.get(index).setVisibility(TextView.VISIBLE);
            else
                tabList.get(i).setVisibility(TextView.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        conversationFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.conversationTv:
                pager.setCurrentItem(0, true);
                break;
            case R.id.contactsTv:
                pager.setCurrentItem(1, true);
                break;
            case R.id.right_btn://添加好友
                IntentUtils.skip(HomeAct.this, AddFriendAct.class, Constanst.SKIP_ADD_FRIENT_PASS_USER, SharePrefUtils.getString(Constanst.ACCOUNT, ""), false);
                break;
            case R.id.logout_btn://注销账户
                IntentUtils.skip(HomeAct.this, LoginAct.class, true);
                XmppManager.xmppManager.exitLogin();//退出登录
                break;
            case R.id.exit_btn://退出账户
                finish();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MFramPagerAdt extends FragmentStatePagerAdapter {

        public MFramPagerAdt(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmppManager.xmppManager.exitLogin();//退出登录
        ContactsDbUtil.deleteAll();
    }

    @Override
    public void onBackPressed() {
//        if (DateUtils.getCurrTime() - exitTime > 2000) {
//            Toast.makeText(HomeAct.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//            exitTime = DateUtils.getCurrTime();
//            return;
//        }
//        super.onBackPressed();
        if (view == null) {
            view = LayoutInflater.from(this).inflate(R.layout.act_logout, null);
            view.findViewById(R.id.logout_btn).setOnClickListener(this);
            view.findViewById(R.id.exit_btn).setOnClickListener(this);
        }

        showPop(view);
    }

    /**
     * PopupWindow 弹出详情介绍
     *
     * @param view
     */
    public void showPop(View view) {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(view, UtilTools.getScreenWidth(getApplicationContext()),
                    UtilTools.getScreenHeight(getApplicationContext()) / 2);
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.mipmap.logout_bg));// 点击空白时popupwindow关闭,设置背景图片，不能在布局中设置，要通过代码来设置
        mPopupWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功
        mPopupWindow.setAnimationStyle(R.style.ExitAnim);// 设置窗口显示的动画效果
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        mPopupWindow.update();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

}
