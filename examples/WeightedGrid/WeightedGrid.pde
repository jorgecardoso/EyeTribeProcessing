import org.jorgecardoso.processing.eyetribe.*;
import com.theeyetribe.client.data.*;
import java.util.Collections;

int COLS = 10;
int ROWS = 10;

EyeTribe eyeTribe;

int grid[][];

PVector point;

boolean sketchFullScreen() {
  return true;
  //return false;
}

void setup() {
  size(displayWidth, displayHeight);
  //size(800, 600);

  smooth();
  grid = new int[ROWS][COLS];
  point = new PVector();
  eyeTribe = new EyeTribe(this);
}

void draw() {
  background(0);

  noStroke();
  for ( int i = 0; i < ROWS*COLS; i++ ) {
    int x = i % COLS;
    int y = i / ROWS;

    fill(grid[y][x]);
    rect(x*width/COLS, y*height/ROWS, width/COLS, height/ROWS);
    if ( grid[y][x] > 0 ) {
      grid[y][x] --;
    }
  }

  noFill();
  stroke(255);
  ellipse(point.x, point.y, 5, 5);
}


void onGazeUpdate(PVector gaze, PVector leftEye_, PVector rightEye_, GazeData data) {

  //println(eyeTribe.isTracking() + " " + eyeTribe.isTrackingGaze() + " " + eyeTribe.isTrackingEyes() + " " + data.stateToString());
  if ( gaze != null ) {
    point = gaze.get();
    //println(point);
    int x = (int)constrain(map(gaze.x, 0, width, 0, COLS-1), 0, COLS-1);
    int y = (int)constrain(map(gaze.y, 0, height, 0, ROWS-1), 0, ROWS-1);
    println(x + " " + y);
    grid[y][x] += 5;
  }
}

void trackerStateChanged(String state) {
  println("Tracker state: " + state);
}

