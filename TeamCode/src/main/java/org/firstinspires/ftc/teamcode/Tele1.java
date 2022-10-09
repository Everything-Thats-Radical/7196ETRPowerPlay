package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp (name = "Tele 1.0", group = "Iterative Opmode")
public class Tele1 extends OpMode {

    //Declare OpMode members
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor FLDrive = null;
    private DcMotor FRDrive = null;
    private DcMotor BLDrive = null;
    private DcMotor BRDrive = null;
    private CRServo liftArm = null;
    private CRServo liftClaw = null;
    private DcMotor STRAIGHTUPPPP = null;


    @Override
    public void init() { //Code to run ONCE when the driver hits INIT

        telemetry.addData("Status", "Initializing");
        telemetry.update();

        FLDrive  = hardwareMap.get(DcMotor.class, "FLDrive");
        FRDrive = hardwareMap.get(DcMotor.class, "FRDrive");
        BLDrive  = hardwareMap.get(DcMotor.class, "BLDrive");
        BRDrive = hardwareMap.get(DcMotor.class, "BRDrive");
        liftArm = hardwareMap.get(CRServo.class, "liftArm");
        liftClaw = hardwareMap.get(CRServo.class, "liftClaw");
        STRAIGHTUPPPP = hardwareMap.get(DcMotor.class, "STRAIGHTUPPPP");

        FLDrive.setDirection(DcMotor.Direction.FORWARD);
        BLDrive.setDirection(DcMotor.Direction.FORWARD);
        FRDrive.setDirection(DcMotor.Direction.REVERSE);
        BRDrive.setDirection(DcMotor.Direction.REVERSE);
        liftArm.setDirection(CRServo.Direction.FORWARD);
        liftClaw.setDirection(CRServo.Direction.FORWARD);
        STRAIGHTUPPPP.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }


    @Override
    public void start() { //Code to run ONCE when the driver hits PLAY
        runtime.reset();
    }


    @Override
    public void loop() { //Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP

        // Retrieve lift values from controller

        boolean armRight = gamepad2.x;
        boolean armLeft = gamepad2.b;
        boolean clawOpen = gamepad2.y;
        boolean clawClosed = gamepad2.a;
        boolean liftUp = gamepad1.dpad_up;
        boolean liftDown = gamepad1.dpad_down;


        //Retrieve driving values from controller
        double y = gamepad1.left_stick_y * .8; // Remember, this is reversed!
        double x = -gamepad1.left_stick_x * .8; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x * .8;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when at least one is out
        // of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x - rx) / denominator;
        double backLeftPower = (y - x - rx) / denominator;
        double frontRightPower = (y - x + rx) / denominator;
        double backRightPower = (y + x + rx) / denominator;

        FLDrive.setPower(frontLeftPower);
        FRDrive.setPower(frontRightPower);
        BLDrive.setPower(backLeftPower);
        BRDrive.setPower(backRightPower);

        if (armRight && !armLeft) {
            liftArm.setPower(1);
        } else if (!armRight && armLeft){
            liftArm.setPower(-1);
        }else{
            liftArm.setPower(0);
        }


        if (clawOpen && !clawClosed) {
            liftClaw.setPower(1);
        } else if(!clawOpen && clawClosed){
            liftClaw.setPower(-1);
        } else {
            liftClaw.setPower(0);
        }

        if (clawOpen && !clawClosed) {
            liftClaw.setPower(1);
        } else if(!clawOpen && clawClosed){
            liftClaw.setPower(-1);
        } else {
            liftClaw.setPower(0);
        }

        if(liftUp && !liftDown){
            STRAIGHTUPPPP.setPower(.4);
        } else if(liftDown && !liftUp){
            STRAIGHTUPPPP.setPower(-.4);
        } else {
            STRAIGHTUPPPP.setPower(0);
        }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "Front Left (%.2f), Front Right (%.2f), Back Left (%.2f), " +
                        "Back Right (%.2f)", frontLeftPower, frontRightPower, backLeftPower,
                backRightPower);
        //telemetry.addData("Slides", "left (%.2f), right (%.2f)", x, y);
        telemetry.update();
    }

    @Override
    public void stop() { //Code to run ONCE after the driver hits STOP
        telemetry.addData("Status", "STOPPED");
        FLDrive.setPower(0);
        FRDrive.setPower(0);
        BLDrive.setPower(0);
        BRDrive.setPower(0);
        liftArm.setPower(0);
        liftClaw.setPower(0);
        STRAIGHTUPPPP.setPower(0);
    }
}