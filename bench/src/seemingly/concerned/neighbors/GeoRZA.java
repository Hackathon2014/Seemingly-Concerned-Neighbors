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
 * @version 26.10.2014 10:32am
 */

public class GeoRZA {

  public GeoRZA(float th, float v1, float v2, float zt, float freq, 
      float offset) {
    this.th = th;
    this.v1 = v1;
    this.v2 = v2;
    this.zt = zt;
    this.freq = freq;
    this.offset = offset;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * zero-offset source-receiver geometry.
   * @return array[2] of t01 and t02 (top and bottom times resepctively)
   */
  private float[] goTimeCalcZeroOff() {
    float[] t0 = new float[2];
    t0[0] = (2.0f*zt)/v1;
    t0[1] = t0[0] + th/v2;
    return t0;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * zero-offset source-receiver geometry.
   * @param n number of offset values
   * @return array[2][n] of t01 and t02 (top and bottom times resepctively)
   */
  private float[][] goTimeCalcZeroOff(int n) {
    float[][] t0 = new float[n][2];
    for (int i=0; i<n; ++i) {
      t0[i] = goTimeCalcZeroOff();
    }
    return t0;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * a specified offset source-receiver geometry.
   * @param t0 the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param offset offset value between source and reciever
   * @return array[2] of tx1 and tx2 (top and bottom times resepctively)
   */
  private float[] goTimeCalcNonZeroOff(float[] t0, float[] vrms, 
      float offset) {
    float[] tx = new float[2];
    tx[0] = sqrt(t0[0]*t0[0] + (offset/vrms[0])*(offset/vrms[0]));
    tx[1] = sqrt(t0[1]*t0[1] + (offset/vrms[1])*(offset/vrms[1]));
    return tx;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * a specified offset source-receiver geometry.
   * @param t0 the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param offset offset value between source and reciever
   * @return array[2][#off] of tx1 and tx2 (top and bottom times resepctively)
   */
  private float[][] goTimeCalcNonZeroOff(float[][] t0, float[][] vrms, 
      float[] offset) {
    int n = offset.length;
    float[][] tx = new float[n][2];
    for (int i=0; i<n; ++i) {
      tx[i] = goTimeCalcNonZeroOff(t0[i],vrms[i],offset[i]); 
    }
    return tx;
  }

  /**
   * Calculates the RMS velocities of the layer.
   * @param t the times for the top and bottom of layer
   * @return array[2] of RMS velocities
   */
  private float[] goVrmsCalc(float[] t) {
    float[] vrms = new float[2];
    vrms[0] = v1;
    vrms[1] = sqrt((v1*v1*t[0] + v2*v2*t[1])/(t[0] + t[1]));
    return vrms;
  }

  /**
   * Calculates the RMS velocities of the layer.
   * @param t the times for the top and bottom of layer
   * @return array[2][#off] of RMS velocities
   */
  private float[][] goVrmsCalc(float[][] t) {
    int n = t.length;
    float[][] vrms = new float[n][2];
    for (int i=0; i<n; ++i) {
      vrms[i] = goVrmsCalc(t[i]);
    }
    return vrms;
  }

  /**
   * Calculates the delta RMS velocities.
   * @param t the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param offset offset value between source and reciever
   * @return array[2] delta RMS velocities
   */
  private float[] goDelVrms(float[] t, float[] vrms, float offset) {
    float[] delvrms = new float[2];
    delvrms[0] = A[0]*(t[0]*pow(vrms[0],3))/(freq*offset*offset);
    delvrms[1] = A[1]*(t[1]*pow(vrms[1],3))/(freq*offset*offset);
    return delvrms;
  }

  /**
   * Calculates the delta RMS velocities.
   * @param t the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param offset offset value between source and reciever
   * @return array[2][#off] delta RMS velocities
   */
  private float[][] goDelVrms(float[][] t, float[][] vrms, 
      float[] offset) {
    int n = offset.length;
    float[][] delvrms = new float[n][2];
    for (int i=0; i<n; ++i) {
      delvrms[i] = goDelVrms(t[i],vrms[i],offset[i]);
    }
    return delvrms;
  }

  /**
   * Calculates the uncertainty in the depth of the top of layer.
   * @param t times for the top of layer
   * @param vrms RMS velocities 
   * @param offset offset value between source and reciever
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in top depth values (plus/minus)
   */
  private float[] goDepthUncertaintyT(float[] t, float[] vrms, 
      float offset, float[] delvrms) {
    float[] zt = new float[2];
    float ztp, ztm;
    float r = (t[0]*vrms[0])/2.0f;
    float theta = asin(offset/(2.0f*r));
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
   * @param offset offset value between source and reciever
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in top depth values (plus/minus)
   */
  private float[][] goDepthUncertaintyT(float[][] t, float[][] vrms, 
      float offset[], float[][] delvrms) {
    int n = offset.length;
    float[][] zt = new float[n][2];
    for (int i=0; i<n; ++i) {
      zt[i] = goDepthUncertaintyT(t[i],vrms[i],offset[i],delvrms[i]);
    }
    return zt;
  }

  /**
   * Calculates the uncertainty in the depth of the bottom of layer.
   * @param t times for the bottom of layer
   * @param vrms RMS velocities 
   * @param offset offset value between source and reciever
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in bottom depth values (plus/minus)
   */
  private float[] goDepthUncertaintyB(float[] t, float[] vrms, 
      float offset, float[] delvrms) {
    float[] zb = new float[2];
    float zbp, zbm;
    float r = (t[1]*vrms[1])/2.0f;
    float theta = asin(offset/(2.0f*r));
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
   * @param offset offset value between source and reciever
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in bottom depth values (plus/minus)
   */
  private float[][] goDepthUncertaintyB(float[][] t, float[][] vrms, 
      float[] offset, float[][] delvrms) {
    int n = offset.length;
    float[][] zb = new float[n][2];
    for (int i=0; i<n; ++i) {
      zb[i] = goDepthUncertaintyB(t[i],vrms[i],offset[i],delvrms[i]);
    }
    return zb;
  }

  /******************************TESTING*********************************/
  private float goDepthCalc(float[] tx, float[] vrms, float offset) {
    float r = (tx[0]*vrms[0])/2.0f;
    float theta = asin(offset/(2.0f*r));
    float z = cos(theta)*tx[0]*vrms[0]/2.0f;
    return z;
  }

  private float[] goDepthCalc(float[][] tx, float[][] vrms, 
      float[] offset) {
    int n = offset.length;
    float[] z = new float[n];
    for (int i=0; i<n; ++i) {
      z[i] = goDepthCalc(tx[i],vrms[i],offset[i]);
    }
    return z;
  }

  private void goPrint(float[] t, float[] vrms) {
    System.out.println("The time to the top layer is "+t[0]);
    System.out.println("The time to the bottom layer is "+t[1]);
    System.out.println("The RMS velocity 1 is "+vrms[0]);
    System.out.println("The RMS velocity 2 is "+vrms[1]);
  }

  /****************************PRIVATE*********************************/
  private float th,v1,v2,zt,freq,offset;
  private float[] A = {4.0f,4.0f}; //proportionality constant

  /************************MAIN METHOD**********************************/
  public static void main(String[] args) {
    float th = 70.0f;              //(m)
    float v1 = 2000.0f;            //(m/s) 
    float v2 = 2200.0f;            //(m/s) 
    float zt = 500.0f;             //(m)
    float offset = 300.0f;         //(m)
    float freq = 25.0f;            //(Hz)

    int noff = 300;                //# of offsets/ offset value in (m)
    float[] offa = new float[noff];//(m)
    float[] za = new float[noff];  //(m)
    for (int i=0; i<noff; ++i) {
      offa[i] = i;
    }

    GeoRZA grza = new GeoRZA(th,v1,v2,zt,freq,offset);

    float[] t0 = new float[2];           //(s)
    float[] tx = new float[2];           //(s)
    float[] vrms = new float[2];         //(m/s)
    float[] delvrms = new float[2];      //(m/s)
    float[] zut = new float[2];          //(m)
    float[] zub = new float[2];          //(m)

    float[][] t0a = new float[noff][2];     //(s)
    float[][] txa = new float[noff][2];     //(s)
    float[][] vrmsa = new float[noff][2];   //(m/s)
    float[][] delvrmsa = new float[noff][2];//(m/s)
    float[][] zuta = new float[noff][2];    //(m)
    float[][] zuba = new float[noff][2];    //(m)


    // Calculations using a single offset value
    t0 = grza.goTimeCalcZeroOff();
    vrms = grza.goVrmsCalc(t0);
    tx = grza.goTimeCalcNonZeroOff(t0,vrms,offset);
    delvrms = grza.goDelVrms(tx,vrms,offset);
    zut = grza.goDepthUncertaintyT(tx,vrms,offset,delvrms);
    zub = grza.goDepthUncertaintyB(tx,vrms,offset,delvrms);
    // Calculations using an array of offset values
    t0a = grza.goTimeCalcZeroOff(noff);
    vrmsa = grza.goVrmsCalc(t0a);
    txa = grza.goTimeCalcNonZeroOff(t0a,vrmsa,offa);
    delvrmsa = grza.goDelVrms(txa,vrmsa,offa);
    zuta = grza.goDepthUncertaintyT(txa,vrmsa,offa,delvrmsa);
    zuba = grza.goDepthUncertaintyB(txa,vrmsa,offa,delvrmsa);

    float z = grza.goDepthCalc(tx,vrms,offset);
    za = grza.goDepthCalc(txa,vrmsa,offa);
    for (int i=0; i<noff; ++i){
      //System.out.println(za[i]);
      System.out.println(grza.th);
    }
    System.out.println(z);
    grza.goPrint(t0,vrms);
  }
}
