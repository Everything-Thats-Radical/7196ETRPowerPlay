package org.firstinspires.ftc.teamcode.drive;

import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
//import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
//import com.qualcomm.robotcore.hardware.Servo;
import android.graphics.Bitmap;

@Autonomous(name = "ABCR")
public class Auto1 extends LinearOpMode {
    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        ConceptWebcam cam = new ConceptWebcam();
        ObjectDetection detect = new ObjectDetection();

        Bitmap bmp;
        String position = "start";

        cam.callbackHandler = CallbackLooper.getDefault().getHandler();
        cam.cameraManager = ClassFactory.getInstance().getCameraManager();
        cam.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        cam.initializeFrameQueue(2);
        AppUtil.getInstance().ensureDirectoryExists(cam.captureDirectory);


        Trajectory myTrajectory = drive.trajectoryBuilder(new Pose2d())
                .strafeRight(10)
                .forward(5)
                .build();

        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectory(myTrajectory);
    }
}
