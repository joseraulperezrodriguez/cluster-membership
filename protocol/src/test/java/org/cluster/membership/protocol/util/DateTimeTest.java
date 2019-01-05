package org.cluster.membership.protocol.util;

import java.util.TimeZone;

import org.cluster.membership.common.model.util.DateTime;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class DateTimeTest 
    extends TestCase
{
		
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DateTimeTest( String testName )
    {
        super( testName );
        
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DateTimeTest.class );
    }

    public void testLocalTimeConversionPOC() {
		String havana = "America/Havana";
		String madrid = "Europe/Madrid";
		long hour = 1000*60*60;
		TimeZone madridTZ = TimeZone.getTimeZone(madrid);			
		TimeZone havanaTZ = TimeZone.getTimeZone(havana);
		
		long nowMadrid = System.currentTimeMillis();		
		long offset = madridTZ.getOffset(nowMadrid);
		long nowUTC = nowMadrid - offset;
		
		long nowHavana = havanaTZ.getOffset(nowUTC) + nowUTC;
		assert(nowHavana == nowMadrid - (hour*6));
    }
    
    public void testLocalTimeConversion() {
    	String havana = "America/Havana";
		String madrid = "Europe/Madrid";
		
		long hour = 1000*60*60;
		TimeZone madridTZ = TimeZone.getTimeZone(madrid);			
		TimeZone havanaTZ = TimeZone.getTimeZone(havana);

		long nowMadrid = System.currentTimeMillis();
		
		long nowHavana = DateTime.localTime(nowMadrid, madridTZ, havanaTZ);
		
		assert(nowHavana == nowMadrid - (hour*6));
		
    }
    
}
