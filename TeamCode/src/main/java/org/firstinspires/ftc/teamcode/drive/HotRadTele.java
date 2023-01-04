package org.firstinspires.ftc.teamcode.drive;

//import com.google.blocks.ftcrobotcontroller.runtime.BNO055IMUAccess; Had imported, but was giving error
// kept it in just in case, because I (Isaiah) am not sure it was me who wanted it here

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp (name = "HotRadTele", group = "Iterative Opmode")
public class HotRadTele extends OpMode {

    //Declare OpMode members
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor FLDrive = null;
    private DcMotor FRDrive = null;
    private DcMotor BLDrive = null;
    private DcMotor BRDrive = null;
    private CRServo clampyBoi = null;
    private DcMotor STRAIGHTUPPPP = null;
    private DistanceSensor junctionSensor = null;
    private boolean autoDropCone = false;




    @Override
    public void init() { //Code to run ONCE when the driver hits INIT


        telemetry.addData("Status", "Initializing");
        telemetry.update();

        FLDrive  = hardwareMap.get(DcMotor.class, "FLDrive");
        FRDrive = hardwareMap.get(DcMotor.class, "FRDrive");
        BLDrive  = hardwareMap.get(DcMotor.class, "BLDrive");
        BRDrive = hardwareMap.get(DcMotor.class, "BRDrive");
        clampyBoi = hardwareMap.get(CRServo.class, "clampyBoi");
        STRAIGHTUPPPP = hardwareMap.get(DcMotor.class, "STRAIGHTUPPPP");
        junctionSensor = hardwareMap.get(DistanceSensor.class, "junctionSensor");

        FLDrive.setDirection(DcMotor.Direction.FORWARD);
        BLDrive.setDirection(DcMotor.Direction.FORWARD);
        FRDrive.setDirection(DcMotor.Direction.REVERSE);
        BRDrive.setDirection(DcMotor.Direction.REVERSE);
        clampyBoi.setDirection(CRServo.Direction.FORWARD);
        STRAIGHTUPPPP.setDirection(DcMotor.Direction.REVERSE);

        STRAIGHTUPPPP.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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

        boolean clawOpen = gamepad2.y;
        boolean clawClosed = gamepad2.a;
        boolean slowMode = gamepad1.right_bumper;
        double speedMultiplier;

        //Retrieve driving values from controller
        double y = gamepad1.left_stick_y * .8; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * .8; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x * .8;
        double STRAIGHTUPPPPPower = gamepad2.left_stick_y;


        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x - rx) / denominator;
        double backLeftPower = (y - x - rx) / denominator;
        double frontRightPower = (y - x + rx) / denominator;
        double backRightPower = (y + x + rx) / denominator;

        // end of standard mecanum code


        // set power to motors
        if(slowMode){
            speedMultiplier = .3;
            if(junctionSensor.getDistance(DistanceUnit.INCH) > 5){
                autoDropCone = false;
            }
            else {
                autoDropCone = true;
            }
        }else{
            speedMultiplier = 1.0;
        }



        FLDrive.setPower(frontLeftPower * speedMultiplier);
        FRDrive.setPower(frontRightPower * speedMultiplier);
        BLDrive.setPower(backLeftPower * speedMultiplier);
        BRDrive.setPower(backRightPower * speedMultiplier);

        if(autoDropCone){
            clampyBoi.setPower(1);
        }
        else if (clawOpen && !clawClosed) {
            clampyBoi.setPower(1);
        } else {
            clampyBoi.setPower(0);
        }

        STRAIGHTUPPPP.setPower(-STRAIGHTUPPPPPower);


        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "Front Left (%.2f), Front Right (%.2f), Back Left (%.2f), " +
                        "Back Right (%.2f)", frontLeftPower, frontRightPower, backLeftPower,
                backRightPower);
        telemetry.addData("Distance (in inches)", junctionSensor.getDistance(DistanceUnit.INCH));
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
        clampyBoi.setPower(0);
        STRAIGHTUPPPP.setPower(0);
    }
}