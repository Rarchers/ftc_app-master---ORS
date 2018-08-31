package org.firstinspires.ftc.robotcontroller.internal;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.R;

import org.opencv.core.Mat;

public class Vision {
    //单例设计
    private static Vision vision;

    public static Vision getInstance() {
        if (vision == null) {
            vision = new Vision();
        }
        return vision;
    }

    //私有化构造方法
    private Vision() {

    }

    public FtcRobotControllerActivity ftcRobotControllerActivity;//FtcRC的Activity
    public int cameraViewWeigh;
    public int cameraViewHeight;//摄像头帧宽高
    public int targetX;
    public int targetY;//目标点坐标（摄像头中心为原点）
    public int x1;
    public int y1;
    public int x2;
    public int y2;//坐标原始值
    public int target1 = R.drawable.starry_night;//默认
    public int target2 = R.drawable.self_portrait;
    public int target3 = -1;//识别目标
    private int blind = 30;//盲区
    public boolean find = false;//找到目标
    public int core = 1;//视觉核心

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //获取摄像头获取帧的大小
    public void setCameraViewSize(int cameraViewWeigh, int cameraViewHeight) {
        Log.d("ORS","Get");
        Log.d("ORS","Core3  w"+ftcRobotControllerActivity.objectTrackingView.getWidth()+"  h"+ftcRobotControllerActivity.objectTrackingView.getHeight());
        Log.d("ORS","Core1  w"+cameraViewWeigh+"  h"+cameraViewWeigh);
        this.cameraViewWeigh = cameraViewWeigh;
        this.cameraViewHeight = cameraViewHeight;
    }

    //获取目标坐标
    public void setTargetFromFilter(Mat sceneCorners) {
        double x = 0;
        double y = 0;
        double x_max = sceneCorners.get(0, 0)[0];
        double x_min = sceneCorners.get(0, 0)[0];
        double y_max = sceneCorners.get(0, 0)[1];
        double y_min = sceneCorners.get(0, 0)[1];
        for (int i = 0; i <= 3; i++) {
            final double thisX = sceneCorners.get(i, 0)[0];
            final double thisY = sceneCorners.get(i, 0)[1];
            x = x + thisX;
            y = y + thisY;
            x_max = Math.max(x_max, thisX);
            x_min = Math.min(x_min, thisX);
            y_max = Math.max(y_max, thisY);
            y_min = Math.min(y_min, thisY);
        }//累计边缘四个点的横纵坐标之和，处理目标宽高
        x1 = (int)x_min;
        y1 = (int)y_min;
        x2 = (int)(x_max - x_min);
        y2 = (int)(y_max - y_min);
    }

    //读取最终坐标
    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    //获取方向，0为中心
    public Direction getDirection() {
        if (find) {
            if (Math.abs(getTargetX()) <= blind) {
                return Direction.CENTER;
            } else {
                if (getTargetX() < 0) {
                    return Direction.LEFT;
                } else {
                    return Direction.RIGHT;
                }
            }
        } else return Direction.UNKNOWN;
    }

    //控制摄像头预览开关
    public void cameraStart() {
        ftcRobotControllerActivity.objectTrackingView.enableView();
    }

    public void cameraStop() {
        ftcRobotControllerActivity.objectTrackingView.disableView();
    }

    //控制视觉系统开关
    public void visionStart() {
        ftcRobotControllerActivity.aSwitch.post(new Runnable() {
            @Override
            public void run() {
                ftcRobotControllerActivity.aSwitch.setChecked(true);
            }
        });
    }

    public void visionStop() {
        ftcRobotControllerActivity.aSwitch.post(new Runnable() {
            @Override
            public void run() {
                ftcRobotControllerActivity.aSwitch.setChecked(false);
                ftcRobotControllerActivity.objectTrackingView.mImageDetectionFilterIndex = 0;
            }
        });
    }

    //获取和修改正在检测的目标
    public void setNowTarget(int target) {
        visionStart();
        if (target != -1)
            ftcRobotControllerActivity.objectTrackingView.mImageDetectionFilterIndex = target;
    }

    public int getNowTarget() {
        return ftcRobotControllerActivity.objectTrackingView.mImageDetectionFilterIndex;
    }

    //获取、修改和清除目标
    public void setNewTarget(int target, int referenceImageResourceID) {
        switch (target) {
            case 1:
                target1 = referenceImageResourceID;
                break;
            case 2:
                target2 = referenceImageResourceID;
                break;
            case 3:
                target3 = referenceImageResourceID;
                break;
        }
        ftcRobotControllerActivity.objectTrackingView.setTarget(target1, target2, target3);
    }

    public int getTarget(int target) {
        switch (target) {
            case 1:
                return target1;
            case 2:
                return target2;
            case 3:
                return target3;
            default:
                return -1;
        }
    }

    public void killTarget(int target) {
        setNewTarget(target, -1);
    }

    //获取与修改盲区
    public void setBlind(int blind) {
        this.blind = blind;
    }

    public int getBlind() {
        return this.blind;
    }
}
