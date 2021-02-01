package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.TempClaw;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.drivebase.DifferentialDrive;
import com.arcrobotics.ftclib.util.Timing.Timer;

@Autonomous(name="HDrive: Move Fwd for 10 secs", group="HDrive", preselectTeleOp="HDrive Teleop")
public class HDrivePark extends LinearOpMode {
    // this is incredibly lazy and i'm going to work on a better autonomous before meet 2.
    // this should allow us to park which is definitely better than nothing, but definitely not great either

    @Override
    public void runOpMode() throws InterruptedException {
        long raiseClawSecs = 0.5;
        long mvFwdSecs = 10.0;
        long totalSecs = raiseClawSecs+mvFwdSecs;

        long currentTime = 0;
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
        Timer time = new Timer(totalSecs);

        double turn = 0.0;
        double drive, left, right, max, strafe, angleSpeed;
        Motor leftMotor = new Motor(hardwareMap, "Left_Motor", Motor.GoBILDA.NONE);
        Motor rightMotor = new Motor(hardwareMap, "Right_Motor", Motor.GoBILDA.NONE);

        Servo grabber = hardwareMap.get(Servo.class, "serve");
        Motor claw_angle = new Motor(hardwareMap, "claw", Motor.GoBILDA.NONE);

        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule module : allHubs) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        TempClaw claw = new TempClaw(grabber, claw_angle);
        Motor[] motors = {leftMotor, rightMotor};
        // i would use FTCLib's HDrive drivebase but it was being weird with strafing for some reason.
        DifferentialDrive dt = new DifferentialDrive(true, motors);

        telemetry.addData("Status", "Ready To Run");
        telemetry.addData("1. Raise Claw (secs)", raiseClawSecs);
        telemetry.addData("2. Move Bot Forward (secs)", mvFwdSecs);
        telemetry.update();

        waitForStart();
        time.start();
        while (opModeIsActive()) {
            for (LynxModule module : allHubs) {
                module.clearBulkCache();
            }

            currentTime = time.currentTime();
            
            if (!time.isDone()) {
                // raises claw first so it doesn't drag on the ground
                if (currentTime <= raiseClawSecs && currentTime > 0) {
                    claw.angleSpeed(0.5);
                } else {
                    claw.angleSpeed(0.0);
                }

                // moves robot forward for x amount of secs after raising claw
                if (time.currentTime() <= mvFwdSecs && time.currentTime() >= raiseClawSecs) {
                    drive = 1;
                } else {
                    drive = 0;
                }

                dt.arcadeDrive(drive, turn);
            }
            
            telemetry.addData("Remaining Time on Timer", totalSecs-currentTime);
            telemetry.addData("1. Raise Claw (secs)", raiseClawSecs);
            telemetry.addData("2. Move Bot Forward (secs)", mvFwdSecs);
            telemetry.update();

        }
    }
}