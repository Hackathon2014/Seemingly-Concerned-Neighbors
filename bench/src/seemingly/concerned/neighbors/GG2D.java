package seemingly.concerned.neighbors;

import edu.mines.jtk.io.*;
import edu.mines.jtk.lapack.*;

import java.io.*;
import java.nio.*;
import javax.swing.*;

import static edu.mines.jtk.util.ArrayMath.*;

/**
 * @author Elias Arias, Colorado School of Mines CWP
 * This software is a Java adaptation of code originally written by Joe 
 * Capriotti in the MATLAB programming language with modifications to the code
 * being written by Thomas Rapstine, also in MATLAB. 
 *
 *
 * @version 26.10.2014 x:xxam
 */

public class GG2D {

  private static void ggCube(x1,x2,z1,z2) {

  }

  private static void dxxnd(x,z) {

  }

  private int i1(int index) {
		return node % n1som;
	}

	private int i2(int index) {
		return node / n1som;
	}
  
  public int linIndex(int i1, int i2) {
		return i1 + i2*n1som;
	}  

  public int linIndex(int i1, int i2, int i3) {
		return i1 + i2*n1som + i3*n2som*n1som;
	}
  
  /*******************************PRIVATE******************************/

  private int n, nx, nz;

  /************************MAIN METHOD**********************************/
    public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        
      }
    });
  }

}
