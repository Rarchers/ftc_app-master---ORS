package org.firstinspires.ftc.robotcontroller.Core3.DriveBase.listener;

/**
 * Created by kongqingwei on 2017/5/15.
 * OnOpenCVLoadListener
 */

public interface OnOpenCVLoadListener {

    // OpenCV加载成功
    void onOpenCVLoadSuccess();

    // OpenCV加载失败
    void onOpenCVLoadFail();

    // 没有安装OpenCVManager
    void onNotInstallOpenCVManager();
}
