/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

/**
 * Add your docs here.
 */
public class Shooter {


    WPI_TalonFX leftShooter;
    WPI_TalonFX rightShooter;

    public Shooter(){
        leftShooter = new WPI_TalonFX(Variables.shooterMotorLeftPort);
        rightShooter = new WPI_TalonFX(Variables.shooterMotorRightPort);

        leftShooter.configFactoryDefault();
        leftShooter.configPeakOutputForward(1);
        leftShooter.configPeakOutputReverse(-1);
        leftShooter.config_kP(0, Variables.leftShooter_kP);
        leftShooter.config_kI(0, Variables.leftShooter_kI);
        leftShooter.config_kD(0, Variables.leftShooter_kD);
        leftShooter.config_kF(0, Variables.leftShooter_kF);

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

     public void spinShooter(double distance){
        leftShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(15));
        rightShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(15));
     }

     private double convertToUnitsPer100ms(double rpm){
        double unitsPerMinute = (rpm * 2048);
        double unitsPer100 = unitsPerMinute / 600;
        return unitsPer100;
     }
}
