package org.firstinspires.ftc.teamcode.drive;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
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

@Autonomous(name = "RoadrunnerBasic")
public class RoadrunnerBasic extends LinearOpMode {


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // We want to start the bot at x: -36, y: -66, heading: 0 degrees
        Pose2d startPose = new Pose2d(-36, -66, 0);

        drive.setPoseEstimate(startPose);

        Trajectory trajForward = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(-36, -36), 0)
                .build();
/*
        Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                .splineTo(new Vector2d(20, 9), Math.toRadians(45))
                .build();
*/
        waitForStart();

        drive.followTrajectory(trajForward);
        //drive.followTrajectory(traj2);

    }
}