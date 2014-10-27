package seemingly.concerned.neighbors.hackathon14;

import seemingly.concerned.neighbors.hackathon14.drawImage.SeismicImage;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	SeismicImage seismicImage;
	SeekBar seekBar_Depth;
	SeekBar seekBar_Thickness;
	SeekBar seekBar_PeakFreq;
	SeekBar seekBar_MaxOffset;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Resources res = getResources();
        
        setContentView(R.layout.activity_main);
        
        // Set range of seekBar_Depth. True seekBar_Depth is [0,((max-min)/step)]
        final float depth_step = 1;
        final float depth_min = 1;
        final float depth_max = 10000;
        // Set range of seekBar_Thickness. True seekBar_Thickness is [0,((max-min)/step)]
        final float step_Thickness = 1;
        final float min_Thickness = 1;
        final float max_Thickness = 1000;
        // Set range of seekBar_PeakFreq. True seekBar_PeakFreq is [0,((max-min)/step)]
        // TODO how do I step a value by a non-floateger?
        final float step_PeakFreq = 1;
        final float min_PeakFreq = 8;
        final float max_PeakFreq = 80;
        // Set range of seekBar_MaxOffset. True seekBar_MaxOffset is [0,((max-min)/step)]
        final float step_MaxOffset = 10;
        final float min_MaxOffset = 700; // does not equal 0 in case zero cannot be handled.
        final float max_MaxOffset = 10000;
        
        seismicImage = (SeismicImage) this.findViewById(R.id.SeismicImage);
        //seismicImage.setBackgroundColor(Color.WHITE);
        
        // Depth inputs
        //TextView textView_Depth = (TextView) (findViewById(R.id.textView_Depth));
        final TextView t_Depth = new TextView(this);
        
        seekBar_Depth = (SeekBar) (findViewById(R.id.seekBar_Depth));
        
        
        seismicImage.setDepthStep(depth_step);
        seismicImage.setDepthMax(depth_max);
        seismicImage.setDepthMin(depth_min);
    
        seekBar_Depth.setMax((int)((depth_max - depth_min)/depth_step));
        
        // Depth Seekbar Listener
        seekBar_Depth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int depth = (int)(depth_min + (depth_step*seekBar.getProgress()));
				seismicImage.setDepth(depth);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int depth = (int)(depth_min + (depth_step*seekBar.getProgress()));
				depth = seismicImage.setDepth(depth);
				t_Depth.setTextSize(depth);
		        Toast.makeText(getApplicationContext(), String.valueOf(depth),Toast.LENGTH_SHORT).show();		        
			}
        }); 
        
        // Thickness inputs
        final TextView t_Thickness = new TextView(this);
        
        seekBar_Thickness = (SeekBar) (findViewById(R.id.seekBar_Thickness));

        
        seekBar_Thickness.setMax((int)((max_Thickness - min_Thickness)/step_Thickness));
        
        seekBar_Thickness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int thickness = (int) (min_Thickness + (step_Thickness*seekBar.getProgress()));
				seismicImage.setThickness(thickness);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int thickness = (int) (min_Thickness + (step_Thickness*seekBar.getProgress()));
				thickness = seismicImage.setThickness(thickness);
				t_Thickness.setTextSize(thickness);
		        Toast.makeText(getApplicationContext(), String.valueOf(thickness),Toast.LENGTH_SHORT).show();
  
			}
        }); 
        
     // PeakFreq inputs
        final TextView t_PeakFreq = new TextView(this);
        
        seekBar_PeakFreq = (SeekBar) (findViewById(R.id.seekBar_PeakFreq));
        

        seekBar_PeakFreq.setMax((int) ((max_PeakFreq - min_PeakFreq)/step_PeakFreq));
        
        seekBar_PeakFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int peakFreq = (int) (min_PeakFreq + (step_PeakFreq*seekBar.getProgress()));
				seismicImage.setPeakFreq(peakFreq);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int peakFreq = (int) (min_PeakFreq + (step_PeakFreq*seekBar.getProgress()));
				t_PeakFreq.setTextSize(peakFreq);
		        Toast.makeText(getApplicationContext(), String.valueOf(peakFreq),Toast.LENGTH_SHORT).show();
		        
		        seismicImage.setPeakFreq(peakFreq);
			}
        }); 
        
     // MaxOffset inputs
        final TextView t_MaxOffset = new TextView(this);
        
        seekBar_MaxOffset = (SeekBar) (findViewById(R.id.seekBar_MaxOffset));
        

        seekBar_MaxOffset.setMax((int) ((max_MaxOffset - min_MaxOffset)/step_MaxOffset));
        
        seekBar_MaxOffset.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int maxOffset = (int) (min_MaxOffset + (step_MaxOffset*seekBar.getProgress()));
				seismicImage.setMaxOffset(maxOffset);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int maxOffset = (int) (min_MaxOffset + (step_MaxOffset*seekBar.getProgress()));
				t_MaxOffset.setTextSize(maxOffset);
				//Toast toast = new Toast(null);
		        Toast.makeText(getApplicationContext(), String.valueOf(maxOffset),Toast.LENGTH_SHORT).show();
		        
		        seismicImage.setMaxOffset(maxOffset);
			}
        }); 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
