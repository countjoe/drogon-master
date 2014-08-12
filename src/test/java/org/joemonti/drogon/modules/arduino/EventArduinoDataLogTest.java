/**
 * 
 */
package org.joemonti.drogon.modules.arduino;

import junit.framework.TestCase;

/**
 * @author joe
 *
 */
public class EventArduinoDataLogTest extends TestCase {
    public void testParse() {
        int millis = 123;
        double[] accel = { 1.2, 1.3, 1.4 };
        double[] gyro = { 1.5, 1.6, 1.7 };
        double[] motor = { 1.8, 1.9, 2.0, 2.1 };
        double[] pos = { 2.2, 2.3 };
        double[] pidErr = { 2.4, 2.5 };
        
        
        EventArduinoDataLog orig = new EventArduinoDataLog( );
        orig.setTimestamp( millis );
        orig.setAccelerometer( accel );
        orig.setGyroscope( gyro );
        orig.setMotorAdjust( motor );
        orig.setRotation( pos );
        orig.setPidError( pidErr );
        
        String line = orig.getData( );
    }
}
