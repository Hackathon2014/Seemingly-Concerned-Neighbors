package seemingly.concerned.neighbors;

import edu.mines.jtk.io.*;

import java.io.*;
import java.nio.*;
import javax.swing.*;

import static edu.mines.jtk.util.ArrayMath.*;

/**
 * @author Elias Arias, Colorado School of Mines CWP 
 * with contributions from Thomas Rapstine, Gabe Martinez, and Brent Putman
 * This software is written for use in the GeoRZA android application.
 * GeoRZA is a tool for geophysicists to better understand the vertical and
 * horizontal seismic resolutions, with uncertainties, based off acquisition
 * parameters. The tool also allows users to use these uncertainties as 
 * constraints for a gravity inversion that inverts for the depth of top and
 * bottom of a layer in the subsurface. Throughout this software we will be
 * using units of meters (m), seconds (s), and meters/second (m/s).
 * @version 25.10.2014 12:01pm
 */

public class GeoRZA {

  /**
   * TESTING.
   */
  private static void goPrint(float[] t, float[] vrms) {
    System.out.println("The time to the top layer is "+t[0]);
    System.out.println("The time to the bottom layer is "+t[1]);
    System.out.println("The RMS velocity 1 is "+vrms[0]);
    System.out.println("The RMS velocity 2 is "+vrms[1]);
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer.
   * @param th thickness of layer being imaged
   * @param v1 stacking velocity
   * @param v2 velocity of the layer
   * @param zt depth of top of layer
   * @return array[2] of t01 and t02 (top and bottom times resepctively)
   */
  private static float[] goTimeCalc(float th, float v1, float v2, float zt) {
    float[] t = new float[2];
    t[0] = (2.0f*zt)/v1;
    t[1] = t[0] + th/v2;
    return t;
  }

  /**
   * Calculates the RMS velocities of the layer.
   * @param th thickness of layer being imaged
   * @param v1 stacking velocity
   * @param v2 velocity of the layer
   * @return array[2] of RMS velocities
   */
  private static float[] goVrmsCalc(float[] t, float v1, float v2) {
    float[] vrms = new float[2];
    vrms[0] = v1;
    vrms[1] = sqrt((v1*v1*t[0] + v2*v2*t[1])/(t[0] + t[1]));
    return vrms;
  }

  /**
   * Calculates the uncertainty in the depth of the top and bottom of layer.
   * @param t the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @return array[4] the uncertainties in top and bottom depth values
   */
  private static float[] goDepthUncertainty(float[] t, float[] vrms) {
    float[] zu = new float[4];
    float ztm, ztp, zbm, zbp;
    ztm = (t[0]/2.0f)*(vrms[0]); 

    return zu;
  }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        float th = 70;               //(m)
        float v1 = 2500;             //(m/s) 
        float v2 = 3000;             //(m/s) 
        float zt = 2000;             //(m)

        float[] t = new float[2];    //(s)
        float[] vrms = new float[2]; //(m/s)
        float[] zu = new float[4];   //(m) 

        t = goTimeCalc(th,v1,v2,zt);
        vrms = goVrmsCalc(t,v1,v2);
        //zu = goDepthUncertainty(t,vrms);
        goPrint(t,vrms);
      }
    });
  }
}
