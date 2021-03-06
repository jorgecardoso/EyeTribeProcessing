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
import java.util.*;

import processing.core.*;

import com.theeyetribe.client.*;
import com.theeyetribe.client.data.*;

/**
 * 
 */

public class EyeTribe implements IGazeListener, ITrackerStateListener,
		ICalibrationProcessHandler {

	private int CALIBRATION_ATTEMPTS = 5;
	
	// myParent is a reference to the parent sketch
	PApplet myParent;

	GazeManager gm;

	public final static String VERSION = "##library.prettyVersion##";

	private Method gazeUpdateMethod = null;
	private Method trackerStateChangedMethod = null;
	private Method calibratingPointMethod = null;
	private Method calibrationEndedMethod = null;

	private boolean isTracking = false;
	private boolean isTrackingGaze = false;
	private boolean isTrackingEyes = false;

	private PVector calibrationPoints[];
	private int currentCalibrationPoint = 0;
	
	private int calibrationPointInterval;
	private int calibrationPointDuration;
	
	// number of calibration/resampling attempts
	private int calibrationAttempts = 0;

	public EyeTribe(PApplet theParent) {
		this(theParent, 300, 1200);
	}
	
	/**
	 * a Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 * @example Hello
	 * @param theParent
	 */
	public EyeTribe(PApplet theParent, int calibrationPointInterval, int calibrationPointDuration) {
		myParent = theParent;
		this.calibrationPointInterval = calibrationPointInterval;
		this.calibrationPointDuration = calibrationPointDuration;
		
		welcome();

		try {
			gazeUpdateMethod = myParent.getClass().getMethod(
					"onGazeUpdate",
					new Class[] { PVector.class, PVector.class, PVector.class,
							GazeData.class });

		} catch (Exception e) {
			System.err.println("onGazeUpdate() method not defined. ");
		}

		try {
			trackerStateChangedMethod = myParent.getClass().getMethod(
					"trackerStateChanged", new Class[] { String.class });

		} catch (Exception e) {
			System.err.println("trackerStateChanged() method not defined. ");
		}

		try {
			calibratingPointMethod = myParent.getClass().getMethod(
					"calibratingPoint", new Class[] { PVector.class, boolean.class });

		} catch (Exception e) {
			System.err.println("calibratingPoint() method not defined. ");
		}

		try {
			calibrationEndedMethod = myParent.getClass().getMethod(
					"calibrationEnded",
					new Class[] { boolean.class, double.class, double.class,
							double.class, CalibrationResult.class });

		} catch (Exception e) {
			System.err.println("calibrationEnded() method not defined. ");
		}

		gm = GazeManager.getInstance();
		boolean success = gm.activate(GazeManager.ApiVersion.VERSION_1_0,
				GazeManager.ClientMode.PUSH);
		// System.out.println(""+success);

		if (!success) {
			System.err.println("Could not activate Eye Tribe. :(");
			return;
		}
		gm.addGazeListener(this);
		gm.addTrackerStateListener(this);

	}

	public void dispose() {
		gm.removeGazeListener(EyeTribe.this);
		gm.deactivate();
		System.out.println("GazeManager deactivated.");
	}

	private void welcome() {
		System.out
				.println("##library.name## ##library.prettyVersion## by ##author##");
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
	public void onGazeUpdate(GazeData gazeData) {
		// System.out.println(gazeData.stateToString());
		// System.out.println(gazeData.leftEye.toString());

		if (gazeData != null) {
			isTracking = ((gazeData.STATE_TRACKING_PRESENCE & gazeData.state) != 0);
			isTrackingGaze = ((gazeData.STATE_TRACKING_GAZE & gazeData.state) != 0);
			isTrackingEyes = ((gazeData.STATE_TRACKING_EYES & gazeData.state) != 0);
		}

		if (gazeData != null && gazeUpdateMethod != null) {

			PVector gaze = null;
			PVector leftEye = null;
			PVector rightEye = null;

			if (gazeData.hasSmoothedGazeCoordinates()) {
				gaze = new PVector((float) (gazeData.smoothedCoordinates.x),
						(float) (gazeData.smoothedCoordinates.y));
			}
			if (isTrackingEyes
					&& gazeData.leftEye.pupilCenterCoordinates.x != 0
					&& gazeData.leftEye.pupilCenterCoordinates.y != 0) {
				leftEye = new PVector(
						(float) (gazeData.leftEye.pupilCenterCoordinates.x),
						(float) (gazeData.leftEye.pupilCenterCoordinates.y));
			}
			if (isTrackingEyes
					&& gazeData.rightEye.pupilCenterCoordinates.x != 0
					&& gazeData.rightEye.pupilCenterCoordinates.y != 0) {
				rightEye = new PVector(
						(float) (gazeData.rightEye.pupilCenterCoordinates.x),
						(float) (gazeData.rightEye.pupilCenterCoordinates.y));
			}

			try {
				gazeUpdateMethod.invoke(myParent, new Object[] { gaze, leftEye,
						rightEye, gazeData });
			} catch (Exception e) {
				System.err
						.println("Disabling gaze updates because of an error.");
				System.err.println(e.getMessage());
				e.printStackTrace();
				gazeUpdateMethod = null;
			}
		}

	}

	@Override
	public void onTrackerStateChanged(int trackerState) {
		// System.out.println( (
		// GazeManager.TrackerState.fromInt(trackerState)).toString() );
		if (trackerStateChangedMethod != null) {
			try {
				trackerStateChangedMethod.invoke(myParent,
						new Object[] { (GazeManager.TrackerState
								.fromInt(trackerState)).toString() });
			} catch (Exception e) {
				System.err
						.println("Disabling tracker state updates because of an error.");
				System.err.println(e.getMessage());
				e.printStackTrace();
				trackerStateChangedMethod = null;
			}
		}
	}

	@Override
	public void onScreenStatesChanged(int screenIndex,
			int screenResolutionWidth, int screenResolutionHeight,
			float screenPhysicalWidth, float screenPhysicalHeight) {
	}

	public void calibrate(PVector calibrationPoints[]) {
		// Start calibration
		calibrationAttempts = 0;
		calibrate(calibrationPoints, true);
		
		
	}


    private void calibrate(PVector calibrationPoints[], boolean callStart) {
    	this.calibrationPoints = calibrationPoints;
    	this.calibrationAttempts++;
    	if ( callStart ) {
			gm.calibrationStart(calibrationPoints.length, this);
		} else {
			onCalibrationStarted();
		}

    }
    
    private void calPoint(int pointIndex) {
    
    	// call calibratingPoint in Processing to show the dot and let eyes rest
    	try {
			calibratingPointMethod
					.invoke(myParent,
							new Object[] { calibrationPoints[pointIndex], true });
		} catch (Exception e) {
			System.err
					.println("Disabling calibration point feedback because of an error.");
			System.err.println(e.getMessage());
			e.printStackTrace();
			calibratingPointMethod = null;
		}
		try {
			Thread.sleep(this.calibrationPointInterval);
		} catch (InterruptedException ie) {
			System.err.println(ie.getMessage());
			ie.printStackTrace();
		}
		
		// call calibrationPointStart to start calibrating point
		gm.calibrationPointStart(
				(int) calibrationPoints[pointIndex].x,
				(int) calibrationPoints[pointIndex].y);
		try {
			Thread.sleep(this.calibrationPointDuration);
		} catch (InterruptedException ie) {
			System.err.println(ie.getMessage());
			ie.printStackTrace();
		}
		gm.calibrationPointEnd();
		try {
			calibratingPointMethod
					.invoke(myParent,
							new Object[] { calibrationPoints[pointIndex], false });
		} catch (Exception e) {
			System.err
					.println("Disabling calibration point feedback because of an error.");
			System.err.println(e.getMessage());
			e.printStackTrace();
			calibratingPointMethod = null;
		}
    }
    
	/**
	 * Called when a calibration process has been started.
	 */
	@Override
	public void onCalibrationStarted() {
		//System.out.println("EyeTribe: Calibration started");
		currentCalibrationPoint = 0;

		calPoint(currentCalibrationPoint);
	}

	/**
	 * Called every time tracking of a single calibration points has completed.
	 * 
	 * @param progress
	 *            'normalized' progress [0..1.0d]
	 */
	@Override
	public void onCalibrationProgress(double progress) {
		System.out.println("EyeTribe: Calibration progress: " + progress);
		currentCalibrationPoint++;
		if (currentCalibrationPoint >= calibrationPoints.length)
			return;
		calPoint(currentCalibrationPoint);	
	}

	/**
	 * Called when all calibration points have been collected and calibration
	 * processing begins.
	 */
	@Override
	public void onCalibrationProcessing() {
		System.out.println("EyeTribe: Calibration processing...");
	}

	/**
	 * Called when processing of calibration points and calibration as a whole
	 * has completed.
	 * 
	 * @param calibResult
	 *            the result of the calibration process
	 */
	@Override
	public void onCalibrationResult(final CalibrationResult calibResult) {
		
		ArrayList<PVector> recalibrate = new ArrayList<PVector>();
		for ( CalibrationResult.CalibrationPoint cp : calibResult.calibpoints) {
			if ( cp.state == 1 ) {
				recalibrate.add(new PVector((float)cp.coordinates.x, (float)cp.coordinates.y));
			}
			//System.out.println(cp.state);
		}
		

		
		if ( recalibrate.size() > 0 && calibrationAttempts < CALIBRATION_ATTEMPTS) {
			PVector []points = recalibrate.toArray(new PVector[recalibrate.size()]);
			calibrate(points, false);
		} else {
			
			if ( recalibrate.size() > 0   ) {
				gm.calibrationAbort();
			}
			
			// System.out.println("EyeTribe: Calibration result: " + calibResult);
			try {
				calibrationEndedMethod.invoke(myParent, new Object[] {
					calibResult.result.booleanValue(),
					calibResult.averageErrorDegree.doubleValue(),
					calibResult.averageErrorDegreeLeft.doubleValue(),
					calibResult.averageErrorDegreeRight.doubleValue(),
					calibResult });
			} catch (Exception e) {
				System.err
					.println("Disabling calibration ended feedback because of an error.");
				System.err.println(e.getMessage());
				e.printStackTrace();
				calibrationEndedMethod = null;
			}
		} 
		
		
	}
}
