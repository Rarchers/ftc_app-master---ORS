package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Rarcher on 2018/8/24.
 */

public class ORS_RobotMap {
    DcMotor leftMotor;
    DcMotor rightMotor;

    HardwareMap hw = null;

    public ORS_RobotMap(){
    }
    public void init(HardwareMap hardwareMap)
    {
        hw = hardwareMap;
        leftMotor = hw.dcMotor.get("left");
        rightMotor = hw.dcMotor.get("right");
    }
}
