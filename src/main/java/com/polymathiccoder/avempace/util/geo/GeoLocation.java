package com.polymathiccoder.avempace.util.geo;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GeoLocation {
// Fields
	private final double latitude;
	private final double longitude;

// Life cycle
	public GeoLocation(final double latitude, final double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

// Accessors and mutators
	public double getLatitude() { return latitude; }

	public double getLongitude() { return longitude; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}