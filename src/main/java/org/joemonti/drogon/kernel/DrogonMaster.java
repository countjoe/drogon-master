/*
 * Drogon : DrogonMaster.java
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

package org.joemonti.drogon.kernel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.joemonti.drogon.kernel.module.DrogonModule;
import org.joemonti.drogon.module.weblogger.WebLoggerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class DrogonMaster implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger( DrogonMaster.class );
    
    private static final Class<DrogonModule>[] MODULES = new Class[] {
        WebLoggerModule.class
    };
    
    private static Map<String ,DrogonModule> modules = new HashMap<String, DrogonModule>( );
    
    public static DrogonModule getModule( Class<DrogonModule> clazz ) {
        return getModule( clazz.getCanonicalName( ) );
    }
    
    public static DrogonModule getModule( String name ) {
        return modules.get( name );
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed( ServletContextEvent arg0 ) {
        for ( DrogonModule module: modules.values( ) ) {
            module.shutdown( );
        }
        modules.clear( );
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized( ServletContextEvent arg0 ) {
        for ( Class<DrogonModule> moduleClass: MODULES ) {
            String className = moduleClass.getCanonicalName( );
            try {
                DrogonModule module = moduleClass.newInstance( );
                
                modules.put( className, module );
            } catch ( InstantiationException ex ) {
                logger.error( "Error instantiating " + className, ex );
            } catch ( IllegalAccessException ex ) {
                logger.error( "Error accessing " + className, ex );
            }
        }
    }

}
