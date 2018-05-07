/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.hkstlr.blogbox.control;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import javax.inject.Named;

/**
 *
 * @author henry.kastler
 */
@Named
public class DateFormatter {

    
    /**
     * yyyy MMM dd HH:mm
     */
    public static final DateTimeFormatter  sdf = DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm");
    
    /**
     * yyyy
     */
    public static final DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
    
    /**
     * MM
     */
    public static final DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("d");
    
    private static final DateTimeFormatter format8chars = DateTimeFormatter.ofPattern("YYYYMMdd");
    public String format8chars() {
    	
    	String[] aryDate = this.date.toString().trim().split(" ");
    	return DateFormatter.format8chars.format(
				LocalDateTime.ofInstant(this.dateInstant, 
						ZoneId.of(TimeZone.getTimeZone(aryDate[aryDate.length-2].toString().trim()).getID())
				));
    }
    
    public static final DateTimeFormatter yearMonthFormat = DateTimeFormatter.ofPattern("yyyyMMM");
    
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     * thanks to https://github.com/jarrodhroberson/Stack-Overflow/blob/master/src/main/java/com/stackoverflow/Q2597083.java
     */
    public static final DateTimeFormatter jsFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	public String formatjsFormat() {
	    	
	    	String[] aryDate = this.date.toString().trim().split(" ");
	    	return DateFormatter.jsFormat.format(
					LocalDateTime.ofInstant(this.dateInstant, 
							ZoneId.of(TimeZone.getTimeZone(aryDate[aryDate.length-2].toString().trim()).getID())
					));
	    }
    
    
    public static final DateTimeFormatter datePickerFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    
    private Date date;
    private Instant dateInstant;
    
    public DateFormatter() {
        
    }

    public DateFormatter(Date date) {
		super();
		this.date = date;
		this.dateInstant = date.toInstant();
	}
    
	public DateFormatter(Date date, Instant dateInstant) {
		super();
		this.date = date;
		this.dateInstant = dateInstant;
	}



	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


	public Instant getDateInstant() {
		return dateInstant;
	}


	public void setDateInstant(Instant dateInstant) {
		this.dateInstant = dateInstant;
	}
	
	

}