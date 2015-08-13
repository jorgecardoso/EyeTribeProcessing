/**
 * Eye Tribe for Processing library
 * Weighted Grid example: Simple example showing how to get gaze data.
 * August 2015
 * http://jorgecardoso.eu
 **/
 
import org.jorgecardoso.processing.eyetribe.*;
import com.theeyetribe.client.data.*;

int COLS = 10;
int ROWS = 10;

EyeTribe eyeTribe;

float grid[][];

PVector point;

PImage img;


void setup() {
  fullScreen();
  //size(800, 600);
  img = loadImage("cat.jpg");
  
  smooth();
  grid = new float[ROWS][COLS];
  point = new PVector();
  eyeTribe = new EyeTribe(this);
}

void draw() {
  background(0);
  image(img, 0, 0, width, height);
  noStroke();
  for ( int i = 0; i < ROWS*COLS; i++ ) {
    int x = i % COLS;
    int y = i / ROWS;

    fill(#2FACC4, 255-grid[y][x]);
    rect(x*width/COLS, y*height/ROWS, width/COLS, height/ROWS);
    if ( grid[y][x] > 0 ) {
      grid[y][x] -= 0.1;
    }
  }

  noFill();
  stroke(255);
  ellipse(point.x, point.y, 5, 5);
}


void onGazeUpdate(PVector gaze, PVector leftEye_, PVector rightEye_, GazeData data) {

  if ( gaze != null ) {
    point = gaze.get(); 

    int x = (int)constrain(round(map(point.x, 0, width, 0, COLS-1)), 0, COLS-1);
    int y = (int)constrain(round(map(point.y, 0, height, 0, ROWS-1)), 0, ROWS-1);
    
    grid[y][x] = constrain( grid[y][x]+10, 0, 255);
  }
}

void trackerStateChanged(String state) {
  println("Tracker state: " + state);
}