package com.anch.wxy_pc.imclient.act;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.custom.NoTouchViewPager;
import com.anch.wxy_pc.imclient.service.OnlineService;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.BitmapUtils;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.DateUtils;
import com.anch.wxy_pc.imclient.utils.DialogUtil;
import com.anch.wxy_pc.imclient.utils.IntentUtils;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.xmpp.XmppCallBack;
import com.anch.wxy_pc.imclient.xmpp.XmppConstant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy-pc on 2015/6/11.
 */
@ContentView(R.layout.act_login)
public class LoginAct extends FragmentActivity implements View.OnClickListener {

    @ViewInject(R.id.login_pager)
    private NoTouchViewPager pager;

    //登陆控件
    private EditText userNameTv;
    private EditText userPassTv;
    private ImageView loginHeadIv;
    private Button newUserBtn;
    private Button loginBtn;

    //注册控件
    private EditText signUserNameTv;
    private EditText signUserPassTv;
    private Button signBtn;

    //创建昵称控件
    private EditText createNickNameTv;
    private Button nextBtn;

    //上传头像控件
    private ImageView upHeadIv;
    private Button createFinishBtn;

    private AccountManager accountManager;
    private List<View> views = new ArrayList<>();
    private LayoutInflater mInflater;
    private View loginView;
    private View signView;
    private View createNickView;
    private View upHeadView;
    private int index = 0;
    private XMPPConnection connection;
    private String accountName, accountPassWord, accountNickName;
    // 创建一个以当前时间为名称的文件
    private File tempFile = new File(Environment.getExternalStorageDirectory(), DateUtils.getPhotoFileName());
    private byte[] avatarByte;
    private boolean regState = false;//注册是否成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        mInflater = LayoutInflater.from(this);
//        禁止触摸滑动
        pager.setNoScroll(true);
        loginInit();
        signInit();
        createNickInit();
        upHeadInit();
        addViews();
        pager.setAdapter(new LoginPagerAdapter());
        setCurrentItem(index);
//        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new LoginFragment()).commit();
    }

    //登陆View初始化
    private void loginInit() {
        loginView = mInflater.inflate(R.layout.fragment_login, null);
        userNameTv = (EditText) loginView.findViewById(R.id.user_name_tv);
        userPassTv = (EditText) loginView.findViewById(R.id.user_pass_tv);
        loginHeadIv = (ImageView) loginView.findViewById(R.id.login_head_iv);
        newUserBtn = (Button) loginView.findViewById(R.id.new_user_btn);
        loginBtn = (Button) loginView.findViewById(R.id.login_btn);
        userNameTv.setText(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        userPassTv.setText(SharePrefUtils.getString(Constanst.PASSWORD, ""));
        newUserBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        loginHeadIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.head), 150));

    }

    //注册View初始化
    private void signInit() {
        signView = mInflater.inflate(R.layout.fragment_sign, null);
        signUserNameTv = (EditText) signView.findViewById(R.id.sign_user_name_tv);
        signUserPassTv = (EditText) signView.findViewById(R.id.sign_user_pass_tv);
        signBtn = (Button) signView.findViewById(R.id.sign_btn);
        signBtn.setOnClickListener(this);
    }

    //创建昵称View初始化
    private void createNickInit() {
        createNickView = mInflater.inflate(R.layout.fragment_nick_name, null);
        createNickNameTv = (EditText) createNickView.findViewById(R.id.create_nick_name_tv);
        nextBtn = (Button) createNickView.findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this);
    }

    //上传头像View初始化
    private void upHeadInit() {
        upHeadView = mInflater.inflate(R.layout.fragment_head_up, null);
        upHeadIv = (ImageView) upHeadView.findViewById(R.id.up_head_iv);
        createFinishBtn = (Button) upHeadView.findViewById(R.id.create_finish_btn);
        upHeadIv.setOnClickListener(this);
        createFinishBtn.setOnClickListener(this);
    }

    private void addViews() {
        views.add(loginView);
        views.add(signView);
        views.add(createNickView);
        views.add(upHeadView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_user_btn://登陆
                setCurrentItem(1);
                break;
            case R.id.login_btn://登陆
                if (userNameTv.length() == 0) {
                    Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userPassTv.length() == 0) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //登陆
                login(userNameTv.getText().toString().trim(), userPassTv.getText().toString().trim());
                /***TODO******************/
//                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
//                IntentUtils.skip(LoginAct.this, HomeAct.class, true);
                break;

            case R.id.sign_btn://注册创建
                if (signUserNameTv.length() == 0) {
                    Toast.makeText(this, "注册用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (signUserPassTv.length() == 0) {
                    Toast.makeText(this, "注册密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //注册
                sign(signUserNameTv.getText().toString().trim(), signUserPassTv.getText().toString().trim());
                /***TODO******************/
//                setCurrentItem(2);
//                signUserNameTv.setText("");
//                signUserPassTv.setText("");
//                Toast.makeText(LoginAct.this, "注册成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.next_btn://创建下一步
                accountNickName = createNickNameTv.getText().toString().trim();
                if (createNickNameTv.length() == 0) {
                    Toast.makeText(this, "创建昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                setCurrentItem(3);
                createNickNameTv.setText("");
                break;

            case R.id.up_head_iv://上传头像点击
                DialogUtil.simpleAlertDialog(LoginAct.this, "上传头像", "上传头像", "拍照", "相册", R.mipmap.head, true, false, new DialogUtil.OnClickCallBack() {
                    @Override
                    public void leftOnclick() {
                        IntentUtils.skipCamera(LoginAct.this, Uri.fromFile(tempFile), Constanst.TASK_SKIP_CAMERA);
                    }

                    @Override
                    public void rightOnclick() {
                        IntentUtils.skipPhoto(LoginAct.this, Constanst.TASK_SKIP_PHOTO);
                    }
                });
                break;

            case R.id.create_finish_btn://完成注册
                //开始注册创建
                upHead();
                break;
        }
    }

    private void setCurrentItem(int pos) {
        index = pos;
        pager.setCurrentItem(index, true);
    }

    //登陆方法
    private void login(final String userName, final String userPass) {
        XmppManager.xmppManager.connectOpenfireSer(LoginAct.this, userName, userPass, new XmppCallBack(LoginAct.this) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess() {

                SharePrefUtils.saveString(Constanst.ACCOUNT, userName);
                SharePrefUtils.saveString(Constanst.PASSWORD, userPass);

                startActivity(new Intent(LoginAct.this, HomeAct.class));
                LoginAct.this.finish();
                super.onSuccess();
            }

            @Override
            public void onFail(int result) {
                super.onFail(result);
            }
        });
    }

    //注册用户名和密码
    private void sign(String newUserName, String newUserPass) {
        new AsyncTask<String, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                DialogUtil.showDialog(LoginAct.this, mInflater.inflate(R.layout.dialog_pro, null), "注册中", R.style.ProgressDialog, false);
            }

            @Override
            protected Integer doInBackground(String... params) {
                connection = XmppManager.xmppManager.createXmppConnect();
                try {
                    connection.connect();
                    Registration registration = new Registration();
                    registration.setType(IQ.Type.SET);
                    registration.setTo(connection.getServiceName());
                    registration.addAttribute("username", params[0]);
                    registration.addAttribute("password", params[1]);
                    accountName = params[0];
                    accountPassWord = params[1];
//                registration.setUsername(params[0]);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
//                registration.setPassword(params[1]);
                    registration.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
                    PacketFilter filter = new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class));
                    PacketCollector collector = connection.createPacketCollector(filter);
                    connection.sendPacket(registration);// 向服务器端，发送注册Packet包，注意其中Registration是Packet的子类
                    IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
                    collector.cancel();//停止请求result
                    if (result == null) return XmppConstant.XMPP_REG_NO_RESPONSE;
                    else if (result.getType() == IQ.Type.RESULT)
                        return XmppConstant.XMPP_REG_SUCCESS;
                    else if (result.getError().toString().equalsIgnoreCase("conflict(409)"))
                        return XmppConstant.XMPP_REG_ACCOUNT_EXIST;
                    else return XmppConstant.XMPP_REG_FAILD;
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                return XmppConstant.XMPP_REG_FAILD;
            }

            @Override
            protected void onPostExecute(Integer result) {
                DialogUtil.cancleDialog();
                switch (result) {
                    case XmppConstant.XMPP_REG_NO_RESPONSE:
                        regState = false;
                        Toast.makeText(LoginAct.this, "服务器可能发呆了吧", Toast.LENGTH_SHORT).show();
                        break;
                    case XmppConstant.XMPP_REG_SUCCESS:
                        regState = true;
                        setCurrentItem(2);
                        signUserNameTv.setText("");
                        signUserPassTv.setText("");
                        try {
                            connection.login(accountName, accountPassWord, XmppConstant.XMPP_RESOURCE);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(LoginAct.this, "注册成功", Toast.LENGTH_SHORT).show();
                        break;
                    case XmppConstant.XMPP_REG_ACCOUNT_EXIST:
                        regState = false;
                        Toast.makeText(LoginAct.this, "账户已存在", Toast.LENGTH_SHORT).show();
                        break;
                    case XmppConstant.XMPP_REG_FAILD:
                        regState = false;
                        Toast.makeText(LoginAct.this, "注册失败了呀！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }.execute(newUserName, newUserPass);
    }

    /**
     * 上传头像
     */
    private void upHead() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                DialogUtil.showDialog(LoginAct.this, mInflater.inflate(R.layout.dialog_pro, null), "正在上传头像", R.style.ProgressDialog, false);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    VCard vCard = new VCard();
                    vCard.load(connection);
                    vCard.setNickName(accountNickName);
                    String encodedImage = StringUtils.encodeBase64(avatarByte);
                    vCard.setAvatar(avatarByte, encodedImage);
                    vCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage
                            + "</BINVAL>", true);
                    vCard.save(connection);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                DialogUtil.cancleDialog();

                if (aBoolean) {
                    Toast.makeText(LoginAct.this, "注册成功了呀！可用该账号登陆了！", Toast.LENGTH_SHORT).show();
                    setCurrentItem(0);
                    XmppManager.xmppManager.exitLogin();
                } else {
                    Toast.makeText(LoginAct.this, "注册失败了！", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    //ViewPager适配器
    private class LoginPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }
    }

    @Override
    public void onBackPressed() {
        switch (index) {
            case 0:
                finish();
                break;
            default:
                DialogUtil.simpleAlertDialog(LoginAct.this, "您想取消注册新用户了吗？", "放弃注册", "放弃", "继续", R.mipmap.head, true, true, new DialogUtil.OnClickCallBack() {
                    @Override
                    public void leftOnclick() {
                        setCurrentItem(0);
                        upHeadIv.setImageResource(R.mipmap.ic_launcher);
                        if (connection != null && regState)
                            try {//删除账户
                                connection.getAccountManager().deleteAccount();
                                regState = false;
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                    }

                    @Override
                    public void rightOnclick() {

                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constanst.TASK_SKIP_CAMERA://跳转相机裁剪图片
                IntentUtils.cropImageUri(LoginAct.this, Uri.fromFile(tempFile), Constanst.TASK_CUT_PHOTO);
                break;
            case Constanst.TASK_CUT_PHOTO://裁剪图片后设置图片
                if (data != null)
                    setPicToView(data);
                break;
            case Constanst.TASK_SKIP_PHOTO://跳转相册裁剪图片
                if (data != null)
                    IntentUtils.cropImageUri(LoginAct.this, data.getData(), Constanst.TASK_CUT_PHOTO);
                break;
        }
    }

    //将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            upHeadIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(photo, 130));
            avatarByte = BitmapUtils.Bitmap2Bytes(photo);
        }
    }
}
