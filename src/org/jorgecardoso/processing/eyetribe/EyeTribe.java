/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package org.jorgecardoso.processing.eyetribe;

import java.lang.reflect.*;

import processing.core.*;

import com.theeyetribe.client.*;
import com.theeyetribe.client.data.*;

/**
 * This is a template class and can be used to start a new processing library or tool.
 * Make sure you rename this class as well as the name of the example package 'template' 
 * to your own library or tool naming convention.
 * 
 * (the tag example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 * @example Hello 
 */

public class EyeTribe implements IGazeListener, ITrackerStateListener {
	
	// myParent is a reference to the parent sketch
	PApplet myParent;


	GazeManager gm;
	
	public final static String VERSION = "##library.prettyVersion##";
	
    private Method gazeUpdateMethod = null;


	private boolean isTracking = false;
	private boolean isTrackingGaze = false;
	private boolean isTrackingEyes = false;
	
	/**
	 * a Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 * @example Hello
	 * @param theParent
	 */
	public EyeTribe(PApplet theParent) {
		myParent = theParent;
		welcome();
		
		try {
      		gazeUpdateMethod =
        	myParent.getClass().getMethod("onGazeUpdate",  new Class[] { 
        		GazeData.class
      		});
      
    	} catch (Exception e) {
    		System.err.println("onGazeUpdate() method not defined. ");
    	}
    
		gm = GazeManager.getInstance();        
   		boolean success = gm.activate(GazeManager.ApiVersion.VERSION_1_0, GazeManager.ClientMode.PUSH);
   		System.out.println(""+success);

    	gm.addGazeListener(this);
    	gm.addTrackerStateListener(this);
    	
	}
	
	public void dispose() {
		gm.removeGazeListener(EyeTribe.this);
		gm.deactivate();
       	System.out.println("GazeManager deactivated.");
 	}
	
	private void welcome() {
		System.out.println("##library.name## ##library.prettyVersion## by ##author##");
	}
	

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

	public boolean isTracking() {
		return this.isTracking;
	}
	
	public boolean isTrackingGaze() {
		return this.isTrackingGaze;
	}
	
	public boolean isTrackingEyes() {
		return this.isTrackingEyes;
	}	
	
	 @Override
    public void onGazeUpdate(GazeData gazeData)
    {
        //System.out.println(gazeData.stateToString());
        //System.out.println(gazeData.leftEye.toString());
        
        if (gazeData != null) {
        	isTracking =  ((gazeData.STATE_TRACKING_PRESENCE & gazeData.state) != 0);
    	    isTrackingGaze =  ((gazeData.STATE_TRACKING_GAZE & gazeData.state) != 0);
    	    isTrackingEyes =  ((gazeData.STATE_TRACKING_EYES & gazeData.state) != 0);
        }
         
		if (gazeData != null && gazeUpdateMethod != null) {
      		try {
        		gazeUpdateMethod.invoke(myParent, new Object[] {
          			gazeData
        		}    );
      		} catch (Exception e) {
        		System.err.println("Disabling gaze updates because of an error.");
        		System.err.println(e.getMessage());
        		e.printStackTrace();
        		gazeUpdateMethod = null;
      		}
    	}
         
    }
    
    @Override
    public void onTrackerStateChanged(int trackerState) {
    	System.out.println(( GazeManager.TrackerState.fromInt(trackerState)).toString() );
    
    }
    @Override
    public void onScreenStatesChanged(int screenIndex, int screenResolutionWidth, int screenResolutionHeight,
            float screenPhysicalWidth, float screenPhysicalHeight) {
    }
    
    
}

