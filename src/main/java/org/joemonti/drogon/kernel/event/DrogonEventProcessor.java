/*
 * Drogon : DrogonEventProcessor.java
 * 
 * This file is part of Drogon.
 *
 * Drogon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Drogon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Drogon.  If not, see <http://www.gnu.org/licenses/>. 
 *
 * Author: Joseph Monti <joe.monti@gmail.com>
 * Copyright (c) 2013 Joseph Monti All Rights Reserved, http://joemonti.org/
 */

package org.joemonti.drogon.kernel.event;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class DrogonEventProcessor implements Runnable {
    private final DrogonEventHandler handler;
    
    private final AtomicBoolean active;
    
    private volatile DrogonEvent event;
    
    public DrogonEventProcessor( DrogonEventHandler handler ) {
        this.handler = handler;
        this.active = new AtomicBoolean( );
        this.event = null;
    }
    
    public boolean isActive() {
        return active.get( );
    }
    
    public boolean activate( DrogonEvent event ) {
        boolean activated = this.active.compareAndSet( false, true );
        
        if ( !activated ) return false;
        
        this.event = event;
        
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        active.set( true );
        try {
            handler.handle( event );
        } finally {
            active.set( false );
        }
    }
}
