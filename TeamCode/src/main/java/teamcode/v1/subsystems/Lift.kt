package teamcode.v1.subsystems

import com.asiankoala.koawalib.hardware.motor.KMotor
import com.asiankoala.koawalib.subsystem.KSubsystem

class Lift( private val leadMotor: KMotor,
            private val secondMotor: KMotor
            ) : KSubsystem() {

    fun setPos(pos: Double) {
        leadMotor.setProfileTarget(pos)
    }

    override fun periodic() {
        secondMotor.power = leadMotor.power
    }
}