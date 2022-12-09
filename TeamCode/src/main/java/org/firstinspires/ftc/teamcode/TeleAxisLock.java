package org.firstinspires.ftc.teamcode;

//import com.google.blocks.ftcrobotcontroller.runtime.BNO055IMUAccess; Had imported, but was giving error
// kept it in just in case, because I (Isaiah) am not sure it was me who wanted it here
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.lang.Math;


@TeleOp (name = "TeleAxisLock", group = "Iterative Opmode")
public class TeleAxisLock extends OpMode {

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
    private DistanceSensor dist;

    private double currentHeight = 0;
    // Grab is to get a cone, ground for Ground junction, short, mid, high for respective junctions.
    private double grabLvl = 0;
    private double groundLvl = 0.7;
    private double shortLvl = 14.5;
    private double midLvl = 24.5;
    //double highLvl = 34.5;




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
        STRAIGHTUPPPP.setDirection(DcMotor.Direction.REVERSE);
        spinnyBoi.setDirection(DcMotor.Direction.REVERSE);

        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;

        boolean clawClamp;

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

        boolean grab = gamepad2.dpad_down;
        boolean ground = gamepad2.dpad_left;
        boolean small = gamepad2.dpad_right;
        boolean mid = gamepad2.dpad_up;

        boolean slowMode = gamepad1.right_bumper;
        double speedMultiplier;

        double FC_YMagnitude; // field centric y magnitude
        double FC_XMagnitude;

        //Retrieve driving values from controller
        double y = gamepad1.left_stick_y * .8; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * .8; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x * .8;


        double STRAIGHTUPPPPPower = gamepad2.left_stick_y;
        double spinnyBoiPower = gamepad2.left_stick_x;

        //-----------------------------------------------------------------------------------------
        // START OF FIELD-CENTRIC DRIVING CODE: (Leaving this section in will make driving field-centric.
        // commenting it out will make driving standard mecanum drive. There is no need to touch
        // any other code than this to change that setting. It integrates automatically.

        BNO055IMU imu = null;

        double joystickHeading = Math.atan2(y, x); // get driver's desired heading in degrees from x and y of joystick
        // idea for atan2 found here https://www.reddit.com/r/FTC/comments/t02l65/field_centric_driving_mecanum/

        // Get robot heading
        double robotHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
        // above line of code taken from here https://stemrobotics.cs.pdx.edu/node/7265.html if problems occur

        // get direction we should power the robot based on joystick direction and robot heading
        double drivePowerHeading = joystickHeading - robotHeading;

        FC_YMagnitude = Math.sin(drivePowerHeading); // get y magnitude from desired angle
        FC_XMagnitude = Math.cos(drivePowerHeading); // get x magnitude from desired angle

        double joystickMagnitude = Math.sqrt((Math.pow(y, 2)) + Math.pow(x, 2)); // get magnitude from original joystick pushing

        y = FC_YMagnitude * joystickMagnitude; // scale y power by the original joystick magnitude
        x = FC_XMagnitude * joystickMagnitude; // scale x power by the original joystick magnitude
        //END OF FIELD-CENTRIC DRIVING CODE -----------------------------------------------------

        // Standard mecnaum driving code: (this block is used no matter what. The field=centric code)
        // can either be commented our or included.
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x - rx) / denominator;
        double backLeftPower = (y - x - rx) / denominator;
        double frontRightPower = (y - x + rx) / denominator;
        double backRightPower = (y + x + rx) / denominator;

        // end of standard mecanum code


        // set power to motors
        if(slowMode){
            speedMultiplier = .3;
            if((dist.getDistance(DistanceUnit.INCH) >= 3) && (dist.getDistance(DistanceUnit.INCH) <= 5)){
                clawControl("release");
                }
        }else{
            speedMultiplier = 1.0;
        }

        FLDrive.setPower(frontLeftPower * speedMultiplier);
        FRDrive.setPower(frontRightPower * speedMultiplier);
        BLDrive.setPower(backLeftPower * speedMultiplier);
        BRDrive.setPower(backRightPower * speedMultiplier);

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

        if(grab && !(ground) && !(small) && !(mid)){
            autoGoalHeight(grabLvl);
        } else if(!(grab) && ground && !(small) && !(mid)){
            autoGoalHeight(groundLvl);
        } else if(!(grab) && !(ground) && small && !(mid)){
            autoGoalHeight(shortLvl);
        } else if(!(grab) && !(ground) && !(small) && mid){
            autoGoalHeight(midLvl);
        } else {
            liftArm.setPower(0);
        }

        STRAIGHTUPPPP.setPower(-STRAIGHTUPPPPPower);

        spinnyBoi.setPower(spinnyBoiPower/4);


        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "Front Left (%.2f), Front Right (%.2f), Back Left (%.2f), " +
                        "Back Right (%.2f)", frontLeftPower, frontRightPower, backLeftPower,
                backRightPower);
        telemetry.addData("Distance: ", dist.getDistance(DistanceUnit.INCH));
        //telemetry.addData("Slides", "left (%.2f), right (%.2f)", x, y);
        telemetry.update();
    }

    public void autoGoalHeight(double wLevel){
        double needMove = 0;
        String d = "up";
        if(currentHeight >= wLevel){
            needMove = currentHeight - wLevel;
            d = "down";
        } else {
            needMove = currentHeight + wLevel;
            clawControl("clamp");
        }
        currentHeight += needMove;
        needMove = Math.abs(needMove);
        moveLift(d, needMove, .7);
    }

    public void moveLift(String direction, double height, double power){
        double ticks_per_inch = (1120 / (1.75 * 2 * Math.PI));
        // FIND ACTUAL TICKS PER INCH BEFORE RUNNING
        double ticksNeeded = height * ticks_per_inch;
        double initialPosition = STRAIGHTUPPPP.getCurrentPosition();
        double currentPosition = STRAIGHTUPPPP.getCurrentPosition();
        int directionSign = 1;
        double ticksMoved;

        if(direction.equals("up")){
            directionSign = 1;
        }else if(direction.equals("down")){
            directionSign = -1;
        }else{
            directionSign = -1;
        }

        ticksMoved = Math.abs(initialPosition - currentPosition);
        while (ticksNeeded > ticksMoved){
            currentPosition = STRAIGHTUPPPP.getCurrentPosition();
            ticksMoved = Math.abs(initialPosition - currentPosition);
            /*
            telemetry.addData("ticksNeeded ", ticksNeeded);
            telemetry.addData("ticksMoved ", ticksMoved);
            telemetry.addData("ticksNeeded - ticksMoved ", ticksNeeded - ticksMoved);
            telemetry.update();
             */
            STRAIGHTUPPPP.setPower(power * directionSign);
        }
        STRAIGHTUPPPP.setPower(0);
    }

    public void clawControl(String state){
        int directionSign = 1;

        if(state.equals("clamp")){
            directionSign = 1;
        }else if(state.equals("release")){
            directionSign = -1;
        }else{
            directionSign = -1;
        }
        liftClaw.setPower(directionSign);
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