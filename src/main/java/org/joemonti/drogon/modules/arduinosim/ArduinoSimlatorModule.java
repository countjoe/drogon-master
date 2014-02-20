/**
 * 
 */
package org.joemonti.drogon.modules.arduinosim;

import org.joemonti.drogon.kernel.event.DrogonEvent;
import org.joemonti.drogon.kernel.event.DrogonEventCommand;
import org.joemonti.drogon.kernel.event.DrogonEventManager;
import org.joemonti.drogon.kernel.event.DrogonEventData;
import org.joemonti.drogon.kernel.module.DrogonModule;
import org.joemonti.drogon.modules.arduino.EventArduinoDataLog;
import org.joemonti.drogon.modules.arduino.EventArduinoMessage;

/**
 * @author joe
 *
 */
public class ArduinoSimlatorModule implements DrogonModule {
    private static final long SIM_INTERVAL = 500;
    
    private long eventClientId = 0;
    DrogonEventManager eventManager = null;
    
    private static final String EVENT_CLIENT_NAME = "arduinosim";
    
    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.module.DrogonModule#initialize()
     */
    @Override
    public void initialize() {
        eventClientId = eventManager.registerClient( EVENT_CLIENT_NAME );
        eventManager.registerEvent( eventClientId, DrogonEventCommand.ARDUINO_MESSAGE, EventArduinoMessage.class );
        eventManager.registerEvent( eventClientId, DrogonEventCommand.ARDUINO_DATA_LOG, EventArduinoDataLog.class );
        
    }

    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.module.DrogonModule#shutdown()
     */
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }
    
    class SimRunner implements Runnable {
        @Override
        public void run() {
            
            while ( true ) {
                try {
                    Thread.sleep( SIM_INTERVAL );
                } catch ( InterruptedException ex ) {
                    return;
                }
                
                String data = "...";
                
                DrogonEventData object = new EventArduinoDataLog( data );
                
                DrogonEvent event = new DrogonEvent( 
                        eventClientId, 
                        DrogonEventCommand.ARDUINO_DATA_LOG, 
                        object );
                
                
                eventManager.send( event );
            }
        }
    }
}
