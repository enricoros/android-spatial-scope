package com.enricoros.androidspatialscope;

public class SomeMath {

    public static void decomposeEulerRotation(float[] changeXYZ, float[] R, float[] prevR) {
        final float ri0 = R[0], ri1 = R[1], ri2 = R[2],
                    ri3 = R[4], ri4 = R[5], ri5 = R[6],
                    ri6 = R[8], ri7 = R[9], ri8 = R[10];

        final float pri0 = prevR[0], pri1 = prevR[1], pri2 = prevR[2],
                    pri3 = prevR[4], pri4 = prevR[5], pri5 = prevR[6],
                    pri6 = prevR[8], pri7 = prevR[9], pri8 = prevR[10];

        final float[] r = {
                 pri0 * ri0 + pri3 * ri3 + pri6 * ri6,
                pri0 * ri1 + pri3 * ri4 + pri6 * ri7,
                 pri0 * ri2 + pri3 * ri5 + pri6 * ri8,

                0,
                pri1 * ri1 + pri4 * ri4 + pri7 * ri7,
                 pri1 * ri2 + pri4 * ri5 + pri7 * ri8,

                pri2 * ri0 + pri5 * ri3 + pri8 * ri6,
                pri2 * ri1 + pri5 * ri4 + pri8 * ri7,
                pri2 * ri2 + pri5 * ri5 + pri8 * ri8,
        };

        /* Old decomposition
          angleChange[2] = (float) Math.atan2(r[1], r[4]);
          angleChange[0] = (float) Math.asin(-r[7]);
          angleChange[1] = (float) Math.atan2(-r[6], r[8]);
        */

        /* Unadjusted Shoemakes's */
        final float c2 = (float) Math.hypot(r[0], r[1]);
        changeXYZ[0] = (float) Math.atan2(r[5], r[8]);
        changeXYZ[1] = (float) Math.atan2(-r[2], c2);
        changeXYZ[2] = (float) Math.atan2(r[1], r[0]);

        /* Adjusted Shoemake's
         *  https://d3cw3dd2w32x2b.cloudfront.net/wp-content/uploads/2012/07/euler-angles.pdf
         */
    }

}
