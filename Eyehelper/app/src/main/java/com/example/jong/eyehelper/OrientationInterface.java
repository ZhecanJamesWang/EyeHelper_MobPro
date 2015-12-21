package com.example.jong.eyehelper;

//Same as before
/**
 * Created by jong on 11/19/15.
 */
 
 //If this code is created by Kaleb Kircher, you should site him, even if he has not
 //added the license agreement at the top
public interface OrientationInterface
{
    /**
     * Get the orientation of the device. This method can be called *only* after
     * setAcceleration(), setMagnetic() and getGyroscope() have been called.
     *
     * @return float[] an array containing the linear acceleration of the device
     *         where values[0]: azimuth, rotation around the Z axis. values[1]:
     *         pitch, rotation around the X axis. values[2]: roll, rotation
     *         around the Y axis. with respect to the Android coordinate system.
     */
    public float[] getOrientation();

    /**
     * The complementary filter coefficient, a floating point value between 0-1,
     * exclusive of 0, inclusive of 1.
     *
     * @param filterCoefficient
     */
    public void setFilterCoefficient(float filterCoefficient);
}
