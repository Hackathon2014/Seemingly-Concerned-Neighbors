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
   * Calculates the times to the top and bottom of a subsurface layer for
   * zero-offset source-receiver geometry.
   * @param th thickness of layer being imaged
   * @param v1 stacking velocity
   * @param v2 velocity of the layer
   * @param zt depth of top of layer
   * @return array[2] of t01 and t02 (top and bottom times resepctively)
   */
  private static float[] goTimeCalcZeroOff(float th, float v1, float v2, 
      float zt) {
    float[] t0 = new float[2];
    t0[0] = (2.0f*zt)/v1;
    t0[1] = t0[0] + th/v2;
    return t0;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * zero-offset source-receiver geometry.
   * @param th thickness of layer being imaged
   * @param v1 stacking velocity
   * @param v2 velocity of the layer
   * @param zt depth of top of layer
   * @param n number of offset values
   * @return array[2][n] of t01 and t02 (top and bottom times resepctively)
   */
  private static float[][] goTimeCalcZeroOff(float th, float v1, float v2, 
      float zt, int n) {
    float[][] t0 = new float[2][n];
    for (int i=0; i<n; ++i) {
      t0[0][i] = (2.0f*zt)/v1;
      t0[1][i] = t0[0][i] + th/v2;
    }
    return t0;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * a specified offset source-receiver geometry.
   * @param t0 the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param off offset between source and receiver
   * @return array[2] of tx1 and tx2 (top and bottom times resepctively)
   */
  private static float[] goTimeCalcNonZeroOff(float[] t0, float[] vrms, 
      float off) {
    float[] tx = new float[2];
    tx[0] = sqrt(t0[0]*t0[0] + (off/vrms[0])*(off/vrms[0]));
    tx[1] = sqrt(t0[1]*t0[1] + (off/vrms[1])*(off/vrms[1]));
    return tx;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * a specified offset source-receiver geometry.
   * @param t0 the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param off offset between source and receiver
   * @return array[2][#off] of tx1 and tx2 (top and bottom times resepctively)
   */
  private static float[][] goTimeCalcNonZeroOff(float[][] t0, float[][] vrms, 
      float[] off) {
    int n = off.length;
    float[][] tx = new float[2][n];
    for (int i=0; i<n; ++i) {
      tx[0][i] = sqrt(t0[0][i]*t0[0][i] + 
          (off[i]/vrms[0][i])*(off[i]/vrms[0][i]));
      tx[1][i] = sqrt(t0[1][i]*t0[1][i] + 
          (off[i]/vrms[1][i])*(off[i]/vrms[1][i]));
    }
    return tx;
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
   * Calculates the RMS velocities of the layer.
   * @param th thickness of layer being imaged
   * @param v1 stacking velocity
   * @param v2 velocity of the layer
   * @return array[2][#off] of RMS velocities
   */
  private static float[][] goVrmsCalc(float[][] t, float v1, float v2) {
    int n = t[0].length;
    float[][] vrms = new float[2][n];
    for (int i=0; i<n; ++i) {
      vrms[0][i] = v1;
      vrms[1][i] = sqrt((v1*v1*t[0][i] + v2*v2*t[1][i])/(t[0][i] + t[1][i]));
    }
    return vrms;
  }

  /**
   * Calculates the delta RMS velocities.
   * @param t
   * @param vrms 
   * @param off
   * @param freq
   * @param A
   * @return array[2] delta RMS velocities
   */
  private static void goDVrms(float[] t, float[] vrms, float off, float freq,
      float[] A) {
    float delvrms 
  }

  /**
   * Calculates the delta RMS velocities.
   * @param t
   * @param vrms 
   * @param off
   * @param freq
   * @param A
   * @return array[2][#off] delta RMS velocities
   */
  private static void goDVrms(float[][] t, float[][] vrms, float[] off, 
      float freq, float[] A) {

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
    //ztp = 
    //zbm = 
    //zbp = 

    return zu;
  }

  /**
   *********************************************************************** 
   * TESTING.
   */
  private static float goDepthCalc(float tx, float vrms, float off) {
    float r = (tx*vrms)/2.0f;
    float theta = asin(off/(2.0f*r));
    float z = cos(theta)*tx*vrms/2.0f;
    return z;
  }

  private static float[] goDepthCalc(float[] tx, float[] vrms, float[] off) {
    int n = off.length;
    float[] theta = new float[n];
    float[] z = new float[n];
    float[] r = new float[n];
    for (int i=0; i<n; ++i) {
      r[i] = (tx[i]*vrms[i])/2.0f;
      theta[i] = asin(off[i]/(2.0f*r[i]));
      z[i] = cos(theta[i])*tx[i]*vrms[i]/2.0f;
    }
    return z;
  }

  private static void goPrint(float[] t, float[] vrms) {
    System.out.println("The time to the top layer is "+t[0]);
    System.out.println("The time to the bottom layer is "+t[1]);
    System.out.println("The RMS velocity 1 is "+vrms[0]);
    System.out.println("The RMS velocity 2 is "+vrms[1]);
  }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        int n = 300;                //# of offsets
        float th = 70;              //(m)
        float v1 = 2000;            //(m/s) 
        float v2 = 2200;            //(m/s) 
        float zt = 500;             //(m)
        float off = 300;          //(m)
        float[] offa = new float[n];//(m)
        float[] za = new float[n];   //(m)
        for (int i=0; i<n; ++i) {
          offa[i] = i;
        }

        float[] t0 = new float[2];    //(s)
        float[][] t0a = new float[2][n];    //(s)
        float[] tx = new float[2];    //(s)
        float[][] txa = new float[2][n];    //(s)
        float[] vrms = new float[2]; //(m/s)
        float[][] vrmsa = new float[2][n]; //(m/s)
        float[] zu = new float[4];   //(m) 

        t0a = goTimeCalcZeroOff(th,v1,v2,zt,n);
        vrmsa = goVrmsCalc(t0a,v1,v2);
        txa = goTimeCalcNonZeroOff(t0a,vrmsa,offa);
        t0 = goTimeCalcZeroOff(th,v1,v2,zt);
        vrms = goVrmsCalc(t0,v1,v2);
        tx = goTimeCalcNonZeroOff(t0,vrms,off);
        //zu = goDepthUncertainty(t0,vrms);
        goPrint(t0,vrms);
        za = goDepthCalc(txa[0],vrmsa[0],offa);
        float z = goDepthCalc(tx[0],vrms[0],off);
        for (int i=0; i<n; ++i){
          System.out.println(za[i]);
        }
        System.out.println(z);
      }
    });
  }
}
