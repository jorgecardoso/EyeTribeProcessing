/**
 * Eye Tribe for Processing library
 * EyeTribe2OSC example: Sends gaze data to other applications via the OSC protocol.
 * August 2015
 * http://jorgecardoso.eu
 **/

import org.jorgecardoso.processing.eyetribe.*;
import com.theeyetribe.client.data.*;

import netP5.*;
import oscP5.*;

EyeTribe eyeTribe;

OscP5 oscP5;
NetAddress myRemoteLocation;

void setup() {
  //fullScreen();
  size(200, 200);

  
  oscP5 = new OscP5(this, 12000);

  myRemoteLocation = new NetAddress("127.0.0.1", 54321);
  
  eyeTribe = new EyeTribe(this);
}

void draw() {
}


void onGazeUpdate(PVector gaze, PVector leftEye_, PVector rightEye_, GazeData data) {

  if ( gaze != null ) {
    OscMessage myMessage = new OscMessage("/gazeUpdate");

    myMessage.add(gaze.x); 
    myMessage.add(gaze.y); 

    oscP5.send(myMessage, myRemoteLocation);
  }
  
  if ( leftEye_ != null ) {
    OscMessage myMessage = new OscMessage("/leftEye");
    
    myMessage.add(leftEye_.x);
    myMessage.add(leftEye_.y);

    oscP5.send(myMessage, myRemoteLocation);
  }
  
  if ( rightEye_ != null ) {
    OscMessage myMessage = new OscMessage("/rightEye");

    myMessage.add(rightEye_.x);
    myMessage.add(rightEye_.y);

    oscP5.send(myMessage, myRemoteLocation);
  }
}

void trackerStateChanged(String state) {
  println("Tracker state: " + state);
}