package org.firstinspires.ftc.teamcode;

//import com.google.blocks.ftcrobotcontroller.runtime.BNO055IMUAccess; Had imported, but was giving error
// kept it in just in case, because I (Isaiah) am not sure it was me who wanted it here

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp (name = "TeleMiniBot", group = "Iterative Opmode")
public class TeleMiniBot extends LinearOpMode{

    //Declare OpMode members
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor FLDrive = null;
    private DcMotor FRDrive = null;
    private DcMotor BLDrive = null;
    private DcMotor BRDrive = null;

    @Override
    public void runOpMode() throws InterruptedException { //Code to run ONCE when the driver hits INIT


        telemetry.addData("Status", "Initializing");
        telemetry.update();

        FLDrive = hardwareMap.get(DcMotor.class, "FLDrive");
        FRDrive = hardwareMap.get(DcMotor.class, "FRDrive");
        BLDrive = hardwareMap.get(DcMotor.class, "BLDrive");
        BRDrive = hardwareMap.get(DcMotor.class, "BRDrive");

        FLDrive.setDirection(DcMotor.Direction.FORWARD);
        BLDrive.setDirection(DcMotor.Direction.FORWARD);
        FRDrive.setDirection(DcMotor.Direction.REVERSE);
        BRDrive.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Retrieve the IMU from the hardware map
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);


        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            //Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP

            // Retrieve lift values from controller

            boolean armRight = gamepad2.x;
            boolean armLeft = gamepad2.b;
            boolean clawOpen = gamepad2.y;
            boolean clawClosed = gamepad2.a;
            boolean slowMode = gamepad1.right_bumper;
            double speedMultiplier;

            double FC_YMagnitude; // field centric y magnitude
            double FC_XMagnitude;

            //Retrieve driving values from controller
            double y = gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;

            // Read inverse IMU heading, as the IMU heading is CW positive
            double botHeading = -imu.getAngularOrientation().firstAngle;

            double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
            double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio, but only when
            // at least one is out of the range [-1, 1]


            double STRAIGHTUPPPPPower = gamepad2.left_stick_y;
            double spinnyBoiPower = gamepad2.left_stick_x;

            // Standard mecnaum driving code: (this block is used no matter what. The field=centric code)
            // can either be commented our or included.
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x - rx) / denominator;
            double backLeftPower = (y - x - rx) / denominator;
            double frontRightPower = (y - x + rx) / denominator;
            double backRightPower = (y + x + rx) / denominator;

            // end of standard mecanum code


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

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "Front Left (%.2f), Front Right (%.2f), Back Left (%.2f), " +
                            "Back Right (%.2f)", frontLeftPower, frontRightPower, backLeftPower,
                    backRightPower);
            //telemetry.addData("Slides", "left (%.2f), right (%.2f)", x, y);
            telemetry.update();
        }
    }
}