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
	

	
	
    /**
     * Draw text to the left of the image
     */
    public static final int TEXTPOS_LEFT = 0;
    
    /**
     * Interface definition for a callback to be invoked when the current
     * item changes.
     */
    public interface VariableChangeListener {
        void setOnVariableChange(float depth);
    }
    
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
	       thickness = a.getFloat(R.styleable.SeismicImage_layerThickness,500);
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
     * @param float - the new layer depth
     * @return int - the depth achieved, in meters
     */
    public int setDepth(float depth) {
        this.depth = depth;
        int height = getHeight();
        depth_Pixels = (int) (height*(depth-depth_min)/depth_max);
        depth = getPaddingTop()+depth_Pixels;
        
        if (depth+thickness_Pixels > height) {
        	depth_Pixels = (int) (height - thickness_Pixels);
        	this.depth = ((depth_Pixels*depth_max)/height) + depth_min;
        }
        invalidate();
        
        //rza.setDepth(this.depth);
        
        return (int) this.depth;
    }
    
	/**
     * @return Depth of the layer
     */
    public float getDepthStep() {
        return this.depth_step;
    }

    /**
     * Update the layer depth based on user inputs
     *
     * @param float - the new layer depth
     */
    public void setDepthStep(float depth_step) {
        this.depth_step = depth_step;
        invalidate();
    }
    
	/**
     * @return Minimum depth where the layer can be located
     */
    public float getDepthMin() {
        return this.depth_min;
    }

    /**
     * Update the Minimum depth where the layer can be located based on user input
     *
     * @param float - the new layer minimum layer depth
     */
    public void setDepthMin(float depth_min) {
        this.depth_min = depth_min;
        invalidate();
    }
    
	/**
     * @return Maximum depth where the layer can be located
     */
    public float getDepthMax() {
        return this.depth_max;
    }

    /**
     * Update the Minimum depth where the layer can be located based on user input
     *
     * @param float - the new layer minimum layer depth
     */
    public void setDepthMax(float depth_max) {
        this.depth_max = depth_max;
        invalidate();
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
     * 
     * @return int - the thickness achieved, in meters
     */
    public int setThickness(float thickness) {
        this.thickness = thickness;
        int height = getHeight();
        int depth = getPaddingTop()+depth_Pixels;
        thickness_Pixels = (int) (height*(thickness-depth_min)/depth_max);
        if (depth+thickness_Pixels > height) {
        	thickness_Pixels = height - depth;
        	this.thickness = (((thickness_Pixels*depth_max)/height) + depth_min);
        }
        invalidate();
        
        //rza.setThickness(this.thickness);
        
        return (int) this.thickness;
    }
    
	/**
     * @return peak frequency of the survey
     */
    public float getPeakFreq() {
        return this.peakFreq;
    }

    /**
     * Update the peak frequency based on user inputs
     *
     * @param float - the new peak frequency
     */
    public void setPeakFreq(float peakFreq) {
        this.peakFreq = peakFreq;
        //rza.setPeakFreq(this.peakFreq);
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
     *
     * @param float - the new maximum offset
     */
    public void setMaxOffset(float maxOffset) {
        this.maxOffset = maxOffset;
        //rza.setMaxOffset();
        invalidate();
    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        // Do nothing. Do not call the superclass method--that would start a layout pass
//        // on this view's children. PieChart lays out its children in onSizeChanged().
//    }

//    /**
//     * Register a callback to be invoked when the depth variable changes.
//     *
//     * @param listener Can be null.
//     *                 The variable change listener to attach to this view.
//     */
//    public void setOnVariableChangeListener(VariableChangeListener listener) {
//        mVariableChangedListener = listener;
//    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Account for padding
        int xpad =  (getPaddingLeft() + getPaddingRight());
        int ypad =  (getPaddingTop() + getPaddingBottom());

        int ww = getWidth() - xpad;
        int hh =  getHeight() - ypad;
        
        int depth = getPaddingTop()+depth_Pixels;
        if (depth+thickness_Pixels > getHeight()) depth = getHeight() - thickness_Pixels;
        
        mLayerBounds.set(0,0,ww-2,thickness_Pixels);
        mLayerBounds.offsetTo(getPaddingLeft()+1, depth);        
        
        // Draw the outer rectangle
        canvas.drawRect(mRectBounds, mRectPaint);
        
        // Draw the layer
        canvas.drawRect(mLayerBounds, mLayerPaint);
        
        // Update the calculation object with new inputs
        rza.setValues(this.thickness, this.v1, this.v2, this.depth, this.peakFreq, this.maxOffset);
        float[] t0 = rza.goTimeCalcZeroOff();
        float[] vrms = rza.goVrmsCalc(t0);
        float[] tx = new float[2];
        float[] delvrms = new float[2];
        float offset = 0f;
        
        // Set updated errorBars
        for (int i=0; i<numErrorBar; ++i) {
        	
        	offset = upperErrorBar.get(i).getOffset();
        	tx = rza.goTimeCalcNonZeroOff(t0, vrms, offset);
        	delvrms = rza.goDelVrms(tx, vrms, offset);
        	upperErrorBar.get(i).setStdDev(rza.goDepthUncertaintyTh(tx,vrms,offset,delvrms)); // insert function for Std Dev
        	lowerErrorBar.get(i).setStdDev(rza.goDepthUncertaintyBh(tx,vrms,offset,delvrms)); // insert function for Std Dev
        	upperErrorBar.get(i).setErrorBar(ww,numErrorBar,i,(int)rza.goDepthUncertaintyTc(tx,vrms,offset,delvrms));
        	lowerErrorBar.get(i).setErrorBar(ww,numErrorBar,i,(int)rza.goDepthUncertaintyBc(tx,vrms,offset,delvrms));
        	//upperErrorBar.get(i).setStdDev(10); // insert function for Std Dev
        	//lowerErrorBar.get(i).setStdDev(20); // insert function for Std Dev

        	canvas.drawLines(upperErrorBar.get(i).getLines(), mUpperErrorBarPaint);
        	canvas.drawLines(lowerErrorBar.get(i).getLines(), mLowerErrorBarPaint);
        }
    }
    
    /**
     * Update size of the view and the contained views
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Set dimensions for text, pie chart, etc

        // Account for padding
        int xpad =  (getPaddingLeft() + getPaddingRight());
        int ypad =  (getPaddingTop() + getPaddingBottom());

        int ww = w - xpad;
        int hh =  h - ypad;
        
        mRectBounds = new Rect(0,0,ww,hh);
        mRectBounds.offsetTo(getPaddingLeft(), getPaddingTop()); 
        
        // depth_Pixels and thickness_Pixels need to be updated in this method
        float canvasToDepthSlope = hh/(depth_max-depth_min);
        depth_Pixels = (int) (canvasToDepthSlope*depth);
        thickness_Pixels = (int) (canvasToDepthSlope*thickness);
        
        // Set updated layer bounds
        mLayerBounds = new Rect(0,0,ww-2,thickness_Pixels);
        mLayerBounds.offsetTo(getPaddingLeft()+1, getPaddingTop()+depth_Pixels);
        
        // Update the calculation object with new inputs
        rza.setValues(this.thickness, this.v1, this.v2, this.depth, this.peakFreq, this.maxOffset);
        float[] t0 = rza.goTimeCalcZeroOff();
        float[] vrms = rza.goVrmsCalc(t0);
        float[] tx = new float[2];
        float[] delvrms = new float[2];
        float offset = 0f;
        
        // Set updated errorBars
        int upperSurface = getPaddingTop()+depth_Pixels;
        int lowerSurface = getPaddingTop()+depth_Pixels+thickness_Pixels;
        for (int i=0; i<numErrorBar; ++i) {
        	
        	offset = upperErrorBar.get(i).getOffset();
        	tx = rza.goTimeCalcNonZeroOff(t0, vrms, offset);
        	delvrms = rza.goDelVrms(tx, vrms, offset);
        	upperErrorBar.get(i).setStdDev(rza.goDepthUncertaintyTh(tx,vrms,offset,delvrms)); // insert function for Std Dev
        	lowerErrorBar.get(i).setStdDev(rza.goDepthUncertaintyBh(tx,vrms,offset,delvrms)); // insert function for Std Dev
        	upperErrorBar.get(i).setErrorBar(ww,numErrorBar,i,(int)rza.goDepthUncertaintyTc(tx,vrms,offset,delvrms));
        	lowerErrorBar.get(i).setErrorBar(ww,numErrorBar,i,(int)rza.goDepthUncertaintyBc(tx,vrms,offset,delvrms));
        	//upperErrorBar.get(i).setStdDev(10); // insert function for Std Dev
        	//lowerErrorBar.get(i).setStdDev(20); // insert function for Std Dev
        }
    }

    private class ErrorBar {
		private float[] mErrorBarLines; 
		private float stdDev;
		private float offset;
		
		public ErrorBar() {
			this.mErrorBarLines = new float[3*4]; // 3 lines
		}
		
		public float[] getLines() {
			return this.mErrorBarLines;
		}

		/*
		 * stdDev - meters
		 * convert to Pixels and store
		 */
		public void setStdDev(float stdDev) {
			this.stdDev = getHeight()*((stdDev-depth_min)/depth_max);
		}
		
		public float getStdDev() {
			
			return this.stdDev;
		}
		
		public float getOffset() {
			return this.offset;
		}
    	
    	private void setErrorBar(int ww, int n, int i, float surface1) {
    		int surface = (int)(getHeight()*((surface1-depth_min)/depth_max)+getPaddingTop());
    		int dx = ww/n; // x separation between errorBars
    		int xStart = (int) (0.5*dx);
    		int errorWidth = (int) (0.25*dx);
    		int xLoc = xStart + dx*i;
    		
    		float d_Offset = maxOffset/n;
    		float offset_Start = d_Offset/2f;
    		float offset = offset_Start + d_Offset*i;
    		this.offset = offset;
		    //Surface upper horizontal line
		    mErrorBarLines[0] = xLoc;
		    mErrorBarLines[1] = surface-stdDev/2;
		    mErrorBarLines[2] = xLoc + errorWidth;
		    mErrorBarLines[3] = surface-stdDev/2;
		    //Surface lower horizontal line
		    mErrorBarLines[4] = xLoc;
	        mErrorBarLines[5] = surface+stdDev/2;
	        mErrorBarLines[6] = xLoc + errorWidth;
	        mErrorBarLines[7] = surface+stdDev/2;
	        //Surface vertical line
	        mErrorBarLines[8] = xLoc + errorWidth/2;
	        mErrorBarLines[9] = surface+stdDev/2;
	        mErrorBarLines[10] = xLoc + errorWidth/2;
	        mErrorBarLines[11] = surface-stdDev/2;
    	}
    }
    
    /**
     * Initialize the control. This code is in a separate method so that it can be
     * called from both constructors.
     */
    private void init() { 
    	// Set the outer rectangle
    	mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setStyle(Style.STROKE);
        mRectPaint.setStrokeWidth(1);
    	mRectPaint.setColor(Color.BLACK);
    	
    	// Set the layer 
    	mLayerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	mLayerPaint.setStyle(Style.FILL);
    	mLayerPaint.setColor(Color.CYAN);
    	
    	// Set error bar
    	upperErrorBar = new ArrayList<ErrorBar>();
    	lowerErrorBar = new ArrayList<ErrorBar>();
    	

    	numErrorBar = 10;
    	for (int i=0; i<numErrorBar; ++i) {
    		upperErrorBar.add(new ErrorBar());
    		lowerErrorBar.add(new ErrorBar());
    	}
    	
    	mUpperErrorBarPaint = new Paint();
    	mUpperErrorBarPaint.setStrokeWidth(2);
    	mUpperErrorBarPaint.setColor(Color.RED);
    	
    	mLowerErrorBarPaint = new Paint();
    	mLowerErrorBarPaint.setStrokeWidth(2);
    	mLowerErrorBarPaint.setColor(Color.DKGRAY);
    	
        //mErrorBarLines = new float[4*6]; // 6 lines. Each line is x0,y0,x1,y1
        
        // Create GeoRZA computing object
        
        rza = new GeoRZA(thickness, v1, v2, depth, peakFreq, maxOffset);
    }
	
	public void setDepthMax(int depth_max) {
		this.depth_max = depth_max;
	}

	public void setDepthMin(int depth_min) {
		this.depth_min = depth_min;
	}
}
