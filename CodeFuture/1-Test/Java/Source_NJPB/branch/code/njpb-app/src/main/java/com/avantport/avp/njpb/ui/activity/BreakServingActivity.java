package com.avantport.avp.njpb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avantport.avp.njpb.R;
import com.avantport.avp.njpb.uitls.ToastUtil;

public class BreakServingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mHead_goback;
    private TextView mHead_title;
    private ImageView mHead_qrcode;
    private LinearLayout mBreak_station;
    private LinearLayout mBreak_bike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_serving);

        mHead_goback = (ImageView) findViewById(R.id.head_goback);//返回键
        mHead_title = (TextView) findViewById(R.id.head_title);//头布局的标题
        mHead_qrcode = (ImageView) findViewById(R.id.head_qrcode);//二维码
        mBreak_station = (LinearLayout) findViewById(R.id.break_station);
        mBreak_bike = (LinearLayout) findViewById(R.id.break_bike);
        mHead_qrcode.setVisibility(View.INVISIBLE);

        mHead_goback.setOnClickListener(this);
        mBreak_station.setOnClickListener(this);
        mBreak_bike.setOnClickListener(this);
        mHead_title.setText("报修方式的选择");
    }

    @Override
    public void onClick(View v) {//TODO、、点击没有设置完全
        //处理点击事件
        switch (v.getId()) {
            case R.id.head_goback:
                //返回键，销毁当前界面
                finish();
                break;
            case R.id.break_station:
                //跳转站点维修界面
                ToastUtil.show(this,"站点维修");
                startActivity(new Intent(this,StationServingActivity.class));
                break;
            case R.id.break_bike:
                //跳转车辆维修界面
                ToastUtil.show(this,"车辆维修");
                startActivity(new Intent(this,BikeServingActivity.class));
                break;
        }


    }
}
