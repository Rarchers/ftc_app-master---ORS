package org.firstinspires.ftc.teamcode;

import android.widget.Switch;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.robotcontroller.internal.Direction;
import org.firstinspires.ftc.robotcontroller.internal.Vision;

/**
 * Created by Rarcher on 2018/8/24.
 */

@TeleOp(name = "OpenCV_Rarcher&Summerlights",group = "Demo")
public class ORS extends OpMode{
    Vision vision = Vision.getInstance();//获取Vision类 ，这个类中打包了OpenCV调用的所有方法以及识别物体的方法
    ORS_RobotMap robot;
    @Override
    public void init() {
        robot.init(hardwareMap);
        vision.cameraStart();//开启视频预览
        vision.setNewTarget(1,R.drawable.icon_menu);//设置一个新的目标物体图，第一个参数为设置的多张照片范围1，2，3 第二个参数为具体的图片id
        //接上，第二个参数的图必须清晰且无其他物体干扰，不能有背景，所以最好使用透明背景图
        vision.setNowTarget(2);//在这个函数里，可以修改当前搜索目标的图片
        vision.cameraStop();//这个函数关闭视觉预览，但并没有关闭识别算法
        vision.setBlind(15);//修改盲区，此方法修改忽略距离，即中心点为坐标原点左右被忽略的像素点距离，被忽略的区域会被当做CENTER值
        vision.killTarget(2);//取消目标，即不对某一目标检测，注意放在初始化区域，不能在运行中kill，因为是动态添加的目标，我们提供了三个空余目标，不使用的目标就kill
        //接上：例如，我们提供了三个目标，但本场比赛我只需要识别两个目标，那么就用setNewTarget方法添加两个目标，然后用此方法kill第三个目标即killTarget（3）;当运行中
        //接上：需要调整当前目标，使用setNowTarget函数来切换当前识别目标
    }

    @Override
    public void loop() {
        Direction direction =  vision.getDirection();//此函数返回几个数值，分别为UNKNOWN,LEFT,RIGHT,CENTER,用于判断当前目标距离中心的方向
        switch (direction){
            //根据不同的返回来控制机器人的行走
            case LEFT:
                robot.leftMotor.setPower(-1);
                robot.rightMotor.setPower(1);
                break;
            case RIGHT:
                robot.leftMotor.setPower(1);
                robot.rightMotor.setPower(-1);
                break;
            case CENTER:
                robot.leftMotor.setPower(0);
                robot.rightMotor.setPower(0);
                break;
        }
    }
}
