package seemingly.concerned.neighbors.hackathon14.drawImage;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import seemingly.concerned.neighbors.hackathon14.GeoRZA;
import seemingly.concerned.neighbors.hackathon14.R;

public class SeismicImage extends View {

	
	
	private Rect mRectBounds = new Rect();
	private Rect mLayerBounds = new Rect();
	private Paint mRectPaint;
	private Paint mLayerPaint;
	private Paint mUpperErrorBarPaint;
	private Paint mLowerErrorBarPaint;
	private int numErrorBar; // number of error bars

	//private boolean mShowText = false;
	//private float mTextWidth;
	
	// Fields that influence the view, that need to be updated based on user inputs
	private float depth;
	private float thickness;
	private float peakFreq;
	private float maxOffset;
	private int depth_Pixels;
	private int thickness_Pixels;
	private float depth_step;
	private float depth_min;
	private float depth_max;
	
	// Computation object with functions
	private GeoRZA rza;

	// Standard deviation of upper and lower surfaces
	ArrayList<ErrorBar> upperErrorBar;
	ArrayList<ErrorBar> lowerErrorBar;
	private float v1;
	private float v2;
	 
	public SeismicImage(Context context) {
		super(context);
		init();
	}
	
    /**
     * Class constructor taking a context and an attribute set. This constructor
     * is used by the layout engine to construct a {@link SeismicImage} from a set of
     * XML attributes.
     *
     * @param context
     * @param attrs   An attribute set which can contain attributes from
     *                {@link seemingly.concerned.neighbors.hackathon14.R.styleable.SeismicImage} as well as attributes inherited
     *                from {@link android.view.View}.
     */
	public SeismicImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(
	        attrs,
	        R.styleable.SeismicImage,
	        0, 0);

	   try {
	       depth = a.getFloat(R.styleable.SeismicImage_layerDepth,3500);
	       thickness = a.getFloat(R.styleable.SeismicImage_layerThickness,700);
	       v1 = a.getFloat(R.styleable.SeismicImage_v1,2000);
	       v2 = a.getFloat(R.styleable.SeismicImage_v2,2200);
	       peakFreq = a.getFloat(R.styleable.SeismicImage_layerPeakFreq,65);
	       maxOffset = a.getFloat(R.styleable.SeismicImage_layerMaxOffset,6000);    
	   } finally {
	       a.recycle();
	   }
	   init();
	}
	
//	/**
//     * Returns true if the text label should be visible.
//     *
//     * @return True if the text label should be visible, false otherwise.
//     */
//    public boolean getShowText() {
//        return mShowText;
//    }
//
//    /**
//     * Controls whether the text label is visible or not. Setting this property to
//     * false allows the pie chart graphic to take up the entire visible area of
//     * the control.
//     *
//     * @param showText true if the text label should be visible, false otherwise
//     */
//    public void setShowText(boolean showText) {
//        mShowText = showText;
//        invalidate();
//    }
    
	/**
     * 
     * @return Rect object representing the layer
     */
    public Rect getLayerBounds() {
        return mLayerBounds;
    }

    /**
     * Update the layer based on user inputs
     *
     * @param Rect - the new layer bounds
     */
    public void setLayerBounds(Rect mLayerBounds) {
        this.mLayerBounds = mLayerBounds;
        invalidate();
    }
    
	/**
     * @return Depth of the layer
     */
    public float getDepth() {
        return this.depth;
    }

    /**
     * Update the layer depth based on user inputs
     * @param float - the new layer depth, in meters
     * @return float - the depth achieved, in meters
     */
    public float setDepth(float depth) {
        this.depth = depth;
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        depth_Pixels = (int) (height*(depth-depth_min)/depth_max);
        
        if (this.depth + thickness > depth_max) {
        	depth_Pixels = height - thickness_Pixels;
        	this.depth = depth_max - thickness;
        }
        invalidate();

        return this.depth;
    }
    
	/**
     * @return Depth of the layer
     */
    public float getDepthStep() {
        return this.depth_step;
    }
    
	/**
     * @return Maximum depth where the layer can be located
     */
    public float getDepthMax() {
        return this.depth_max;
    }
    
	/**
     * @return Minimum depth where the layer can be located
     */
    public float getDepthMin() {
        return this.depth_min;
    }

    public void setDepthStep(float depth_step) {
        this.depth_step = depth_step;
    }
    
    public void setDepthMax(float depth_max) {
        this.depth_max = depth_max;
    }

	public void setDepthMin(float depth_min) {
		this.depth_min = depth_min;
	}
    
	/**
     * @return Thickness of the layer
     */
    public float getThickness() {
        return this.thickness;
    }

    /**
     * Update the layer thickness based on user inputs
     * @param float - the new layer thickness
     * @return float - the thickness achieved, in meters
     */
    public float setThickness(float thickness) {
        this.thickness = thickness;
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        thickness_Pixels = (int) (height*(thickness-depth_min)/depth_max);
        
        if (depth + this.thickness > depth_max) {
        	thickness_Pixels = height - depth_Pixels;
        	this.thickness = depth_max - depth;
        }
        invalidate();
        
        return this.thickness;
    }
    
	/**
     * @return peak frequency of the survey
     */
    public float getPeakFreq() {
        return this.peakFreq;
    }

    /**
     * Update the peak frequency based on user inputs
     * @param float - the new peak frequency
     */
    public void setPeakFreq(float peakFreq) {
        this.peakFreq = peakFreq;
        invalidate();
    }
    
	/**
     * @return maximum offset of the survey
     */
    public float getMaxOffset() {
        return this.maxOffset;
    }

    /**
     * Update the maximum offset based on user inputs
     * @param float - the new maximum offset
     */
    public void setMaxOffset(float maxOffset) {
        this.maxOffset = maxOffset;
        invalidate();
    }    
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Account for padding
        int xpad =  (getPaddingLeft() + getPaddingRight());
        int ypad =  (getPaddingTop() + getPaddingBottom());
        
        int ww = getWidth() - xpad;
        int hh = getHeight() - ypad;
        
        int depth = getPaddingTop()+depth_Pixels;
        
        mLayerBounds.set(0,0,ww-2,thickness_Pixels);
        mLayerBounds.offsetTo(getPaddingLeft()+1, depth);        
        
        // Draw the outer rectangle
        canvas.drawRect(mRectBounds, mRectPaint);
        
        // Draw the layer
        canvas.drawRect(mLayerBounds, mLayerPaint);
        
        // Update the calculation object and the arrayList of error bars
        calcSetErrorBars(ww,hh);
        
        // Draw updated errorBars
        for (int i=0; i<numErrorBar; ++i) {
        	canvas.drawLines(upperErrorBar.get(i).getLines(), mUpperErrorBarPaint);
        	canvas.drawLines(lowerErrorBar.get(i).getLines(), mLowerErrorBarPaint);
        }
    }
    
    /**
     * Update size of the view and the contained views
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Account for padding
        int xpad =  (getPaddingLeft() + getPaddingRight());
        int ypad =  (getPaddingTop() + getPaddingBottom());

        int ww = w - xpad;
        int hh =  h - ypad;
        
        // Set dimensions of shapes
        mRectBounds = new Rect(0,0,ww,hh);
        mRectBounds.offsetTo(getPaddingLeft(), getPaddingTop()); 
        
        // depth_Pixels and thickness_Pixels need to be updated in this method
        float canvasToDepthSlope = hh/(depth_max-depth_min);
        depth_Pixels = (int) (canvasToDepthSlope*depth);
        thickness_Pixels = (int) (canvasToDepthSlope*thickness);
        
        // Set updated layer bounds
        mLayerBounds = new Rect(0,0,ww-2,thickness_Pixels);
        mLayerBounds.offsetTo(getPaddingLeft()+1, getPaddingTop()+depth_Pixels);
        
        // Update the calculation object and the arrayList of error bars
        calcSetErrorBars(ww,hh);
    }

    /**
     * Update the size and location of the error bars
     * 
     * @param ww width of the drawing view, in pixels
     * @param hh height of the drawing view, in pixels
     */
	private void calcSetErrorBars(int ww, int hh) {
		// Set the calculation object with updated input parameters
        rza.setValues(this.thickness, this.v1, this.v2, this.depth, this.peakFreq, this.maxOffset);
        float[] t0 = rza.goTimeCalcZeroOff();
        float[] vrms = rza.goVrmsCalc(t0);
        float[] tx = new float[2];
        float[] delvrms = new float[2];
        float offset = 0f;
        
        for (int i=0; i<numErrorBar; ++i) {
        	offset = upperErrorBar.get(i).getOffset();
        	tx = rza.goTimeCalcNonZeroOff(t0, vrms, offset);
        	delvrms = rza.goDelVrms(tx, vrms, offset);
        	upperErrorBar.get(i).setStdDev(rza.goDepthUncertaintyTh(tx,vrms,offset,delvrms)); // function for Std Dev
        	lowerErrorBar.get(i).setStdDev(rza.goDepthUncertaintyBh(tx,vrms,offset,delvrms)); // function for Std Dev
        	upperErrorBar.get(i).setErrorBar(ww,hh,numErrorBar,i,(int)rza.goDepthUncertaintyTc(tx,vrms,offset,delvrms));
        	lowerErrorBar.get(i).setErrorBar(ww,hh,numErrorBar,i,(int)rza.goDepthUncertaintyBc(tx,vrms,offset,delvrms));
        }
	}
    
    /**
     * @author Brent Putman
     *
     * ErrorBar is a class to store the 12 data needed to draw an error bar.
     * Each error bar has three lines, and each line has 2 x,y locations. 
     * Thus, each error bar has 12 data.
     * 
     * All parts of the error bar are in terms of pixels to be drawn on the screen.
     * Inputs in meters are converted into pixels using a conversion from depth (meters) to screen height (pixels).
     */
    private class ErrorBar {
		private float[] _mErrorBarLines; 
		private float _stdDev;  // standard deviation in pixels
		private float _offset; 
		
		public ErrorBar() {
			this._mErrorBarLines = new float[3*4]; // 3 lines, 4 data per line
		}
		
		public float[] getLines() {
			return this._mErrorBarLines;
		}

		/**
		 * @param stdDev - standard deviation in meters
		 * convert standard deviation from meters to pixels
		 */
		public void setStdDev(float stdDev) {
			this._stdDev = getHeight()*((stdDev-depth_min)/depth_max);
		}
		
		public float getStdDev() {
			return this._stdDev;
		}
		
		public float getOffset() {
			return this._offset;
		}
    	
		/**
		 * Set location and size of an error bar, in pixels
		 * @param ww, width of the screen, in pixels
		 * @param hh, height of the screen, in pixels
		 * @param numErrorBar, number of error bars
		 * @param ii, error bar index
		 * @param depth_meter, depth of the error bar mean, in meters
		 */
    	private void setErrorBar(int ww, int hh, int numErrorBar, int ii, float depth_meter) {
    		// Convert from meters to pixels
    		int depth_pixel = (int)(hh*((depth_meter-depth_min)/depth_max) + getPaddingBottom());
    		int dx = ww/numErrorBar; // x separation between errorBars
    		int xStart = (int) (0.5*dx);
    		int errorWidth = (int) (0.25*dx);
    		if (errorWidth < 1) errorWidth = 1;
    		int xLoc = xStart + dx*ii;
    		
    		// Calculate the offset (in pixels) of this error bar.
    		float d_Offset = maxOffset/numErrorBar;
    		float offset_Start = d_Offset/2f;
    		float offset = offset_Start + d_Offset*ii;
    		this._offset = offset;
		    // Set upper horizontal line
		    _mErrorBarLines[0] = xLoc;
		    _mErrorBarLines[1] = depth_pixel-_stdDev;
		    _mErrorBarLines[2] = xLoc + errorWidth;
		    _mErrorBarLines[3] = depth_pixel-_stdDev;
		    // Set lower horizontal line
		    _mErrorBarLines[4] = xLoc;
	        _mErrorBarLines[5] = depth_pixel+_stdDev;
	        _mErrorBarLines[6] = xLoc + errorWidth;
	        _mErrorBarLines[7] = depth_pixel+_stdDev;
	        // Set vertical line
	        _mErrorBarLines[8] = xLoc + errorWidth/2;
	        _mErrorBarLines[9] = depth_pixel+_stdDev;
	        _mErrorBarLines[10] = xLoc + errorWidth/2;
	        _mErrorBarLines[11] = depth_pixel-_stdDev;
    	}
    }
    
    /**
     * Initialize the control. This code is in a separate method so that it can be
     * called from both constructors.
     */
    private void init() { 
    	// Set the outer rectangle's color and line width
    	mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setStyle(Style.STROKE);
        mRectPaint.setStrokeWidth(1);
    	mRectPaint.setColor(Color.BLACK);
    	
    	// Set the seismic layer's color
    	mLayerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	mLayerPaint.setStrokeWidth(0);
    	mLayerPaint.setStyle(Style.FILL);
    	mLayerPaint.setColor(Color.CYAN);
    	
    	// Create error bars
    	upperErrorBar = new ArrayList<ErrorBar>();
    	lowerErrorBar = new ArrayList<ErrorBar>();
    	
    	numErrorBar = 10;
    	for (int i=0; i<numErrorBar; ++i) {
    		upperErrorBar.add(new ErrorBar());
    		lowerErrorBar.add(new ErrorBar());
    	}
    	
    	// Set colors and line width of error bars
    	mUpperErrorBarPaint = new Paint();
    	mUpperErrorBarPaint.setStrokeWidth(2);
    	mUpperErrorBarPaint.setColor(Color.RED);
    	
    	mLowerErrorBarPaint = new Paint();
    	mLowerErrorBarPaint.setStrokeWidth(2);
    	mLowerErrorBarPaint.setColor(Color.DKGRAY);
        
        // Create GeoRZA computing object
        rza = new GeoRZA(thickness, v1, v2, depth, peakFreq, maxOffset);
    }
}
