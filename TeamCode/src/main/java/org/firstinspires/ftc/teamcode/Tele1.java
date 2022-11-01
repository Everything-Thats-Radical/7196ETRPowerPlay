package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import java.lang.Math;


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
    private DcMotor spinnyBoi = null;




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
        spinnyBoi = hardwareMap.get(DcMotor.class, "SpinnyBoi");

        FLDrive.setDirection(DcMotor.Direction.FORWARD);
        BLDrive.setDirection(DcMotor.Direction.FORWARD);
        FRDrive.setDirection(DcMotor.Direction.REVERSE);
        BRDrive.setDirection(DcMotor.Direction.REVERSE);
        liftArm.setDirection(CRServo.Direction.FORWARD);
        liftClaw.setDirection(CRServo.Direction.FORWARD);
        STRAIGHTUPPPP.setDirection(DcMotor.Direction.FORWARD);
        spinnyBoi.setDirection(DcMotor.Direction.FORWARD);

        BNO055IMU imu;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;

        telemetry.addData("Status", "Initialized");
        telemetry.update();


    }


    public double getAngle() {
        return 1.1;
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
        boolean liftUp = gamepad2.dpad_up;
        boolean liftDown = gamepad2.dpad_down;
        boolean spinRight = gamepad2.dpad_right;
        boolean spinLeft = gamepad2.dpad_left;



        //Retrieve driving values from controller
        double y = gamepad1.left_stick_y * .8; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * .8; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x * .8;

        // field-centric driving code: (USE FIELD-CENTRIC OR STANDARD. NOT BOTH.)
        double joystickHeading = Math.atan2(y, x); // get heading in degrees from x and y of joystick
        //double drivePowerHeading = joystickHeading -

        //end of field-centric code

        // standard mecnaum driving code: (USE FIELD-CENTRIC OR STANDARD. NOT BOTH.)
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x - rx) / denominator;
        double backLeftPower = (y - x - rx) / denominator;
        double frontRightPower = (y - x + rx) / denominator;
        double backRightPower = (y + x + rx) / denominator;
        // end of standard mecanum code


        // set power to motors
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
            STRAIGHTUPPPP.setPower(1);
        } else if(liftDown && !liftUp){
            STRAIGHTUPPPP.setPower(-1);
        } else {
            STRAIGHTUPPPP.setPower(0);
        }

        if(spinRight && !spinLeft){
            spinnyBoi.setPower(.2);
        } else if(spinLeft && !spinRight){
            spinnyBoi.setPower(-.2);
        } else {
            spinnyBoi.setPower(0);
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
        spinnyBoi.setPower(0);
    }
}