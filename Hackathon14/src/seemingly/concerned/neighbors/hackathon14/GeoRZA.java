package seemingly.concerned.neighbors.hackathon14;

import java.io.*;
import java.nio.*;


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
 * @version 26.10.2014 12:14pm
 */

public class GeoRZA {

  public GeoRZA(float th, float v1, float v2, float zt, float freq, 
      float offset) {
    _th = th;
    _v1 = v1;
    _v2 = v2;
    _zt = zt;
    _freq = freq;
    _offset = offset;
  }

  /**
   * Set method to change the acquisition parameters for your GeoRZA object.
   * @param th thickness of the bed
   * @param v1 stacking velocity above layer
   * @param v2 velocity of the layer
   * @param zt depth to the top of the layer
   * @param freq peak frequency of the source
   * @param offset offset value between source and reciever
   */
  public void setValues(float th, float v1, float v2, float zt, float freq, 
      float offset) {
    _th = th;
    _v1 = v1;
    _v2 = v2;
    _zt = zt;
    _freq = freq;
    _offset = offset;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * zero-offset source-receiver geometry.
   * @return array[2] of t01 and t02 (top and bottom times resepctively)
   */
  public float[] goTimeCalcZeroOff() {
    float[] t0 = new float[2];
    t0[0] = (2.0f*_zt)/_v1;
    t0[1] = t0[0] + 2.0f*_th/_v2;
    return t0;
  }

  /**
   * Calculates the times to the top and bottom of a subsurface layer for
   * zero-offset source-receiver geometry.
   * @param n number of offset values
   * @return array[2][n] of t01 and t02 (top and bottom times resepctively)
   */
  public float[][] goTimeCalcZeroOff(int n) {
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
  public float[] goTimeCalcNonZeroOff(float[] t0, float[] vrms, 
      float offset) {
    float[] tx = new float[2];
    tx[0] = (float) Math.sqrt(t0[0]*t0[0] + (offset/vrms[0])*(offset/vrms[0]));
    tx[1] = (float) Math.sqrt(t0[1]*t0[1] + (offset/vrms[1])*(offset/vrms[1]));
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
  public float[][] goTimeCalcNonZeroOff(float[][] t0, float[][] vrms, 
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
  public float[] goVrmsCalc(float[] t) {
    float[] vrms = new float[2];
    vrms[0] = _v1;
    vrms[1] = (float)Math.sqrt((_v1*_v1*t[0] + _v2*_v2*t[1])/(t[0] + t[1]));
    return vrms;
  }

  /**
   * Calculates the RMS velocities of the layer.
   * @param t the times for the top and bottom of layer
   * @return array[2][#off] of RMS velocities
   */
  public float[][] goVrmsCalc(float[][] t) {
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
  public float[] goDelVrms(float[] t, float[] vrms, float offset) {
    float[] delvrms = new float[2];
    delvrms[0] = (float)(_A[0]*(t[0]*Math.pow(vrms[0],3))/(_freq*offset*offset));
    delvrms[1] = (float)(_A[1]*(t[1]*Math.pow(vrms[1],3))/(_freq*offset*offset));
    return delvrms;
  }

  /**
   * Calculates the delta RMS velocities.
   * @param t the times for the top and bottom of layer
   * @param vrms the RMS velocities 
   * @param offset offset value between source and reciever
   * @return array[2][#off] delta RMS velocities
   */
  public float[][] goDelVrms(float[][] t, float[][] vrms, 
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
  public float goDepthUncertaintyTh(float[] t, float[] vrms, 
      float offset, float[] delvrms) {
    float ztp, ztm;
    float r = (t[0]*vrms[0])/2.0f;
    float theta = (float) Math.asin(offset/(2.0f*r));
    ztp = (float) ((Math.cos(theta)*t[0]/2.0f)*(delvrms[0]));
    ztm = (float) ((Math.cos(theta)*t[0]/2.0f)*(delvrms[0]));
    return ztp;
  }

  /**
   * Calculates the uncertainty in the depth of the top of layer.
   * @param t times for the top of layer
   * @param vrms RMS velocities 
   * @param offset offset value between source and reciever
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in top depth values (plus/minus)
   */
  public float goDepthUncertaintyTc(float[] t, float[] vrms, 
      float offset, float[] delvrms) {
    float ztp, ztm;
    float r = (t[0]*vrms[0])/2.0f;
    float theta = (float) Math.asin(offset/(2.0f*r));
    ztp = (float) ((Math.cos(theta)*t[0]/2.0f)*(vrms[0]));
    ztm = (float) ((Math.cos(theta)*t[0]/2.0f)*(vrms[0]));
    return ztp;
  }

  /**
   * Calculates the uncertainty in the depth of the top of layer.
   * @param t times for the top of layer
   * @param vrms RMS velocities 
   * @param offset offset value between source and reciever
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in top depth values (plus/minus)
   */
  public float[] goDepthUncertaintyT(float[][] t, float[][] vrms, 
      float offset[], float[][] delvrms) {
    int n = offset.length;
    float[] zt = new float[n];
    for (int i=0; i<n; ++i) {
      zt[i] = goDepthUncertaintyTh(t[i],vrms[i],offset[i],delvrms[i]);
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
  public float goDepthUncertaintyBh(float[] t, float[] vrms, 
      float offset, float[] delvrms) {
    float zbp, zbm;
    float r = (t[1]*vrms[1])/2.0f;
    float theta = (float) Math.asin(offset/(2.0f*r));
    zbp = (float) ((Math.cos(theta)*t[1]/2.0f)*(delvrms[1]));
    zbm = (float) ((Math.cos(theta)*t[1]/2.0f)*(delvrms[1]));
    return zbp;
  }

  /**
   * Calculates the uncertainty in the depth of the bottom of layer.
   * @param t times for the bottom of layer
   * @param vrms RMS velocities 
   * @param offset offset value between source and reciever
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in bottom depth values (plus/minus)
   */
  public float goDepthUncertaintyBc(float[] t, float[] vrms, 
      float offset, float[] delvrms) {
    float zbp, zbm;
    float r = (t[1]*vrms[1])/2.0f;
    float theta = (float) Math.asin(offset/(2.0f*r));
    zbp = (float) ((Math.cos(theta)*t[1]/2.0f)*(vrms[1]));
    zbm = (float) ((Math.cos(theta)*t[1]/2.0f)*(vrms[1]));
    return zbp;
  }

  /**
   * Calculates the uncertainty in the depth of the bottom of layer.
   * @param t times for the bottom of layer
   * @param vrms RMS velocities 
   * @param offset offset value between source and reciever
   * @param delvrms delta RMS velocities
   * @return array[2] the uncertainties in bottom depth values (plus/minus)
   */
  public float[] goDepthUncertaintyB(float[][] t, float[][] vrms, 
      float[] offset, float[][] delvrms) {
    int n = offset.length;
    float[] zb = new float[n];
    for (int i=0; i<n; ++i) {
      zb[i] = goDepthUncertaintyBh(t[i],vrms[i],offset[i],delvrms[i]);
    }
    return zb;
  }

  /******************************TESTING*********************************/
  private float goDepthCalc(float[] tx, float[] vrms, float offset) {
    float r = (tx[0]*vrms[0])/2.0f;
    float theta = (float) Math.asin(offset/(2.0f*r));
    float z = (float) (Math.cos(theta)*tx[0]*vrms[0]/2.0f);
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
  private float _th,_v1,_v2,_zt,_freq,_offset;
  private float[] _A = {4.0f,4.0f}; //proportionality constant

  /************************MAIN METHOD**********************************/
  public static void main(String[] args) {
    float th = 1100.0f;              //(m)
    float v1 = 2000.0f;            //(m/s) 
    float v2 = 2200.0f;            //(m/s) 
    float zt = 500.0f;             //(m)
    float offset = 8000.0f;         //(m)
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

    float[][] t0a = new float[noff][2];     //(s)
    float[][] txa = new float[noff][2];     //(s)
    float[][] vrmsa = new float[noff][2];   //(m/s)
    float[][] delvrmsa = new float[noff][2];//(m/s)
    float[] zuta = new float[noff];    //(m)
    float[] zuba = new float[noff];    //(m)


    // Calculations using a single offset value
    t0 = grza.goTimeCalcZeroOff();
    vrms = grza.goVrmsCalc(t0);
    tx = grza.goTimeCalcNonZeroOff(t0,vrms,offset);
    delvrms = grza.goDelVrms(tx,vrms,offset);
    float zut = grza.goDepthUncertaintyTh(tx,vrms,offset,delvrms);
    float zub = grza.goDepthUncertaintyBh(tx,vrms,offset,delvrms);
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
      //System.out.println(grza._A[0]);
    }
    System.out.println(z);
    System.out.println("zut "+zut);
    System.out.println("zub "+zub);
    grza.goPrint(t0,vrms);
  }
}
