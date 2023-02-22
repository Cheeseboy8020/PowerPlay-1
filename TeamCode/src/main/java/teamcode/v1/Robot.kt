package teamcode.v1

import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.subsystem.drive.KMecanumOdoDrive
import teamcode.v1.constants.ArmConstants
import teamcode.v1.subsystems.*

class Robot(startPose: Pose) {
    val hardware = Hardware(startPose)

    val drive = KMecanumOdoDrive(
        hardware.fl,
        hardware.bl,
        hardware.br,
        hardware.fr,
        hardware.odometry,
        true
    )

    val arm = Arm(hardware.armMotor)
    val claw = Claw(hardware.clawServo)
    val guide = Guide(hardware.guideServo)
    val lift = Lift(hardware.liftLeadMotor, hardware.liftSecondMotor)

    init {
        arm.setPos(ArmConstants.groundPos)
        lift.setPos(0.0)
    }
}