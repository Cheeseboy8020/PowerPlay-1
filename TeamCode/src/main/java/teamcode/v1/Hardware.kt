package teamcode.v1

import com.acmerobotics.dashboard.config.Config
import com.asiankoala.koawalib.control.controller.PIDGains
import com.asiankoala.koawalib.control.motor.FFGains
import com.asiankoala.koawalib.control.profile.MotionConstraints
import com.asiankoala.koawalib.hardware.motor.EncoderFactory
import com.asiankoala.koawalib.hardware.motor.MotorFactory
import com.asiankoala.koawalib.hardware.servo.KServo
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.subsystem.odometry.KThreeWheelOdometry
import teamcode.v1.constants.OdoConstants
import teamcode.v1.constants.ArmConstants
import teamcode.v1.constants.ClawConstants
import teamcode.v1.constants.GuideConstants
import teamcode.v1.constants.LiftConstants
import teamcode.v1.subsystems.KLimitSwitch

class Hardware(startPose: Pose) {
    val fl = MotorFactory("fl")
        .reverse
        .brake
        .build()

    val bl = MotorFactory("bl")
        .reverse
        .brake
        .build()

    val br = MotorFactory("br")
        .forward
        .brake
        .build()

    val fr = MotorFactory("fr")
        .forward
        .brake
        .build()

    val liftLeadMotor = MotorFactory("liftLead")
        .float
        .reverse
        .createEncoder(EncoderFactory(LiftConstants.ticksPerUnit)
            .zero(LiftConstants.homePos)
            .reverse
        )
        .withMotionProfileControl(
            PIDGains(LiftConstants.kP, LiftConstants.kI, LiftConstants.kD),
            FFGains(kS = LiftConstants.kS, kV = LiftConstants.kV, kA = LiftConstants.kA, kG = LiftConstants.kG),
            MotionConstraints(LiftConstants.maxVel, LiftConstants.maxAccel),
            allowedPositionError = LiftConstants.allowedPositionError,
            disabledPosition = LiftConstants.disabledPosition
        )
        .build()

    val liftSecondMotor = MotorFactory("lift2")
        .reverse
        .float
        .build()

    val armMotor = MotorFactory("Arm")
        .reverse
        .float
        .createEncoder(EncoderFactory(ArmConstants.ticksPerUnit)
            .reverse
            .zero(ArmConstants.homePos)
        )
        .withMotionProfileControl(
            PIDGains(ArmConstants.kP, ArmConstants.kI, ArmConstants.kD),
            FFGains(kS = ArmConstants.kS, kV = ArmConstants.kV, kA = ArmConstants.kA, kCos = ArmConstants.kCos),
            MotionConstraints(ArmConstants.maxVel, ArmConstants.maxAccel, ArmConstants.maxDeccel),
            allowedPositionError = ArmConstants.allowedPositionError,
            disabledPosition = ArmConstants.disabledPosition
        )
        .build()

    val clawServo = KServo("Claw")
        .startAt(ClawConstants.closePos)

    val guideServo = KServo("Guide")
        .startAt(GuideConstants.telePos)

    val limitSwitch = KLimitSwitch("LimitSwitch")

    private val leftEncoder = EncoderFactory(Hardware.ticksPerUnit)
        .revEncoder
        .build(fl)
    private val rightEncoder = EncoderFactory(Hardware.ticksPerUnit)
        .revEncoder
        .build(bl)
    private val auxEncoder = EncoderFactory(Hardware.ticksPerUnit)
        .reverse
        .revEncoder
        .build(fr)

    val odometry = KThreeWheelOdometry(
        leftEncoder,
        rightEncoder,
        auxEncoder,
        OdoConstants.TRACK_WIDTH / 25.4,
        OdoConstants.PERP_TRACKER,
        startPose
    )


    @Config
    companion object {
        private const val ticksPerUnit = 1892.3724
    }
}