/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Add your docs here.
 */
public class Shooter {

    WPI_TalonFX leftShooter;
    WPI_TalonFX rightShooter;

    public void shooterInit(){
        // INITIATE AND SET PID COEFFICIENTS for the 2 shooter motors
        leftShooter = new WPI_TalonFX(Variables.shooterMotorLeftPort);
        rightShooter = new WPI_TalonFX(Variables.shooterMotorRightPort);

        leftShooter.configFactoryDefault();
        leftShooter.configPeakOutputForward(1);
        leftShooter.configPeakOutputReverse(-1);
        leftShooter.config_kP(0, Variables.leftShooter_kP);
        leftShooter.config_kI(0, Variables.leftShooter_kI);
        leftShooter.config_kD(0, Variables.leftShooter_kD);
        leftShooter.config_kF(0, Variables.leftShooter_kF);
        leftShooter.setInverted(true);

        rightShooter.configFactoryDefault();
        rightShooter.configPeakOutputForward(1);
        rightShooter.configPeakOutputReverse(-1);
        rightShooter.config_kP(0, Variables.rightShooter_kP);
        rightShooter.config_kI(0, Variables.rightShooter_kI);
        rightShooter.config_kD(0, Variables.rightShooter_kD);
        rightShooter.config_kF(0, Variables.rightShooter_kF);

    }

    /* Shooter via Velocity control and Falcons
     * Convert 500 RPM to units / 100ms.
	 * 2048 Units/Rev * 500 RPM / 600 100ms/min in either direction:
	 * velocity setpoint is in units/100ms
     */

     public void calculateDistanceAndShoot(boolean bPressed, double ty) {
        // This function uses the equation found at https://www.chiefdelphi.com/t/calculating-distance-to-vision-target/387183/6 to calculate distance to target at any angle relative to it.
        double distanceToTarget = Variables.constantDH/(java.lang.Math.tan(Math.toRadians((-1*ty)+15)));
        spinShooter(bPressed, distanceToTarget);


     }

     public void spinShooter(boolean bPressed, double distance) {
        // When complete, this function will use distance to calcualte an appropriate RPM for the shooter motors. 
        // Can be linear, but should really be exponential or curved. Base off of field tests.
        if (bPressed){
        leftShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(3500));
        SmartDashboard.putNumber("Left Shooter Speed: ", leftShooter.getSelectedSensorVelocity());
        rightShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(3500));
        SmartDashboard.putNumber("Right Shooter Speed: ", rightShooter.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Distance (Ft): ", distance);
        } else {
        leftShooter.set(ControlMode.PercentOutput, 0);
        rightShooter.set(ControlMode.PercentOutput, 0);
        }

     }

     private double convertToUnitsPer100ms(double rpm){
         // This function converts RPM to the unit, called "unit," that the motors use.
        double unitsPerMinute = (rpm * 2048);
        double unitsPer100 = unitsPerMinute / 600;
        return unitsPer100;
     }

     public void setFullShoot(boolean bPressed){
         // This function was in the old shoot thing, so I moved it over.
      if (bPressed){
          calculateDistanceAndShoot(true, 0);
          Robot.intake.setFullConvey(true);
          Robot.intake.intakeMotorOne.set(ControlMode.PercentOutput, Variables.intakeMotorSpeed);
      } else {
          calculateDistanceAndShoot(false, 0);
          Robot.intake.setFullConvey(false);
          Robot.intake.intakeMotorOne.set(ControlMode.PercentOutput, 0);
      }
  }


}
