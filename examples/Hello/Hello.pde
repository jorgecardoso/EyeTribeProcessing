import org.jorgecardoso.processing.eyetribe.*;
import com.theeyetribe.client.data.*;

EyeTribe eyeTribe;
ArrayList<PVector> tracking;

PVector point;
PVector leftEye, rightEye;

boolean sketchFullScreen() {
  return true;
}

void setup() {
  size(displayWidth, displayHeight);
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

  PVector betweenEyes = new PVector();
  PVector.sub(rightEye, leftEye, betweenEyes);

  float angle = betweenEyes.heading();
  pushMatrix();
  translate(leftEye.x, leftEye.y);
  PVector rEye = PVector.sub(leftEye, rightEye);
  rotate(angle);
  fill(255);
  stroke(0);
  ellipse(0, 0, 60, 30);
  fill(100);
  ellipse(rEye.x, 0, 60, 30);
  fill(#1A78D3);
  ellipse(0, 0, 20, 20);
  ellipse(rEye.x, 0, 20, 20);
  fill(0);

  ellipse(0, 0, 7, 7);
  ellipse(rEye.x, 0, 7, 7);

  popMatrix();
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
}


void onGazeUpdate(GazeData data) {

  println(eyeTribe.isTracking() + " " + eyeTribe.isTrackingGaze() + " " + eyeTribe.isTrackingEyes() + " " + data.stateToString());
  if ( eyeTribe.isTrackingGaze() && data.hasSmoothedGazeCoordinates() ) {
    point.x = (float)(data.smoothedCoordinates.x);
    point.y = (float)(data.smoothedCoordinates.y);
    tracking.add(point.get());
    if (tracking.size() > 500 ) {
      tracking.remove(0);
    }
    //println(point);
  }
  if ( eyeTribe.isTrackingEyes() ) {
    leftEye.x = (float)(data.leftEye.pupilCenterCoordinates.x*width);
    leftEye.y = (float)(data.leftEye.pupilCenterCoordinates.y*height);

    rightEye.x = (float)(data.rightEye.pupilCenterCoordinates.x*width);
    rightEye.y = (float)(data.rightEye.pupilCenterCoordinates.y*height);
  }
}

void trackerStateChanged(String state) {
  println("Tracker state: " + state);
}
