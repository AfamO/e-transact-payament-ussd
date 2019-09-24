/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.etransact.ussd.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author HP
 */
public class AppLogger {
    private final Logger log = Logger.getLogger("ussdAppLog");
    String source;

    /**
     *
     * @param c
     */
    public AppLogger(Class c){
        source = c.getSimpleName();
    }
    public void log(String message){
        log.info(source+"::"+message);
    }
    public void fatal(String message){
        log.fatal(source+"::"+message);
    }
    public void error(String message){
        log.error(source+"::"+message);
    }
    public void warn(String message){
        log.warn(source+"::"+message);
    }
    public void log(Level level, String message){
        log.log(level, source+"::"+message);
    }
    
}
