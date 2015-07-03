package com.anch.wxy_pc.imclient.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.utils.AnimTools;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by wxy-pc on 2015/6/11.
 */
@ContentView(R.layout.act_welcome)
public class WelcomeAct extends Activity {
    @ViewInject(R.id.wel_tv)
    private TextView welTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(WelcomeAct.this, LoginAct.class));
//                finish();
//            }
//        }, 1000);

        AnimTools.alphaAnim(welTv, 2000, true, 0, 1, new AnimTools.EndAnimation() {
            @Override
            public void endAnim() {
                startActivity(new Intent(WelcomeAct.this, LoginAct.class));
                finish();
            }
        });
    }
}
