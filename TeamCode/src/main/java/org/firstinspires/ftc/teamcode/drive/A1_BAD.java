package org.firstinspires.ftc.teamcode.drive;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous(name = "A1_BAD")
    public class A1_BAD extends LinearOpMode {

        // create motor and servo objects
        //private CRServo claw = null;
        ColorSensor colorSensor;
        DistanceSensor distanceSensor;
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void runOpMode() {
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

            Pose2d startPose = new Pose2d(0, 0, 0);
            Pose2d scanPose = new Pose2d(0, -25, 0);
            drive.setPoseEstimate(startPose);

            // send the info back to driver station using telemetry function.

            // change the background color to match the color detected by the RGB sensor.
            // pass a reference to the hue, saturation, and value array as an argument
            // to the HSVToColor method.


            TrajectorySequence initialDriveForScan = drive.trajectorySequenceBuilder(new Pose2d())
                    .forward(25,
                            SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();

            TrajectorySequence driveAfterScan = drive.trajectorySequenceBuilder(initialDriveForScan.end())
                    .forward(7,
                            SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();


            TrajectorySequence turnLeft = drive.trajectorySequenceBuilder(driveAfterScan.end())
                    .turn(3.14/2)
                    .build();

            TrajectorySequence turnRight = drive.trajectorySequenceBuilder(driveAfterScan.end())
                    .turn(-3.14/2)
                    .build();

            TrajectorySequence parkDriveRight = drive.trajectorySequenceBuilder(turnRight.end())
                    .forward(25,
                            SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();

            TrajectorySequence parkDriveLeft = drive.trajectorySequenceBuilder(turnLeft.end())
                    .forward(25,
                            SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();

            TrajectorySequence leftParkFinalTurn = drive.trajectorySequenceBuilder(parkDriveLeft.end())
                    .turn(-3.14/2)
                    .build();

            TrajectorySequence rightParkFinalTurn = drive.trajectorySequenceBuilder(parkDriveRight.end())
                    .turn(3.14/2)
                    .build();

            TrajectorySequence finalDriveRight = drive.trajectorySequenceBuilder(rightParkFinalTurn.end())
                    .forward(8,
                            SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();

            TrajectorySequence finalDriveLeft = drive.trajectorySequenceBuilder(leftParkFinalTurn.end())
                    .forward(8,
                            SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            waitForStart();

            if (!isStopRequested()) {
                drive.followTrajectorySequence(initialDriveForScan);
            }

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



            telemetry.addData("The cone is ", coneColor);
            telemetry.update();

            sleep(1000);

            if (coneColor.equals("red")) {
                telemetry.addData("Color: ", coneColor);
                telemetry.update();
                drive.followTrajectorySequence(driveAfterScan);
                drive.followTrajectorySequence(turnLeft);
                drive.followTrajectorySequence(parkDriveLeft);
                drive.followTrajectorySequence(leftParkFinalTurn);
                drive.followTrajectorySequence(finalDriveLeft);
            } else if (coneColor.equals("green")) {
                telemetry.addData("Color: ", coneColor);
                telemetry.update();
                drive.followTrajectorySequence(driveAfterScan);
            } else if (coneColor.equals("blue")) {
                telemetry.addData("Color: ", coneColor);
                telemetry.update();
                drive.followTrajectorySequence(driveAfterScan);
                drive.followTrajectorySequence(turnRight);
                drive.followTrajectorySequence(parkDriveRight);
                drive.followTrajectorySequence(rightParkFinalTurn);
                drive.followTrajectorySequence(finalDriveRight);
            } else {
                String noColor = "Color not detected.";
                telemetry.addData("Color: ", noColor);
            }
        }
    }

