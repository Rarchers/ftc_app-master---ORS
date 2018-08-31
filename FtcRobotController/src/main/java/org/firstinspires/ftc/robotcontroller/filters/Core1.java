package org.firstinspires.ftc.robotcontroller.filters;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.Core3.DriveBase.BaseCameraView;
import org.firstinspires.ftc.robotcontroller.Core3.DriveBase.ObjectTracker;
import org.firstinspires.ftc.robotcontroller.Core3.DriveBase.listener.OnCalcBackProjectListener;
import org.firstinspires.ftc.robotcontroller.Core3.DriveBase.listener.OnObjectTrackingListener;
import org.firstinspires.ftc.robotcontroller.filters.CvHelper.Driver;
import org.firstinspires.ftc.robotcontroller.filters.ar.ImageDetectionFilter;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcontroller.internal.Vision;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

@Deprecated
public class Core1 implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "RCActivity";

    private Filter[] mImageDetectionFilters;
    public int mImageDetectionFilterIndex;
    public CameraBridgeViewBase mCameraView;
    public FtcRobotControllerActivity ftcRobotControllerActivity;
    public int times = 0;
    public int x = 0;
    public int y = 0;
    public int lastIndex = 1;  //最后一次使用的非零编号

    public Core1(View CV) {
        mCameraView = (CameraBridgeViewBase)CV;
        mCameraView.setVisibility(SurfaceView.VISIBLE);
        mCameraView.setCvCameraViewListener(this);
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        ftcRobotControllerActivity.objectTrackingView.enableView();
        mCameraView.disableView();
        ftcRobotControllerActivity.returnText("Core1");
        final Mat rgba = inputFrame.rgba();
        Log.d("ORS", "Core1");
        if (mImageDetectionFilterIndex == 0) {
            times = 0;
            x = 0;
            y = 0;
        } else {//停止扫描
            mImageDetectionFilters[mImageDetectionFilterIndex].apply(rgba, rgba);
            x = Vision.getInstance().getTargetX();
            y = Vision.getInstance().getTargetY();
            times++;
        }//启动扫描
        Log.d("ORS","x:"+x+"  y:"+y);
        if (mImageDetectionFilterIndex != -1 && mImageDetectionFilterIndex != 0)
            lastIndex = mImageDetectionFilterIndex;//记录每次使用的编号
        if (Vision.getInstance().find) {
            mImageDetectionFilterIndex = 0;
            Log.d("ORS","Find");
            new Thread() {
                public void run() {
                    super.run();
                    sleepMS(100);
//                    ftcRobotControllerActivity.objectTrackingView.setTarget(x, y);
//                    Log.d("ORS","Core3 setTarget");
                    ftcRobotControllerActivity.objectTrackingView.enableView();
                    Log.d("ORS","Core3 ON");
                }
                private void sleepMS(long ms) {
                    try {
                        Thread.sleep(ms);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            mCameraView.disableView();
            Log.d("ORS","Core1 OFF");
        }
        return rgba;
    }

    private void sleepMS(long ms) {
        try {
            Thread.sleep(ms);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.d(TAG,"Core1开始");
        setTarget(Vision.getInstance().target1, Vision.getInstance().target2, Vision.getInstance().target3);
    }

    @Override
    public void onCameraViewStopped() {

    }

    public void setTarget(int id1, int id2, int id3) {
        Filter target1 = getFilter(id1);
        Filter target2 = getFilter(id2);
        Filter target3 = getFilter(id3);
        mImageDetectionFilters = new Filter[]{
                new NoneFilter(),
                target1,
                target2,
                target3
        };
    }//设置目标

    private Filter getFilter(int id) {
        if (id == 0) {
            id = R.drawable.ic_launcher;//目标为0用FTC标志替换
        }
        if (id == -1) {
            id = R.drawable.ic_launcher;//目标禁用，用FTC标志替换
        }
        try {
            return new ImageDetectionFilter(ftcRobotControllerActivity.getApplicationContext(), id);
        } catch (IOException e) {
            e.printStackTrace();//会显示在DS和RC屏幕上
            //一旦id失败（一般来讲id是-1,0,1,2,3以外的数，或者文件缺失丢失），则以FTC标志替换以防止空指针
            try {
                return new ImageDetectionFilter(ftcRobotControllerActivity.getApplicationContext(), R.drawable.ic_launcher);
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;//两次失败，自求多福
            }
        }
    }//用于setTarget()获取Filter

}
