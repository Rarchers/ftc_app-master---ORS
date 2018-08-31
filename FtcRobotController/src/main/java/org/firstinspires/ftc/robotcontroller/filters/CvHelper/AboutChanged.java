package org.firstinspires.ftc.robotcontroller.filters.CvHelper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.qualcomm.ftcrobotcontroller.R;

import org.firstinspires.ftc.robotcontroller.filters.NoneFilter;
import org.firstinspires.ftc.robotcontroller.internal.Vision;


public class AboutChanged extends Activity {

    private RadioGroup changeCore;
    private Switch cameraSwitch;
    private RadioGroup visionSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_changed);
        if (!Driver.getInstance().check(4)) {
            Driver.getInstance().change(4,"1");
            Toast.makeText(getApplicationContext(), NoneFilter.Decrypt(NoneFilter.UI_Thread4,Short.MAX_VALUE)+ Driver.getInstance().completion(),
                    Toast.LENGTH_LONG).show();
        }
        changeCore =(RadioGroup)findViewById(R.id.ChangeCore);
        switch (Vision.getInstance().core) {
            case 1:
                changeCore.check(R.id.CoreByAR);
                break;
            case 2:
                changeCore.check(R.id.CoreForLight);
                break;
        }
        changeCore.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.CoreByAR) {
                    Vision.getInstance().core = 1;
                } else if (i == R.id.CoreForLight) {
                    Vision.getInstance().core = 2;
                    Toast.makeText(getApplicationContext(), "此核心已被移除", Toast.LENGTH_SHORT).show();
                    changeCore.check(R.id.CoreByAR);
                }
            }
        });
        cameraSwitch = (Switch)findViewById(R.id.CameraSwitch);
        cameraSwitch.setChecked(Vision.getInstance().ftcRobotControllerActivity.objectTrackingView.isActivated());
        cameraSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Vision.getInstance().cameraStart();
                } else {
                    Vision.getInstance().cameraStop();
                }
            }
        });
        visionSwitch = (RadioGroup)findViewById(R.id.VisionControl);
        switch (Vision.getInstance().getNowTarget()) {
            case 0:
                visionSwitch.check(R.id.VisionStop);
                break;
            case 1:
                visionSwitch.check(R.id.VisionTarget1);
                break;
            case 2:
                visionSwitch.check(R.id.VisionTarget2);
                break;
            case 3:
                visionSwitch.check(R.id.VisionTarget3);
                break;
        }
        visionSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.VisionStop) {
                    Vision.getInstance().visionStop();
                } else if (i == R.id.VisionTarget1) {
                    Vision.getInstance().setNowTarget(1);
                } else if (i == R.id.VisionTarget2) {
                    Vision.getInstance().setNowTarget(2);
                }else if (i == R.id.VisionTarget3) {
                    Vision.getInstance().setNowTarget(3);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "更改已保存", Toast.LENGTH_SHORT).show();
    }
}
