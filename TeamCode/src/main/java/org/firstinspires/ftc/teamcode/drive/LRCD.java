package org.firstinspires.ftc.teamcode.drive;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.SwitchableLight;

@Autonomous(name = "LRCD")
public class LRCD extends LinearOpMode {
    // Declare OpMode members. (attributes of OP mode)
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor FLDrive = null;
    private DcMotor FRDrive = null;
    private DcMotor BLDrive = null;
    private DcMotor BRDrive = null;

    final double ticksPerInch = 1120;

    ColorSensor colorSensor;
    DistanceSensor distanceSensor;

    //private BNO055IMU imu;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        colorSensor = hardwareMap.get(ColorSensor.class, "sensor_color_distance");

        // get a reference to the distance sensor that shares the same name.
        distanceSensor = hardwareMap.get(DistanceSensor.class, "sensor_color_distance");
        // hsvValues is an array that will hold the hue, saturation, and value information.

        float hsvValues[] = {0F, 0F, 0F};
        // values is a reference to the hsvValues array.

        final float values[] = hsvValues;
        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attentuate the measured values.

        final double SCALE_FACTOR = 255;

        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)colorSensor).enableLight(true);
        }
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
            }
        });
        telemetry.update();

        // Set the panel back to the default color
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }
        });



        String position = "start";
        String coneColor = "red";


        waitForStart();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        FLDrive = hardwareMap.get(DcMotor.class, "FLDrive");
        FRDrive = hardwareMap.get(DcMotor.class, "FRDrive");
        BLDrive = hardwareMap.get(DcMotor.class, "BLDrive");
        BRDrive = hardwareMap.get(DcMotor.class, "BRDrive");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        FLDrive.setDirection(DcMotor.Direction.REVERSE);
        BLDrive.setDirection(DcMotor.Direction.REVERSE);
        FRDrive.setDirection(DcMotor.Direction.FORWARD);
        BRDrive.setDirection(DcMotor.Direction.FORWARD);

        FLDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BLDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FRDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BRDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start (driver presses PLAY)

        runtime.reset();

        //This is were we put the code we want

        //IMUTurn(360, .5, FLDrive, FRDrive, BLDrive, BRDrive);
        //sleep(1000);
        //IMUTurn(90, .5, FLDrive, FRDrive, BLDrive, BRDrive);
        //sleep(1000);
        //IMUTurn(-90, .5, FLDrive, FRDrive, BLDrive, BRDrive);
        //sleep(1000);

        //This section is if we are the robot closer to the storage unit

        Move(0, 0.3, 50, FLDrive, FRDrive, BLDrive, BRDrive, "Forward");

        int totalReds = colorSensor.red();
        int totalGreens = colorSensor.green();
        int totalBlues = colorSensor.blue();

        if (totalReds > totalGreens && totalReds > totalBlues){
            coneColor= "red";
        } else if(totalGreens > totalBlues && totalGreens > totalReds){
            coneColor= "green";
        } else if(totalBlues > totalGreens && totalBlues > totalReds){
            coneColor= "blue";
        }

        telemetry.addData("The cone is ", coneColor);
        telemetry.update();

        sleep(1000);
        if (coneColor.equals("red")) {
            telemetry.addData("Color: ", coneColor);
            telemetry.update();
            Move(-85, .5, 10,FLDrive, FRDrive, BLDrive, BRDrive, "Left");
            Move(30, 0.5, 30, FLDrive, FRDrive, BLDrive, BRDrive, "Forward");
        } else if (coneColor.equals("green")) {
            telemetry.addData("Color: ", coneColor);
            telemetry.update();
        } else if (coneColor.equals("blue")) {
            telemetry.addData("Color: ", coneColor);
            telemetry.update();
            Move(-85, .5,-10,  FLDrive, FRDrive, BLDrive, BRDrive, "Right");
            Move(30, 0.5, 30, FLDrive, FRDrive, BLDrive, BRDrive, "Forward");
        } else {
            String noColor = "Color not detected.";
            telemetry.addData("Color: ", noColor);
            telemetry.update();
        }

        //This part is if we are the robot nearest to the warehouse and the other team has working auto
            /*driveStraight(2, 0.5, FLDrive, FRDrive, BLDrive, BRDrive);
            sleep(5000);
            IMUTurn(-83.5, .5, FLDrive, FRDrive, BLDrive, BRDrive);
            driveStraight(60, 0.5, FLDrive, FRDrive, BLDrive, BRDrive);
            //Use a motor to return duck from carousel here
            sleep(1000)
            IMUTurn(83.5, .5, FLDrive, FRDrive, BLDrive, BRDrive);
            driveStraight(10, 0.5, FLDrive, FRDrive, BLDrive, BRDrive);
            driveStraight(-5, -0.5, FLDrive, FRDrive, BLDrive, BRDrive);
            IMUTurn(83.5, .5, FLDrive, FRDrive, BLDrive, BRDrive);
            driveStraight(50, 0.9, FLDrive, FRDrive, BLDrive, BRDrive);
             */
        //sleep(5000);
    }
/*
    public void initializeIMU() {
        this.imu = this.hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        this.imu.initialize(parameters);

        if(!this.imu.isGyroCalibrated()) {
            this.sleep(50);
        }
    }*/

    public void Move (double angle, double power, double dist, DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight, String dir){

        //double startAngle = this.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
        //double robotTurnedAngle = 0;

        //angle = angle * .9; //incrementation to account for drift

        //Constants: 90 degree turn with REV 40-1's:
        //Constants: 180 degree turn with REV 40-1's:

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        while(this.opModeIsActive()) {
            int BRTarget = backRight.getCurrentPosition() + (int)(dist * ticksPerInch);
            int BLTarget = backLeft.getCurrentPosition() + (int)(dist * ticksPerInch);
            int FRTarget = frontRight.getCurrentPosition() + (int)(dist * ticksPerInch);
            int FLTarget = frontLeft.getCurrentPosition() + (int)(dist * ticksPerInch);

            // set target position
            backRight.setTargetPosition(BRTarget);
            backLeft.setTargetPosition(BLTarget);
            frontRight.setTargetPosition(FRTarget);
            frontLeft.setTargetPosition(FLTarget);

            //switch to run to position mode
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            //run to position at the desiginated power
            if(dir.equals("Right")) {
                backRight.setPower(-power);
                backLeft.setPower(-power);
                frontLeft.setPower(power);
                frontRight.setPower(power);
            } else if (dir.equals("Left")) {
                backRight.setPower(power);
                backLeft.setPower(power);
                frontLeft.setPower(-power);
                frontRight.setPower(-power);
            } else if (dir.equals("Forward")){
                backRight.setPower(power);
                backLeft.setPower(power);
                frontLeft.setPower(power);
                frontRight.setPower(power);
            } else if(dir.equals("Backward")){
                backRight.setPower(-power);
                backLeft.setPower(-power);
                frontLeft.setPower(-power);
                frontRight.setPower(-power);
            }

            // wait until both motors are no longer busy running to position
            while (opModeIsActive() && (backLeft.isBusy() || backRight.isBusy() || frontLeft.isBusy() || frontRight.isBusy())) {
            }

            // set motor power back to 0
            backRight.setPower(0);
            backLeft.setPower(0);
            frontLeft.setPower(0);
            frontRight.setPower(0);
        }
    }
/*
    public void driveStraight(double inch, double power, DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight) {
        //encoder's resolution: 537.6 in
        //Going straight constant: 42.78
        final double ticksPerInch = 42.78;
        // final double ticksPerInch = 95.94  ; //ticks doubled because the rollers on the mechanam wheels apply some force sideways

        //Reset encoder positions
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        int tickNeeded = (int) (ticksPerInch * inch);

        int FlPosition;
        int FRPosition;
        int BLpositiom;
        int Brposition;

        //How many ticks the motor needs to move
        frontLeft.setTargetPosition(tickNeeded);
        frontRight.setTargetPosition(tickNeeded);
        backLeft.setTargetPosition(tickNeeded);
        backRight.setTargetPosition(tickNeeded);

        //Changes what information we send to the motors.
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Set the power value for each motor
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        FlPosition = frontLeft.getCurrentPosition();
        FRPosition = frontRight.getCurrentPosition();
        BLpositiom = backLeft.getCurrentPosition();
        Brposition = backRight.getCurrentPosition();

        while (this.opModeIsActive() && (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
            //We are just waiting until the motors reach the position (based on the ticks passed)
            telemetry.addData("FL Position", FlPosition);
            telemetry.addData("Fr Position", FRPosition);
            telemetry.addData("Bl Position", BLpositiom);
            telemetry.addData("Br Position", Brposition);
            telemetry.addData("Ticks Needed", tickNeeded);
            telemetry.update();
        }
        //When the motors have passed the required ticks, stop each motor
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    } */
}