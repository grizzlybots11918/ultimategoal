package org.firstinspires.ftc.teamcode.old;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "HDrive C (4 Rings)", group = "HDrive", preselectTeleOp="HDrive Teleop")
public class box3 extends PrimaryAuton {
    @Override
    public void initThings() {
        // 11 feet -> inches
        distanceL = 11.0*12.0;
        distanceR = distanceL;
    }
}
