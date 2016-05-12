package org.dynami.ui.controls.chart;

import java.util.Date;

public class DateValue extends Number implements Comparable<DateValue> {
	private static final long serialVersionUID = 1201792937767567318L;
	private final long time;
	private final int id;
	
	public DateValue(int id, long time){
		this.id = id;
		this.time = time;
	}
	
	public long getTime(){
		return time;
	}
	
	public Date getDate() {
		return new Date(time);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public int compareTo(DateValue o) {
		return Long.compare(time, o.time);
	}

	@Override
	public int intValue() {
		return id;
	}

	@Override
	public long longValue() {
		return (long)id;
	}

	@Override
	public float floatValue() {
		return (float)id;
	}

	@Override
	public double doubleValue() {
		return (double)id;
	}
}
