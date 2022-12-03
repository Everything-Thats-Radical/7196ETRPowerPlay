package org.firstinspires.ftc.teamcode.drive;

import static java.lang.Math.signum;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

/* Our 3rd in command, Troy Tomson, has made an amazing piece of art that is this code.
Although janky, we accept it as beautiful, brave, and stunning.
 */

@Autonomous(name = "A2_CD_Score_Park")
public class A2_CD_Score_Park extends LinearOpMode {

    // create motor and servo objects
    private CRServo clampyBoi = null;
    private DcMotor STRAIGHTUPPPP = null;
    private DcMotor spinnyBoi = null;

    //final double ticks_per_inch = (1120 / (2.952 * 2 * Math.PI));

    ColorSensor colorSensor;
    DistanceSensor distanceSensor;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void runOpMode() {

        clampyBoi = hardwareMap.get(CRServo.class, "liftClaw");
        STRAIGHTUPPPP = hardwareMap.get(DcMotor.class, "STRAIGHTUPPPP");
        spinnyBoi = hardwareMap.get(DcMotor.class, "SpinnyBoi");

        clampyBoi.setDirection(CRServo.Direction.FORWARD);
        STRAIGHTUPPPP.setDirection(DcMotor.Direction.REVERSE);
        spinnyBoi.setDirection(DcMotor.Direction.REVERSE);


        spinnyBoi.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        STRAIGHTUPPPP.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // get a reference to the color sensor.
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

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)colorSensor).enableLight(true);
        }

        clampyBoi.setPower(1);

        Pose2d startPose = new Pose2d(0, 0, 0);
        Pose2d scanPose = new Pose2d(0, -25, 0);
        drive.setPoseEstimate(startPose);

        // send the info back to driver station using telemetry function.

        // change the background color to match the color detected by the RGB sensor.
        // pass a reference to the hue, saturation, and value array as an argument
        // to the HSVToColor method.

        // TRAJECTORY SEQUENCES BUILT
        //----------------------------------
        TrajectorySequence initialDriveForScan = drive.trajectorySequenceBuilder(new Pose2d())
                .forward(26,
                        SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        TrajectorySequence closer = drive.trajectorySequenceBuilder(initialDriveForScan.end())
                .forward(11,
                    SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                    SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        TrajectorySequence scoreAlign = drive.trajectorySequenceBuilder(closer.end())
                .back(7,
                    SampleMecanumDrive.getVelocityConstraint(7, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                    SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(3.14/2)
                .forward(5.7,
                        SampleMecanumDrive.getVelocityConstraint(7, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(-3.14/2)
                .forward(7,
                        SampleMecanumDrive.getVelocityConstraint(7, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();


        TrajectorySequence redZone = drive.trajectorySequenceBuilder(scoreAlign.end())
                .back(7,
                        SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(3.14/2, 4, 10)
                .forward(19,
                        SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(-3.2/2)
                .forward(11,
                        SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        TrajectorySequence blueZone = drive.trajectorySequenceBuilder(scoreAlign.end())
                .back(7,
                        SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(-3.14/2, 4, 10)
                .forward(27,
                        SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(3.14/2)
                .forward(11,
                        SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        TrajectorySequence coneGround = drive.trajectorySequenceBuilder(scoreAlign.end())
                .forward(2,
                        SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        waitForStart();

        if (!isStopRequested()) {
            clawControl("clamp");
            drive.followTrajectorySequence(initialDriveForScan);
        }

        sleep(1000);
        telemetry.addData("Starting turn ", "heck yeah" );
        telemetry.update();
        /*
        clawControl("clamp");
        moveLift("up", 27, 1);
        rotateSuzan("left", 90, .1);
        clawControl("");
        */

        telemetry.addData("Turn done ", "heck yeah");
        telemetry.update();


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
        String coneColor = "green";

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

        /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
         * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
         * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
         * for an explanation of HSV color. */



        if (coneColor.equals("red")) {
            telemetry.addData("Color: ", coneColor);
            telemetry.update();
            drive.followTrajectorySequence(closer);
            sleep(1500);
            prepCone();
            drive.followTrajectorySequence(scoreAlign);
            scoreCone();
            drive.followTrajectorySequence(redZone);
            liftReset();

        } else if (coneColor.equals("green")) {
            telemetry.addData("Color: ", coneColor);
            telemetry.update();
            drive.followTrajectorySequence(closer);
            sleep(1500);
            //rotateSuzan("right", 90, .2);
            prepCone();
            drive.followTrajectorySequence(scoreAlign);
            scoreCone();
            liftReset();
            drive.followTrajectorySequence(coneGround);

        } else if (coneColor.equals("blue")) {
            telemetry.addData("Color: ", coneColor);
            telemetry.update();
            drive.followTrajectorySequence(closer);
            sleep(1500);
            prepCone();
            drive.followTrajectorySequence(scoreAlign);
            scoreCone();
            drive.followTrajectorySequence(blueZone);
            liftReset();

        } else {
            String noColor = "Color not detected.";
            telemetry.addData("Color: ", noColor);
            telemetry.update();
        }

    }
/*
    public void lift(String direction, int power){

    }

    public void clampPower(boolean closed){

    }
*/
    public void rotateSuzan(String direction, double degrees, double power){
        double ticksNeeded = (degrees/360) * 1120;
        double initialPosition = spinnyBoi.getCurrentPosition();
        double currentPosition = 0;
        int directionSign;
        double ticksMoved;

        if(direction.equals("right")){
            directionSign = 1;
        }else if(direction.equals("left")){
            directionSign = -1;
        }else{
            directionSign = -1;
        }

        ticksMoved = Math.abs(initialPosition - currentPosition);
        sleep(1500);
        while (ticksNeeded > ticksMoved){
            clawControl("clamp");
            currentPosition = spinnyBoi.getCurrentPosition();
            ticksMoved = Math.abs(initialPosition - currentPosition);
            /*
            telemetry.addData("ticksNeeded ", ticksNeeded);
            telemetry.addData("ticksMoved ", ticksMoved);
            telemetry.addData("ticksNeeded - ticksMoved ", ticksNeeded - ticksMoved);
            telemetry.update();
             */
            spinnyBoi.setPower(power * directionSign);
        }
        spinnyBoi.setPower(0);
    }

    public void moveLift(String direction, double height, double power){
        double revsToInch = (4.35 * Math.PI);
        double ticks_per_inch = (1120 / revsToInch); //TICKS PER INCH MAY BE INCORRECT
        // THE LIFT WENT HIGHER THAN EXPECTED LAST TIME THIS WAS RUN AND BROKE THE LIFT
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
            clampyBoi.setPower(1);
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
        clampyBoi.setPower(directionSign);
    }

    public void prepCone(){
        moveLift("up", 17, .5);
        rotateSuzan("left", 65, .2);
        sleep(500);
    }

    public void scoreCone(){
        //moveLift("down", 10, .5);
        sleep(100);
        clawControl("release");
        sleep(500);
        //moveLift("up", 10, .5);
        rotateSuzan("right", 90, .2);
    }

    public void liftReset(){
        moveLift("down", 14, .5);
    }
}

