import org.jorgecardoso.processing.eyetribe.*;
import com.theeyetribe.client.data.*;

EyeTribe eyeTribe;
ArrayList<PVector> tracking;

PVector calibratingPoint = null;
PVector point;
PVector leftEye, rightEye;

boolean calibrating = false;

boolean sketchFullScreen() {
  return true;
  //return false;
}

void setup() {
  size(displayWidth, displayHeight);
  //size(800, 600);

  smooth();
  tracking = new ArrayList<PVector>();
  point = new PVector();
  leftEye = new PVector();
  rightEye = new PVector();

  eyeTribe = new EyeTribe(this);

  PFont font = createFont("", 40);
  textFont(font);
}

void draw() {
  background(0);
  fill(255);
  //text(eyeTribe.sayHello(), 40, 200);

  if (! calibrating) {
    if ( leftEye != null ) {
      fill(255);
      stroke(0);
      ellipse(leftEye.x*width, leftEye.y*height, 60, 30);

      fill(#1A78D3);
      ellipse(leftEye.x*width, leftEye.y*height, 20, 20);

      fill(0);
      ellipse(leftEye.x*width, leftEye.y*height, 7, 7);
    }
    if ( rightEye != null ) {
      fill(255);
      stroke(0);
      ellipse(rightEye.x*width, rightEye.y*height, 60, 30);

      fill(#1A78D3);
      ellipse(rightEye.x*width, rightEye.y*height, 20, 20);

      fill(0);
      ellipse(rightEye.x*width, rightEye.y*height, 7, 7);
    }


    noFill();
    stroke(255, 0, 0);
    ellipse(point.x, point.y, 30, 30);


    if ( tracking.size() > 1) {
      for (int i = 1; i < tracking.size (); i++ ) {
        stroke(map(i, 1, tracking.size(), 0, 255));
        strokeWeight(map(i, 1, tracking.size(), 0.1, 3));
        PVector f = tracking.get(i-1);
        line(f.x, f.y, tracking.get(i).x, tracking.get(i).y);
      }
    }
  } else {
    if ( calibratingPoint != null ) {
      fill(0, 255, 0);
      ellipse(calibratingPoint.x, calibratingPoint.y, 20, 20);
    }
  }
}

void calibratingPoint(PVector p) {
  println("Calibrating point: " + p);
  calibratingPoint = p.get();
}

void calibrationEnded(boolean result, double acc, double accRight, double accLeft, CalibrationResult calibResult) {
  calibrating = false;
  println("Result: " + result + " " + acc);
}

void onGazeUpdate(PVector gaze, PVector leftEye_, PVector rightEye_, GazeData data) {

  //println(eyeTribe.isTracking() + " " + eyeTribe.isTrackingGaze() + " " + eyeTribe.isTrackingEyes() + " " + data.stateToString());
  if ( gaze != null ) {
    point = gaze;
    tracking.add(point.get());
    if (tracking.size() > 500 ) {
      tracking.remove(0);
    }
    //println(point);
  }

  leftEye = leftEye_;

  rightEye = rightEye_;
}

void trackerStateChanged(String state) {
  println("Tracker state: " + state);
}

void keyPressed() {
  if ( key == ' ' ) {
    calibrating = true;
    PVector calP[] = {
      new PVector(100, 100), new PVector(width/2, 100), new PVector(width-100, 100), 
      new PVector(100, height/2), new PVector(width/2, height/2), new PVector(width-100, height/2), 
      new PVector(100, height-100), new PVector(width/2, height-100), new PVector(width-100, height-100)
      };
      eyeTribe.calibrate(calP);
  }
}
