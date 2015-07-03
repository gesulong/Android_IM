package com.anch.wxy_pc.imclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.act.ChatAct;
import com.anch.wxy_pc.imclient.bean.ContactsBean;
import com.anch.wxy_pc.imclient.fragment.ContactsFragment;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.BitmapUtils;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.DialogUtil;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;

import java.util.List;

/**
 * Created by wxy-pc on 2015/6/15.
 */
public class ContactsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<ContactsBean> contactsBeans;

    public ContactsAdapter(Context context, List<ContactsBean> contactsBeans) {
        this.mContext = context;
        this.contactsBeans = contactsBeans;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (contactsBeans == null) return 0;
        return contactsBeans.size();
    }

    @Override
    public Object getItem(int position) {
        if (contactsBeans == null) return null;
        return contactsBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ContactsHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_contacts_item, parent, false);
            holder = new ContactsHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ContactsHolder) convertView.getTag();
        }

        if (contactsBeans != null) {
            holder.contactsAccountTv.setText(UtilTools.splitStr(contactsBeans.get(position).getAccount(), 0));
            holder.contactsHeadIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.default_left_head), 130));
            holder.contactsOnlineInfoTv.setText("["+contactsBeans.get(position).getOnlineState()+"]");
            //长按开始删除
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogUtil.simpleAlertDialog(mContext, "您确定要删除好友？", "删除好友", "放弃", "确定", R.mipmap.head, true, true, new DialogUtil.OnClickCallBack() {

                        @Override
                        public void leftOnclick() {

                        }

                        @Override
                        public void rightOnclick() {
                            new AsyncTask<Void, Void, Boolean>() {
                                @Override
                                protected void onPreExecute() {
                                    DialogUtil.showDialog((Activity) mContext, inflater.inflate(R.layout.dialog_pro, null), "删除好友中", R.style.ProgressDialog, false);
                                }

                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    XMPPConnection connection = XmppManager.xmppManager.createXmppConnect();
                                    if (XmppManager.xmppManager.removeUser(connection, contactsBeans.get(position).getAccount(), SharePrefUtils.getString(Constanst.ACCOUNT, ""))) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }

                                @Override
                                protected void onPostExecute(Boolean aBoolean) {
                                    DialogUtil.cancleDialog();
                                    if (aBoolean) {
                                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                                        ContactsFragment.notifyAdapter();
                                    } else {
                                        Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }.execute();
                        }
                    });
                    return true;
                }
            });
            //点击开始聊天
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ChatAct.class);
                    intent.putExtra("friendId", contactsBeans.get(position).getAccount());
                    intent.putExtra("friendName", contactsBeans.get(position).getAccount());
                    intent.putExtra("currentUser", SharePrefUtils.getString(Constanst.ACCOUNT, ""));
                    ((Activity) mContext).startActivityForResult(intent, Constanst.RESULT_CODE);
                }
            });
        }
        return convertView;
    }

    private class ContactsHolder {
        @ViewInject(R.id.contacts_account_tv)
        TextView contactsAccountTv;
        @ViewInject(R.id.contacts_online_info_tv)
        TextView contactsOnlineInfoTv;
        @ViewInject(R.id.contacts_head_iv)
        ImageView contactsHeadIv;
    }
}
