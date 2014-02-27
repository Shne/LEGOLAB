import lejos.nxt.*;
import lejos.nxt.ColorSensor.Color;
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

   private ColorSensor ls; 
   private int blackLightValue;
   private int whiteLightValue;
   private int blackGreenThreshold;
   private int greenWhiteThreshold;
   private int greenLightValue;

   public BlackWhiteGreenSensor(SensorPort p)
   {
	   ls = new ColorSensor(SensorPort.S1);
	   // Use the light sensor as a reflection sensor
	   ls.setFloodlight(Color.WHITE);
   }

   private int read(String color){
	   
	   
	   return -1;
   }
   
   public void calibrate()
   {
   }
   
   public boolean black() {
	   return ls.getLightValue()<=75;
   }
   
   public boolean white() {
	   return ls.getColor().getColor() == 6;
   }
   
   public boolean green() {
	   return ls.getColor().getColor() == 1 && ls.getLightValue()>75;
   }
   
   public int light() {
 	   return -1;
   }
   
}