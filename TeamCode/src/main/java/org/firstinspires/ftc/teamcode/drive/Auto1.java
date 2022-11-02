package org.firstinspires.ftc.teamcode.drive;
import com.qualcomm.robotcore.hardware.CRServo;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import com.qualcomm.robotcore.hardware.Servo;
import android.graphics.Bitmap;

@Autonomous(name = "Auto1")
public class Auto1 extends LinearOpMode {

    // create motor and servo objects
    private CRServo claw = null;

    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        ConceptWebcam cam = new ConceptWebcam();

        // Connect motors and servos to control hub
        claw = hardwareMap.get(CRServo.class, "liftClaw");

        // set servo and motor directions
        claw.setDirection(CRServo.Direction.FORWARD);


        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);

        Bitmap fieldImage;
        String position = "start";

        cam.callbackHandler = CallbackLooper.getDefault().getHandler();
        cam.cameraManager = ClassFactory.getInstance().getCameraManager();
        cam.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

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
        } finally {
            cam.closeCamera();
        }

        ColorDetectionNuevo colorChecker = new ColorDetectionNuevo();
        String color = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            color = colorChecker.getColor(fieldImage);
        }

        waitForStart();
        TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)
                .forward(3)
                .strafeLeft(12)
                .forward(3)
                //Opens Claw Here
                .addTemporalMarker(() -> claw.setPower(.5))
                .waitSeconds(.5)
                .back(3)
                .strafeLeft(12)
                .forward(45)
                .turn(-90)
                .build();

        TrajectorySequence nextZone = drive.trajectorySequenceBuilder(startPose)
                .forward(24)
                .build();


        waitForStart();
        if (!isStopRequested())
            drive.followTrajectorySequence(trajSeq);

        //Chooses where to park based on the color of the cone.
        if(color.equals("blue")){
            //Strafe 24 inches
            drive.followTrajectorySequence(nextZone);
            telemetry.addData("Color: ", color);
        }
        else if(color.equals("green")){
            //Strafe 48 inches
            drive.followTrajectorySequence(nextZone);
            drive.followTrajectorySequence(nextZone);
            telemetry.addData("Color: ", color);
        }
        else if(color.equals("red")){
            telemetry.addData("Color: ", color);
        }
        else{
            String noColor = "Color not detected.";
            telemetry.addData("Color: ", noColor);
        }

    }
}
