/*
 * Drogon : DrogonEventManager.java
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

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class DrogonEventManager {
    private static final Logger logger = LoggerFactory.getLogger( DrogonEventManager.class );
    
    private static final int MAX_THREADS = 10;
    
    private static DrogonEventManager instance = new DrogonEventManager( );
    
    public static DrogonEventManager getInstance() {
        return instance;
    }
    
    private ExecutorService executor = Executors.newFixedThreadPool( MAX_THREADS );
    
    private ConcurrentMap<Long, String> clients;
    private ConcurrentMap<DrogonEventCommand, DrogonEventInfo> events;
    private ConcurrentMap<DrogonEventCommand, ConcurrentMap<Long,DrogonEventProcessor>> subscriptions;
    
    private AtomicInteger counter;
    
    public DrogonEventManager() {
        this.events = new ConcurrentHashMap<DrogonEventCommand, DrogonEventInfo>( );
        this.subscriptions = new ConcurrentHashMap<DrogonEventCommand, ConcurrentMap<Long,DrogonEventProcessor>>( );
        
        this.counter = new AtomicInteger( );
    }
    
    /**
     * Registers a client with the event manager.
     * 
     * @param name The readable name of the client 
     * @return A unique id to use as sender/receiver.
     */
    public long registerClient( String name ) {
        // bad random long value
        long id = ( System.currentTimeMillis( ) * (long) ( Math.random( ) * 10000 ) ) + counter.incrementAndGet( );
        clients.put( id, name );
        return id;
    }
    
    /**
     * Register an event command.
     * 
     * @param source The source allowed to send the event.
     * @param command
     * @param eventObjectClass
     * @return
     */
    public boolean registerEvent( long source, DrogonEventCommand command, Class<DrogonEventObject> eventObjectClass ) {
        DrogonEventInfo eventInfo = new DrogonEventInfo( source, eventObjectClass );
        DrogonEventInfo oldEventInfo = events.putIfAbsent( command, eventInfo );
        
        if ( oldEventInfo != null ) {
            // if for same source, ok to overwrite
            if ( oldEventInfo.getSource( ) == source ) {
                events.put( command, eventInfo );
            } else {
                logger.debug( "Attempting to register event for command " + command + " from " + getDisplayName( source )  + ",  however " + getDisplayName( oldEventInfo.getSource( ) ) + " already registered for that command" );
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Returns the event info for command.
     * 
     * @param command The command for which to get the info.
     * @return the event info.
     */
    public DrogonEventInfo getEventInfo( DrogonEventCommand command ) {
        return events.get( command );
    }
    
    /**
     * Subscribes handler for command at receiver.
     * 
     * @param receiver The receiver id receiving the requests
     * @param command  The command to which to subscribe
     * @param handler  The handler class implementation to receive the events
     */
    public void subscribe( long receiver, DrogonEventCommand command, DrogonEventHandler handler ) {
        ConcurrentMap<Long,DrogonEventProcessor> handlers = subscriptions.get( command );
        if ( handlers == null ) {
            handlers = new ConcurrentHashMap<Long, DrogonEventProcessor>( );
            ConcurrentMap<Long,DrogonEventProcessor> oldHandlers = subscriptions.putIfAbsent( command, handlers );
            if ( oldHandlers != null ) {
                handlers = oldHandlers;
            }
        }
        
        handlers.put( receiver, new DrogonEventProcessor( handler ) );
        
        logger.debug( "Receiver " + getDisplayName( receiver ) + " subscribed to " + command );
    }
    
    /**
     * Unsubscribes receiver for command.
     * 
     * @param receiver The receiver id to unsubscribe.
     * @param command The command to unsubscribe from.
     */
    public void unsubscribe( long receiver, DrogonEventCommand command ) {
        ConcurrentMap<Long,DrogonEventProcessor> handlers = subscriptions.get( command );
        if ( handlers == null ) {
            handlers = new ConcurrentHashMap<Long, DrogonEventProcessor>( );
            ConcurrentMap<Long,DrogonEventProcessor> oldHandlers = subscriptions.putIfAbsent( command, handlers );
            if ( oldHandlers != null ) {
                handlers = oldHandlers;
            }
        }
        
        handlers.remove( receiver );
        
        logger.debug( "Receiver " + getDisplayName( receiver ) + " unsubscribed from " + command );
    }
    
    /**
     * Send event to all subscribed receivers.
     * 
     * @param event The event to send
     */
    public boolean send( DrogonEvent event ) {
        if ( !validateSource( event ) ) {
            return false;
        }
        
        ConcurrentMap<Long,DrogonEventProcessor> processors = subscriptions.get( event.getCommand( ) );
        if ( processors != null && processors.size( ) > 0 ) {
            for ( Entry<Long,DrogonEventProcessor> entry: processors.entrySet( ) ) {
                DrogonEventProcessor processor = entry.getValue( );
                if ( processor.activate( event ) ) {
                    executor.execute( processor );
                } else {
                    logger.warn( "Unable to activate handler for event " + event.getCommand( ) + " from " + getDisplayName( event.getSource( ) ) + " to " + getDisplayName( entry.getKey( ) ) );
                }
            }
        }
        
        return true;
    }
    
    /**
     * Send event to specific receiver, only if subscribed.
     * 
     * @param event     The event to send.
     * @param receiver  The receiver to receive the event.
     */
    public boolean send( DrogonEvent event, long receiver ) {
        if ( !validateSource( event ) ) {
            return false;
        }
        
        ConcurrentMap<Long,DrogonEventProcessor> processors = subscriptions.get( event.getCommand( ) );
        if ( processors != null ) {
            DrogonEventProcessor processor = processors.get( receiver );
            if ( processor != null ) {
                if ( processor.activate( event ) ) {
                    executor.execute( processor );
                    
                    return true;
                } else {
                    logger.warn( "Unable to activate handler for event " + event.getCommand( ) + " from " + getDisplayName( event.getSource( ) ) + " to " + getDisplayName( receiver ) );
                }
            } else {
                logger.warn( "Receiver not registered for event " + event.getCommand( ) + " from " + getDisplayName( event.getSource( ) ) + " to " + getDisplayName( receiver ) );
            }
        }
        
        return false;
    }
    
    private boolean validateSource( DrogonEvent event ) {
        DrogonEventInfo eventInfo = events.get( event.getCommand( ) );
        if ( eventInfo == null ) {
            logger.warn( "No event info for command " + event.getCommand( ) );
            return false; // no event (should throw exception)
        }
        if ( eventInfo.getSource( ) != event.getSource( ) ) {
            logger.warn( "Source mismatch for command " + event.getCommand( ) + ". Expected source " + getDisplayName( eventInfo.getSource( ) ) + " is not sending source " + getDisplayName( event.getSource( ) ) );
            return false; // doesnt match source (should throw exception)
        }
        return true;
    }
    
    private String getDisplayName( long client ) {
        return clients.get( client ) + " [" + client + "]";
    }
}
