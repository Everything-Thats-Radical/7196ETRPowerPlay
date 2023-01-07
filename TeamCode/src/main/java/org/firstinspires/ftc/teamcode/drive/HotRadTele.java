package org.firstinspires.ftc.teamcode.drive;

//import com.google.blocks.ftcrobotcontroller.runtime.BNO055IMUAccess; Had imported, but was giving error
// kept it in just in case, because I (Isaiah) am not sure it was me who wanted it here

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp (name = "HotRadTele", group = "Iterative Opmode")
public class HotRadTele extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        //Declare OpMode members
        ElapsedTime runtime = new ElapsedTime();
        DcMotor FLDrive = null;
        DcMotor FRDrive = null;
        DcMotor BLDrive = null;
        DcMotor BRDrive = null;
        CRServo clampyBoi = null;
        DcMotor STRAIGHTUPPPP = null;
        DistanceSensor junctionSensor = null;
        boolean autoDropCone = false;
        double desiredHeading = 0;

        telemetry.addData("Status", "Initializing");
        telemetry.update();

        FLDrive = hardwareMap.get(DcMotor.class, "FLDrive");
        FRDrive = hardwareMap.get(DcMotor.class, "FRDrive");
        BLDrive = hardwareMap.get(DcMotor.class, "BLDrive");
        BRDrive = hardwareMap.get(DcMotor.class, "BRDrive");
        clampyBoi = hardwareMap.get(CRServo.class, "clampyBoi");
        STRAIGHTUPPPP = hardwareMap.get(DcMotor.class, "STRAIGHTUPPPP");
        junctionSensor = hardwareMap.get(DistanceSensor.class, "junctionSensor");

        FLDrive.setDirection(DcMotor.Direction.REVERSE);
        BLDrive.setDirection(DcMotor.Direction.REVERSE);
        FRDrive.setDirection(DcMotor.Direction.REVERSE);
        BRDrive.setDirection(DcMotor.Direction.REVERSE);
        clampyBoi.setDirection(CRServo.Direction.FORWARD);
        STRAIGHTUPPPP.setDirection(DcMotor.Direction.REVERSE);

        STRAIGHTUPPPP.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // Retrieve the IMU from the hardware map
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        runtime.reset();

        waitForStart();

        if (isStopRequested()) return;
/*
    public double getAngle() {
        return 1.1;
    }
    @Override
    public void start() { //Code to run ONCE when the driver hits PLAY

    }
*/


        while (opModeIsActive()) { //Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP

            boolean clawOpen = gamepad2.y;
            boolean slowMode = gamepad1.right_bumper;
            boolean dropCheck = (0.2 < (gamepad2.right_trigger));
            double speedMultiplier;
            
            //Retrieve driving values from controller
            double y = gamepad1.left_stick_y * .8; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * .8; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x * .8;
            double STRAIGHTUPPPPPower = gamepad2.left_stick_y;

// START OF MAIN DRIVING CODE ---------------------------
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x - rx) / denominator;
            double backLeftPower = (y - x - rx) / denominator;
            double frontRightPower = (y - x + rx) / denominator;
            double backRightPower = (y + x + rx) / denominator;

            // set power to motors
            if (slowMode) {
                speedMultiplier = .3;
            } else {
                speedMultiplier = 1.0;
            }

            FLDrive.setPower(frontLeftPower * speedMultiplier);
            FRDrive.setPower(frontRightPower * speedMultiplier);
            BLDrive.setPower(backLeftPower * speedMultiplier);
            BRDrive.setPower(backRightPower * speedMultiplier);

// END OF MAIN DRIVING CODE --------------------------

// START OF MAIN SCORING CODE --------------------------
            if (dropCheck) {
                if ((junctionSensor.getDistance(DistanceUnit.INCH) < 5) && (junctionSensor.getDistance(DistanceUnit.INCH)) > 3.5) {
                    autoDropCone = true;
                } else {
                    autoDropCone = false;
                }
            }

            if (autoDropCone) {
                clampyBoi.setPower(1);
            } else if (clawOpen) {
                clampyBoi.setPower(1);
            } else {
                clampyBoi.setPower(0);
            }

            STRAIGHTUPPPP.setPower(-STRAIGHTUPPPPPower);
// START OF MAIN SCORING CODE -----------------------------
/*
        if(gamepad1.x){
            FLDrive.setPower(1);
        }else{
            FLDrive.setPower(0);
        }

        if(gamepad1.y){
            FRDrive.setPower(1);
        }else{
            FRDrive.setPower(0);
        }

        if(gamepad1.a){
            BLDrive.setPower(1);
        }else{
            BLDrive.setPower(0);
        }

        if(gamepad1.b){
            BRDrive.setPower(1);
        }else{
            BRDrive.setPower(0);
        }
*/
            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "Front Left (%.2f), Front Right (%.2f), Back Left (%.2f), " +
                            "Back Right (%.2f)", frontLeftPower, frontRightPower, backLeftPower,
                    backRightPower);
            telemetry.addData("Distance (in inches)", junctionSensor.getDistance(DistanceUnit.INCH));
            //telemetry.addData("Slides", "left (%.2f), right (%.2f)", x, y);

            telemetry.update();
        }
    }
}