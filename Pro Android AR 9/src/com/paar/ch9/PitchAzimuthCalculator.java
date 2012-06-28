package com.paar.ch9;

public class PitchAzimuthCalculator {
    private static final Vector looking = new Vector();
    private static final float[] lookingArray = new float[3];

    private static volatile float azimuth = 0;

    private static volatile float pitch = 0;

    private PitchAzimuthCalculator() {};

    public static synchronized float getAzimuth() {
        return PitchAzimuthCalculator.azimuth;
    }
    public static synchronized float getPitch() {
        return PitchAzimuthCalculator.pitch;
    }

    public static synchronized void calcPitchBearing(Matrix rotationM) {
        if (rotationM==null) return;

        looking.set(0, 0, 0);
        rotationM.transpose();
        looking.set(1, 0, 0);
        looking.prod(rotationM);
        looking.get(lookingArray);
        PitchAzimuthCalculator.azimuth = ((Utilities.getAngle(0, 0, lookingArray[0], lookingArray[2])  + 360 ) % 360);

        rotationM.transpose();
        looking.set(0, 1, 0);
        looking.prod(rotationM);
        looking.get(lookingArray);
        PitchAzimuthCalculator.pitch = -Utilities.getAngle(0, 0, lookingArray[1], lookingArray[2]);
    }
}