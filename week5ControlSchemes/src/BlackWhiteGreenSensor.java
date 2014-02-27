import lejos.nxt.*;
/**
 * A sensor that is able to distinguish a black/dark surface
 * from a white/bright surface.
 * 
 * Light percent values from an active light sensor and a
 * threshold value calculated based on a reading over a 
 * black/dark surface and a reading over a light/bright 
 * surface is used to make the distinction between the two 
 * types of surfaces.
 *  
 * @author  Ole Caprani
 * @version 20.02.13
 */
public class BlackWhiteGreenSensor {

   private LightSensor ls; 
   private int blackLightValue;
   private int whiteLightValue;
   private int blackGreenThreshold;
   private int greenWhiteThreshold;
   private int greenLightValue;

   public BlackWhiteGreenSensor(SensorPort p)
   {
	   ls = new LightSensor(p); 
	   // Use the light sensor as a reflection sensor
	   ls.setFloodlight(true);
   }

   private int read(String color){
	   
	   int lightValue=0;
	   
	   while (Button.ENTER.isDown());
	   
	   LCD.clear();
	   LCD.drawString("Press ENTER", 0, 0);
	   LCD.drawString("to callibrate", 0, 1);
	   LCD.drawString(color, 0, 2);
	   while( !Button.ENTER.isPressed() ){
	      lightValue = ls.readValue();
	      LCD.drawInt(lightValue, 4, 10, 2);
	      LCD.refresh();
	   }
	   return lightValue;
   }
   
   public void calibrate()
   {
	   blackLightValue = read("black");
	   greenLightValue = read("green");
	   whiteLightValue = read("white");
	   // The threshold is calculated as the median between 
	   // the two readings over the two types of surfaces
	   blackGreenThreshold = (blackLightValue+greenLightValue)/2;
	   greenWhiteThreshold = (greenLightValue+whiteLightValue)/2;
   }
   
   public boolean black() {
           return (ls.readValue()< blackGreenThreshold);
   }
   
   public boolean white() {
	   return (ls.readValue() > greenWhiteThreshold);
   }
   
   public boolean green() {
	   int v = ls.readValue();
	   return (v>blackGreenThreshold && v < greenWhiteThreshold);
   }
   
   public int light() {
 	   return ls.readValue();
   }
   
}