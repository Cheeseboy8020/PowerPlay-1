package teamcode.v1.opmodes

import com.asiankoala.koawalib.command.KOpMode
import com.asiankoala.koawalib.command.commands.Cmd
import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.logger.LoggerConfig
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.math.radians
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import teamcode.v1.Robot
import teamcode.v1.commands.sequences.DepositSequence
import teamcode.v1.commands.sequences.HomeSequence
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.commands.subsystems.DriveCmd
import teamcode.v1.constants.ArmConstants
import teamcode.v1.constants.GuideConstants
import teamcode.v1.constants.LiftConstants


@TeleOp
open class KTeleOp() : KOpMode(photonEnabled = false) {
    private val robot by lazy { Robot(Pose(-66.0, 40.0, 180.0.radians)) }
    private var slowMode = false

    override fun mInit() {
        Logger.config = LoggerConfig.DASHBOARD_CONFIG
        scheduleDrive()
        scheduleCycling()
//        scheduleTest()
    }

    private fun scheduleDrive() {
        robot.drive.defaultCommand = object : Cmd() {
            override fun execute() {
                val xScalar: Double
                val yScalar: Double
                val rScalar: Double
                if (driver.a.isPressed) {
                    xScalar = 0.4
                    yScalar = 0.4
                    rScalar = 0.4
                } else {
                    xScalar = 1.0
                    yScalar = 1.0
                    rScalar = 0.75
                }
                val drivePowers = Pose(
                    driver.leftStick.xAxis * xScalar,
                    driver.leftStick.yInverted.yAxis * yScalar,
                    driver.rightStick.xInverted.xAxis * rScalar
                )
                robot.drive.powers = drivePowers
            }
        }
    }

    private fun scheduleCycling() {
        driver.rightBumper.onPress(HomeSequence(robot.lift, robot.claw, robot.arm, robot.guide, ArmConstants.intervalPos, ArmConstants.groundPos, LiftConstants.homePos, GuideConstants.telePos))
        driver.leftBumper.onPress(DepositSequence(robot.lift, robot.arm, robot.claw, robot.guide, ArmConstants.highPos, LiftConstants.highPos, GuideConstants.depositPos))
        driver.leftTrigger.onPress(ClawCmds.ClawCloseCmd(robot.claw))
        driver.dpadUp.onPress(DepositSequence(robot.lift, robot.arm, robot.claw, robot.guide, ArmConstants.midPos, LiftConstants.midPos, GuideConstants.depositPos))
        driver.y.onPress(DepositSequence(robot.lift, robot.arm, robot.claw, robot.guide, ArmConstants.lowPos, LiftConstants.lowPos, GuideConstants.lowPos))
        driver.rightTrigger.onPress(ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos))
        driver.x.onPress(HomeSequence(robot.lift, robot.claw, robot.arm, robot.guide, ArmConstants.intervalPos, ArmConstants.groundPos, 3.0, GuideConstants.telePos))
        driver.b.onPress(HomeSequence(robot.lift, robot.claw, robot.arm, robot.guide, ArmConstants.intervalPos, ArmConstants.groundPos, 5.0, GuideConstants.telePos))
        gunner.leftTrigger.onPress(InstantCmd({robot.lift.setPos(-15.5)}))
        gunner.rightTrigger.onPress(InstantCmd({robot.arm.setPos(-270.0)}))
        gunner.leftBumper.onPress(InstantCmd({robot.lift.setPos(11.0)}))
        gunner.rightBumper.onPress(InstantCmd({robot.lift.setPos(0.0)}))
    }

    private fun scheduleTest() {
        driver.leftBumper.onPress(InstantCmd({robot.arm.setPos(ArmConstants.highPos)}, robot.arm))
        driver.rightBumper.onPress(InstantCmd({robot.lift.setPos(LiftConstants.highPos)}, robot.lift))
//        driver.leftBumper.onPress(InstantCmd({robot.claw.setPos(ClawConstants.openPos)}))
//        driver.rightBumper.onPress(InstantCmd({robot.claw.setPos(ClawConstants.closePos)}))
        driver.a.onPress(InstantCmd({robot.arm.setPos(-10.0)}, robot.arm))
        driver.b.onPress(InstantCmd({robot.lift.setPos(0.0)}, robot.lift))
    }

    override fun mLoop() {
        Logger.addTelemetryData("arm pos", robot.hardware.armMotor.pos)
        Logger.addTelemetryData("lift pos", robot.hardware.liftLeadMotor.pos)
        Logger.addTelemetryData("arm power", robot.arm.motor.power)
        Logger.addTelemetryData("lift power", robot.hardware.liftLeadMotor.power)
    }
}