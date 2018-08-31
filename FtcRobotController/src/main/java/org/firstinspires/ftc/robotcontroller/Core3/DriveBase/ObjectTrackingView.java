package org.firstinspires.ftc.robotcontroller.Core3.DriveBase;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.qualcomm.ftcrobotcontroller.R;

import org.firstinspires.ftc.robotcontroller.Core3.DriveBase.listener.OnCalcBackProjectListener;
import org.firstinspires.ftc.robotcontroller.Core3.DriveBase.listener.OnObjectTrackingListener;

import org.firstinspires.ftc.robotcontroller.filters.Filter;
import org.firstinspires.ftc.robotcontroller.filters.NoneFilter;
import org.firstinspires.ftc.robotcontroller.filters.ar.ImageDetectionFilter;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcontroller.internal.Vision;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class ObjectTrackingView extends BaseCameraView implements OnCalcBackProjectListener {

    private static final String TAG = "ORS";
    private static final Scalar TRACKING_RECT_COLOR = new Scalar(255, 255, 0, 255);

    public FtcRobotControllerActivity ftcRobotControllerActivity;
    public boolean isCore1ON = true;

    private ObjectTracker objectTracker;// CamShift 目标追踪器
    private Rect mTrackWindow;// 追踪目标区域
    public boolean isTracking = false;// 追踪状态

    public ObjectTrackingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private double mCameraArea;

    private Filter[] mImageDetectionFilters;
    public int mImageDetectionFilterIndex = 0;
    public int times = 0;
    public int x = 0;
    public int y = 0;
    public int lastIndex = 1;  //最后一次使用的非零编号

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (ftcRobotControllerActivity.aSwitch.isChecked()) {
            if (isCore1ON) {
                core1();
            } else {
                core3();
            }
            mImageDetectionFilterIndex = lastIndex;
        } else {
            mImageDetectionFilterIndex = 0;
            ftcRobotControllerActivity.returnText("Core_OFF");
        }

        return mRgba;
    }

    //<Core1
    private void core1() {
        Log.d("ORS", "Core1");
        ftcRobotControllerActivity.returnText("Core1"+"   Index"+mImageDetectionFilterIndex);
        if (mImageDetectionFilterIndex == 0) {
            times = 0;
            x = 0;
            y = 0;//停止扫描
        } else {
            mImageDetectionFilters[mImageDetectionFilterIndex].apply(mRgba, mRgba);
            x = Vision.getInstance().getTargetX();
            y = Vision.getInstance().getTargetY();
            times++;//启动扫描
        }
        Log.d("ORS","x:"+x+"  y:"+y);
        if (mImageDetectionFilterIndex != -1 && mImageDetectionFilterIndex != 0)
            lastIndex = mImageDetectionFilterIndex;//记录每次使用的编号
        if (Vision.getInstance().find) {
            mImageDetectionFilterIndex = 0;
            isCore1ON = false;
            Log.d("ORS","即将设置目标");
            core3SetTarget();
            Log.d("ORS","设置目标完成");
        }
    }//核心
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
    //</Core1>

    //<Core3
    private void core3() {
        Log.d("ORS", "Core3");
        ftcRobotControllerActivity.returnText("Core3");
        if (isTracking && null != mTrackWindow) {
            if (0 == mCameraArea) {
                mCameraArea = mGray.size().area();
            }
            RotatedRect rotatedRect = objectTracker.objectTracking(mRgba);
            Rect rect = rotatedRect.boundingRect();
            double area = rect.area();
            if (1 < area && mCameraArea > area) {
                // 检测到有效的目标位置
                Imgproc.ellipse(mRgba, rotatedRect, TRACKING_RECT_COLOR, 3);
                Imgproc.rectangle(mRgba, rect.tl(), rect.br(), TRACKING_RECT_COLOR, 3);
                Vision.getInstance().targetX = (int)(rect.br().y + rect.tl().y)/2 - mRgba.height()/2;
                Vision.getInstance().targetY = (int)(rect.br().x + rect.tl().x)/2 - mRgba.width()/2;//View经过了旋转，因此X和Y需要换向
                if (null != mOnObjectTrackingListener) {
                    Point center = rotatedRect.center;
                    mOnObjectTrackingListener.onObjectLocation(center);
                }
            } else {
                // 目标跟丢
                Log.i(TAG, "onCameraFrame: 目标丢失");
                isTracking = false;
                mTrackWindow = null;
                Vision.getInstance().find = false;
                if (null != mOnObjectTrackingListener) {
                    mOnObjectTrackingListener.onObjectLost();
                }
                isCore1ON = true;
                restart();
            }
        } else {
            isTracking = false;
            isCore1ON = true;
        }
    }//核心
    public void core3SetTarget() {
        objectTracker = ObjectTracker.init();
        double rateW = mRgba.cols() / Vision.getInstance().cameraViewWeigh;
        double rateH = mRgba.rows() / Vision.getInstance().cameraViewHeight;
        int x1 = (int)(Vision.getInstance().x1 * rateW);
        int y1 = (int)(Vision.getInstance().y1 * rateH);
        int x2 = (int)(Vision.getInstance().x2 * rateW);
        int y2 = (int)(Vision.getInstance().y2 * rateH);
        x1 = Math.max(0, x1);
        y1 = Math.max(0, y1);
        x2 = Math.min(mRgba.cols(),x2);
        y2 = Math.min(mRgba.rows(),y2);
        Log.d("ORS","x1："+x1+"    y1："+y1+"    x2："+x2+"    y2："+y2);
        // 获取跟踪目标
        mTrackWindow = new Rect(x1, y1, x2, y2);
        // 创建跟踪目标
        Log.d("ORS", "core3SetTarget: "+mTrackWindow);
        objectTracker.createTrackedObject(mRgba, mTrackWindow);
        isTracking = true;
    }//设置目标
    //</Core3>

    public void restart() {
        disableView();
        enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        super.onCameraViewStarted(width, height);
        setTarget(Vision.getInstance().target1, Vision.getInstance().target2, Vision.getInstance().target3);
    }

    @Override
    public void onCameraViewStopped() {
        super.onCameraViewStopped();
    }

    public void setmTrackWindow(Rect mTrackWindow) {
        this.mTrackWindow = mTrackWindow;
    }

    private OnCalcBackProjectListener mOnCalcBackProjectListener;

    public void setOnCalcBackProjectListener(OnCalcBackProjectListener listener) {
        mOnCalcBackProjectListener = listener;
    }

    @Override
    public void onCalcBackProject(Mat backProject) {
        if (null != mOnCalcBackProjectListener) {
            mOnCalcBackProjectListener.onCalcBackProject(backProject);
        }
    }

    private OnObjectTrackingListener mOnObjectTrackingListener;

    public void setOnObjectTrackingListener(OnObjectTrackingListener listener) {
        mOnObjectTrackingListener = listener;
    }

    @Override
    public void onManagerConnected(int status) {

    }

    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {

    }
}
