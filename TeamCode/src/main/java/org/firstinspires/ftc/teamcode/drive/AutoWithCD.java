package org.firstinspires.ftc.teamcode.drive;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

    @Autonomous(name = "RRCD")
    public class AutoWithCD extends LinearOpMode {

        // create motor and servo objects
        private CRServo claw = null;

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void runOpMode() {
            SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
            NormalizedColorSensor colorSensor;
            colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
            final float[] hsvValues = new float[3];

            if (colorSensor instanceof SwitchableLight) {
                ((SwitchableLight)colorSensor).enableLight(true);
            }

            Pose2d startPose = new Pose2d(0, 0, 0);
            drive.setPoseEstimate(startPose);

            String position = "start";
            String color = "red";
            NormalizedRGBA colors = colorSensor.getNormalizedColors();

            /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
             * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
             * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
             * for an explanation of HSV color. */

            // Update the hsvValues array by passing it to Color.colorToHSV()
            Color.colorToHSV(colors.toColor(), hsvValues);

            telemetry.addLine()
                    .addData("Red", "%.3f", colors.red)
                    .addData("Green", "%.3f", colors.green)
                    .addData("Blue", "%.3f", colors.blue);


            telemetry.addData("The cone is ", color);
            telemetry.update();

            sleep(10000);

            TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)

                    .forward(3)
                    .turn(90)
                    .forward(24)
                    .turn(-90)
                    .forward(45)
                    .turn(-90)
                    .build();

            TrajectorySequence nextZone = drive.trajectorySequenceBuilder(startPose)
                    .forward(24)
                    .build();


            waitForStart();
            if (!isStopRequested())
                drive.followTrajectorySequence(trajSeq);

            sleep(1000);
                if (color.equals("blue")) {
                    drive.followTrajectorySequence(nextZone);
                    telemetry.addData("Color: ", color);
                    telemetry.update();
                } else if (color.equals("red")) {
                    drive.followTrajectorySequence(nextZone);
                    drive.followTrajectorySequence(nextZone);
                    telemetry.addData("Color: ", color);
                } else if (color.equals("green")) {
                    telemetry.addData("Color: ", color);
                } else {
                    String noColor = "Color not detected.";
                    telemetry.addData("Color: ", noColor);
                }
        }
    }

