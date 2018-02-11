package com.packtpub.e4.clock.ui.internal;

import java.util.TimeZone;

public class TimeZoneOffsetColumn extends TimeZoneColumn {

	@Override
	public String getText(Object element) {
		if (element instanceof TimeZone) {
			int offset = -((TimeZone) element).getOffset(System.currentTimeMillis());
			return String.valueOf(String.valueOf(offset / 3600000) + "h");
		}
		return "";
	}

	@Override
	public String getTitle() {
		return "Offset";
	}
	
	@Override
	public int getWidth() {
		return 100;
	}


}
