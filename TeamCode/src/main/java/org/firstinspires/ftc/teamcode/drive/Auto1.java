package org.firstinspires.ftc.teamcode.drive;
import com.qualcomm.robotcore.hardware.CRServo;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

@Autonomous(name = "Auto1")
public class Auto1 extends LinearOpMode {

    // create motor and servo objects
    private CRServo claw = null;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        ConceptWebcam cam = new ConceptWebcam();

        // Connect motors and servos to control hub
        //claw = hardwareMap.get(CRServo.class, "liftClaw");

        // set servo and motor directions
        //claw.setDirection(CRServo.Direction.FORWARD);


        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);

        Bitmap fieldImage = null;
        String position = "start";

        cam.callbackHandler = CallbackLooper.getDefault().getHandler();
        cam.cameraManager = ClassFactory.getInstance().getCameraManager();
        cam.cameraName = hardwareMap.get(WebcamName.class, "cam");

        cam.initializeFrameQueue(2);
        AppUtil.getInstance().ensureDirectoryExists(cam.captureDirectory);

        try {
            telemetry.addData("Ready", "");
            telemetry.update();
            while (cam.frameQueue.peek() == null) {
                cam.openCamera();
                cam.startCamera();
                sleep(500);
            }
            fieldImage = cam.frameQueue.peek();
        } catch (Exception e) {
            telemetry.addData("Cow too too", "");
            telemetry.update();
            sleep(1000);
        } finally {
            cam.closeCamera();
            telemetry.addData("Closed Camera", "");
            telemetry.update();
        }

        telemetry.addData("!@!", String.valueOf(fieldImage.getColor(0, 0))); // checks if image is grabbed
        telemetry.update();
        sleep(2000);
        String color = "value retrieval failed";


        ColorDetectionNuevo colorChecker = new ColorDetectionNuevo();
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        color = colorChecker.getColor(fieldImage);
        //}

        telemetry.addData("The cone is ", color);
        telemetry.update();

        TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)

                .forward(3)
                .turn(90)
                //.forward(3)
                //Opens Claw Here
                //.addTemporalMarker(() -> claw.setPower(.5))
                //.waitSeconds(.5)
                //.back(3)
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


        if (color.equals(null)) {
            telemetry.addData("Null?", "heck yeah it's null");
            telemetry.update();
        }
        if (!color.equals(null)) {
            telemetry.addData("Not null?", "heck no it's not null");
            telemetry.update();
        }
        sleep(1000);
        //Chooses where to park based on the color of the cone.
        if (color.equals("blue")) {
            //Strafe 24 inches
            drive.followTrajectorySequence(nextZone);
            telemetry.addData("Color: ", color);
            telemetry.update();
        } else if (color.equals("red")) {
            //Strafe 48 inches
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