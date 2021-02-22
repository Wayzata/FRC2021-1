/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.concurrent.TimeUnit;

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
        leftShooter.config_kP(0, Variables.leftShooter_kP);
        leftShooter.config_kI(0, Variables.leftShooter_kI);
        leftShooter.config_kD(0, Variables.leftShooter_kD);
        leftShooter.config_kF(0, Variables.leftShooter_kF);
        leftShooter.setInverted(true);

        rightShooter.configFactoryDefault();
        rightShooter.config_kP(0, Variables.rightShooter_kP);
        rightShooter.config_kI(0, Variables.rightShooter_kI);
        rightShooter.config_kD(0, Variables.rightShooter_kD);
        rightShooter.config_kF(0, Variables.rightShooter_kF);

    }

    // THESE TWO FUNCTIONS JUST PUT THE DISTANCE THE ROBOT CALCULATES AND ALL ASSOCIATED VARIBLES ON THE SMART DASHBOARD WITHOUT SHOOTING
    private double distanceToTarget(double ty){
        double d = 0;
        if (ty > 0){
            d = 10;
        } else if (ty <= 0){
            d = 1.5 + Variables.constantDH/(java.lang.Math.tan(Math.toRadians((1.41*ty)+47.3)));
        }

        return d;
    }


    public void calculateDistance(boolean bPressed, double ty) {
        double distanceToTarget = distanceToTarget(ty);
        SmartDashboard.putNumber("ty: ", ty);
        SmartDashboard.putNumber("Distance (ignoring tx)", distanceToTarget);
     }

     public void calculateDistanceatAngle(boolean bPressed, double ty, double tx) {
        double distanceToTarget = Variables.constantDH/((java.lang.Math.tan(Math.toRadians((-1*ty)+15)))*java.lang.Math.cos(Math.toRadians((tx))));
        SmartDashboard.putNumber("ty: ", ty);
        SmartDashboard.putNumber("tx: ", tx);
        SmartDashboard.putNumber("Distance (incorporating tx)", distanceToTarget);
     }

     /* Shooter via Velocity control and Falcons
     * Convert 500 RPM to units / 100ms.
	 * 2048 Units/Rev * 500 RPM / 600 100ms/min in either direction:
	 * velocity setpoint is in units/100ms
     */
     

     public void calculateDistanceAndShoot(boolean bPressed, double ty) {
        // This function uses the equation found at https://www.chiefdelphi.com/t/calculating-distance-to-vision-target/387183/6 to calculate distance to target at any angle relative to it.
        double distanceToTarget = distanceToTarget(ty);

        spinShooter(bPressed, distanceToTarget);
     }

     public void calculateDistanceAndShootatAngle(boolean bPressed, double ty, double tx) {
        // This function uses the equation found at https://www.chiefdelphi.com/t/calculating-distance-to-vision-target/387183/6 to calculate distance to target at any angle relative to it.
        double distanceToTarget = Variables.constantDH/((java.lang.Math.tan(Math.toRadians((-1*ty)+15)))*java.lang.Math.cos(Math.toRadians((tx))));
        spinShooter(bPressed, distanceToTarget);
     }

     public void spinShooter(boolean bPressed, double distance) {
        // When complete, this function will use distance to calcualte an appropriate RPM for the shooter motors. 
        // Can be linear, but should really be exponential or curved. Base off of field tests.
        if (bPressed){
            if (distance <= 10){
                leftShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(2750));
                SmartDashboard.putNumber("Left Shooter Speed: ", (leftShooter.getSelectedSensorVelocity() * 600)/2048);
                rightShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(2750));
                SmartDashboard.putNumber("Right Shooter Speed: ", (rightShooter.getSelectedSensorVelocity() * 600)/2048);
            } else if (distance > 10 && distance < 15){
                leftShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(50*distance + 2250));
                SmartDashboard.putNumber("Left Shooter Speed: ", (leftShooter.getSelectedSensorVelocity() * 600)/2048);
                rightShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(50*distance + 2250));
                SmartDashboard.putNumber("Right Shooter Speed: ", (rightShooter.getSelectedSensorVelocity() * 600)/2048);
            } else if (distance > 15){
                leftShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(SmartDashboard.getNumber("motorSpeed", 3500)));
                SmartDashboard.putNumber("Left Shooter Speed: ", (leftShooter.getSelectedSensorVelocity() * 600)/2048);
                rightShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(SmartDashboard.getNumber("motorSpeed", 3500)));
                SmartDashboard.putNumber("Right Shooter Speed: ", (rightShooter.getSelectedSensorVelocity() * 600)/2048);
            }
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

  public void autokF() {
    // Initiate all variables neccesary
    double currentLeftkF = Variables.leftShooter_kF;
    double currentRightkF = Variables.rightShooter_kF;
    int[] benchmarks = {3000, 3500, 4000};
    double[] kFArr = new double[3];
    Boolean leftMotorTuned = false;
    Boolean rightMotorTuned = false;
    double kFLeft, kFRight;


    // AUTO-TUNE kF of Left Shooter
    for(int i = 0; i<=2; i++) {
        while (!leftMotorTuned) { 
            leftShooter.config_kF(0, currentLeftkF);
            leftShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(benchmarks[i]));
            try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            int rpm = (leftShooter.getSelectedSensorVelocity() * 600)/2048;
            if(Math.abs(rpm-benchmarks[i]) < 50) {
                leftMotorTuned = true;
            } else if (rpm > benchmarks[i]) {
                currentLeftkF -= .0003;
            } else {
                currentLeftkF += .0003;
            }
        }
        kFArr[i] = currentLeftkF;
        leftMotorTuned = false;
    }
    kFLeft = (kFArr[0] + kFArr[1] + kFArr[2])/3;
    System.out.println(kFLeft);


    // AUTO-TUNE kF of Right Shooter

    for(int i = 0; i<=2; i++) {
        while (!rightMotorTuned) { 
            rightShooter.config_kF(0, currentRightkF);
            rightShooter.set(ControlMode.Velocity, convertToUnitsPer100ms(benchmarks[i]));
            try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            int rpm = (rightShooter.getSelectedSensorVelocity() * 600)/2048;
            if(Math.abs(rpm-benchmarks[i]) < 50) {
                rightMotorTuned = true;
            } else if (rpm > benchmarks[i]) {
                currentRightkF -= .0003;
            } else {
                currentRightkF += .0003;
            }
        }
        kFArr[i] = currentRightkF;
        rightMotorTuned = false;
    }
    kFRight = (kFArr[0] + kFArr[1] + kFArr[2])/3;
    System.out.println(kFRight);

  }


}
