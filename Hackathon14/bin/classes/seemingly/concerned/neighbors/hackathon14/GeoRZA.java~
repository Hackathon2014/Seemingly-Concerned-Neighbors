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
 * @version 25.10.2014 4:57pm
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
    float[][] t0 = new float[n][2];
    for (int i=0; i<n; ++i) {
      t0[i] = goTimeCalcZeroOff(th,v1,v2,zt);
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
    float[][] tx = new float[n][2];
    System.out.println("vrms lengths "+vrms.length+" and "+vrms[0].length);
    System.out.println("t0 lengths "+t0.length+" and "+t0[0].length);
    System.out.println("off length "+off.length);
    for (int i=0; i<n; ++i) {
      tx[i] = goTimeCalcNonZeroOff(t0[i],vrms[i],off[i]); 
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
    int n = t.length;
    float[][] vrms = new float[n][2];
    for (int i=0; i<n; ++i) {
      vrms[i] = goVrmsCalc(t[i],v1,v2);
    }
    return vrms;
  }

  /**
   * Calculates the delta RMS velocities.
   * @param t the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param off offset between source and receiver
   * @param freq the peak frequency of the source
   * @param A proportionality constants
   * @return array[2] delta RMS velocities
   */
  private static float[] goDelVrms(float[] t, float[] vrms, float off, 
      float freq, float[] A) {
    float[] delvrms = new float[2];
    delvrms[0] = A[0]*(t[0]*pow(vrms[0],3))/(freq*off*off);
    delvrms[1] = A[1]*(t[1]*pow(vrms[1],3))/(freq*off*off);
    return delvrms;
  }

  /**
   * Calculates the delta RMS velocities.
   * @param t the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param off offset between source and receiver
   * @param freq the peak frequency of the source
   * @param A proportionality constants
   * @return array[2][#off] delta RMS velocities
   */
  private static float[][] goDelVrms(float[][] t, float[][] vrms, float[] off,
      float freq, float[] A) {
    int n = off.length;
    float[][] delvrms = new float[n][2];
    for (int i=0; i<n; ++i) {
      delvrms[i] = goDelVrms(t[i],vrms[i],off[i],freq,A);
    }
    return delvrms;
  }

  /**
   * Calculates the uncertainty in the depth of the top of layer.
   * @param t times for the top of layer
   * @param vrms RMS velocities 
   * @param off offset between source and receiver
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in top depth values (plus/minus)
   */
  private static float[] goDepthUncertaintyT(float[] t, float[] vrms, 
      float off, float[] delvrms) {
    float[] zt = new float[2];
    float ztp, ztm;
    float r = (t[0]*vrms[0])/2.0f;
    float theta = asin(off/(2.0f*r));
    ztp = (cos(theta)*t[0]/2.0f)*(vrms[0]+delvrms[0]);
    ztm = (cos(theta)*t[0]/2.0f)*(vrms[0]-delvrms[0]);
    zt[0] = ztp;
    zt[1] = ztm;
    return zt;
  }

  /**
   * Calculates the uncertainty in the depth of the top of layer.
   * @param t times for the top of layer
   * @param vrms RMS velocities 
   * @param off offset between source and receiver
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in top depth values (plus/minus)
   */
  private static float[][] goDepthUncertaintyT(float[][] t, float[][] vrms, 
      float off[], float[][] delvrms) {
    int n = off.length;
    float[][] zt = new float[n][2];
    for (int i=0; i<n; ++i) {
      zt[i] = goDepthUncertaintyT(t[i],vrms[i],off[i],delvrms[i]);
    }
    return zt;
  }

  /**
   * Calculates the uncertainty in the depth of the bottom of layer.
   * @param t times for the bottom of layer
   * @param vrms RMS velocities 
   * @param off offset between source and receiver
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in bottom depth values (plus/minus)
   */
  private static float[] goDepthUncertaintyB(float[] t, float[] vrms, 
      float off, float[] delvrms) {
    float[] zb = new float[2];
    float zbp, zbm;
    float r = (t[1]*vrms[1])/2.0f;
    float theta = asin(off/(2.0f*r));
    zbp = (cos(theta)*t[1]/2.0f)*(vrms[1]+delvrms[1]);
    zbm = (cos(theta)*t[1]/2.0f)*(vrms[1]-delvrms[1]);
    zb[0] = zbp;
    zb[1] = zbm;
    return zb;
  }

  /**
   * Calculates the uncertainty in the depth of the bottom of layer.
   * @param t times for the bottom of layer
   * @param vrms RMS velocities 
   * @param off offset between source and receiver
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in bottom depth values (plus/minus)
   */
  private static float[][] goDepthUncertaintyB(float[][] t, float[][] vrms, 
      float[] off, float[][] delvrms) {
    int n = off.length;
    float[][] zb = new float[n][2];
    for (int i=0; i<n; ++i) {
      zb[i] = goDepthUncertaintyB(t[i],vrms[i],off[i],delvrms[i]);
    }
    return zb;
  }

  /**
   *********************************************************************** 
   * TESTING.
   */
  private static float goDepthCalc(float[] tx, float[] vrms, float off) {
    float r = (tx[0]*vrms[0])/2.0f;
    float theta = asin(off/(2.0f*r));
    float z = cos(theta)*tx[0]*vrms[0]/2.0f;
    return z;
  }

  private static float[] goDepthCalc(float[][] tx, float[][] vrms, 
      float[] off) {
    int n = off.length;
    float[] z = new float[n];
    for (int i=0; i<n; ++i) {
      z[i] = goDepthCalc(tx[i],vrms[i],off[i]);
    }
    return z;
  }

  private static void goPrint(float[] t, float[] vrms) {
    System.out.println("The time to the top layer is "+t[0]);
    System.out.println("The time to the bottom layer is "+t[1]);
    System.out.println("The RMS velocity 1 is "+vrms[0]);
    System.out.println("The RMS velocity 2 is "+vrms[1]);
  }

  /************************MAIN METHOD**********************************/
    public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        int n = 300;                //# of offsets/ offset value in (m)
        float th = 70.0f;           //(m)
        float v1 = 2000.0f;         //(m/s) 
        float v2 = 2200.0f;         //(m/s) 
        float zt = 500.0f;          //(m)
        float freq = 25.0f;         //(Hz)
        float[] offa = new float[n];//(m)
        float[] za = new float[n];  //(m)
        for (int i=0; i<n; ++i) {
          offa[i] = i;
        }

        float[] t0 = new float[2];           //(s)
        float[][] t0a = new float[n][2];     //(s)
        float[] tx = new float[2];           //(s)
        float[][] txa = new float[n][2];     //(s)
        float[] vrms = new float[2];         //(m/s)
        float[][] vrmsa = new float[n][2];   //(m/s)
        float[] delvrms = new float[2];      //(m/s)
        float[][] delvrmsa = new float[n][2];//(m/s)
        float[] zut = new float[2];          //(m)
        float[][] zuta = new float[n][2];    //(m)
        float[] zub = new float[2];          //(m)
        float[][] zuba = new float[n][2];    //(m)
        float[] A = {2.0f,2.0f};

        // Calculations using a single offset value
        t0 = goTimeCalcZeroOff(th,v1,v2,zt);
        vrms = goVrmsCalc(t0,v1,v2);
        tx = goTimeCalcNonZeroOff(t0,vrms,n);
        delvrms = goDelVrms(tx,vrms,n,freq,A);
        zut = goDepthUncertaintyT(tx,vrms,n,delvrms);
        zub = goDepthUncertaintyB(tx,vrms,n,delvrms);
        // Calculations using an array of offset values
        t0a = goTimeCalcZeroOff(th,v1,v2,zt,n);
        vrmsa = goVrmsCalc(t0a,v1,v2);
        txa = goTimeCalcNonZeroOff(t0a,vrmsa,offa);
        delvrmsa = goDelVrms(txa,vrmsa,offa,freq,A);
        zuta = goDepthUncertaintyT(txa,vrmsa,offa,delvrmsa);
        zuba = goDepthUncertaintyB(txa,vrmsa,offa,delvrmsa);

        float z = goDepthCalc(tx,vrms,n);
        za = goDepthCalc(txa,vrmsa,offa);
        for (int i=0; i<n; ++i){
          System.out.println(za[i]);
        }
        System.out.println(z);
        goPrint(t0,vrms);
      }
    });
  }
}
