import org.jorgecardoso.processing.eyetribe.*;

EyeTribe eyeTribe;

void setup() {
  size(400,400);
  smooth();
  
  eyeTribe = new EyeTribe(this);
  
  PFont font = createFont("",40);
  textFont(font);
}

void draw() {
  background(0);
  fill(255);
  text(eyeTribe.sayHello(), 40, 200);
}
