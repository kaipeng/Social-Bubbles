package com.social.bubbles.fsquery;

import java.util.Calendar;
import java.util.Date;

import com.social.bubbles.Bubble;

public class FoursquareObject extends Bubble{
	protected long createdAt;

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getCreatedAt() {
		return createdAt;
	}
}
