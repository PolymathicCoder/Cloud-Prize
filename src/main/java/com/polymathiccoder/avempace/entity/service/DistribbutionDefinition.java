package com.polymathiccoder.avempace.entity.service;

import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.google.api.client.util.Sets;
import com.polymathiccoder.avempace.config.Region;
import com.polymathiccoder.avempace.util.IPUtils;
import com.polymathiccoder.avempace.util.geo.GeoIPUtils;
import com.polymathiccoder.avempace.util.geo.GeoLocation;
import com.polymathiccoder.avempace.util.geo.GeoUtils;
import com.polymathiccoder.avempace.util.geo.GeoUtils.DistanceCalculationFormula;

@AutoProperty
public class DistribbutionDefinition {
// Static fields
	private static Region THE_CLOSEST_REGION = null;

// Fields
	private final Region primaryRegion;
	private final Set<Region> secondaryRegions;
	private final boolean propagatedAcrossAllRegions;

// Life cycle
	// Constructors
	public DistribbutionDefinition(Region primaryRegion, Set<Region> secondaryRegions, boolean propagatedAcrossAllRegions) {
		this.primaryRegion = primaryRegion;
		this.secondaryRegions = secondaryRegions;
		this.propagatedAcrossAllRegions = propagatedAcrossAllRegions;
		THE_CLOSEST_REGION = lookupTheClosestRegion();
	}

// Behavior
	public Region lookupTheClosestRegion() {
		if (THE_CLOSEST_REGION == null) {
			Set<Region> relevantRegions = Sets.newHashSet();
			relevantRegions.add(primaryRegion);
			relevantRegions.addAll(secondaryRegions);

			final String currentPublicIP = IPUtils.lookupPublicIP();
			final GeoLocation currentGeoLocation = GeoIPUtils.lookupGeoLocation(currentPublicIP);

			Region theClosestRegion = primaryRegion;
			try {
				double theShortestDistance = Double.MAX_VALUE;
				for (final Region region : relevantRegions) {
					final double distance = GeoUtils.calculateOrthodromicDistance(region.getGeoLocation(), currentGeoLocation, DistanceCalculationFormula.HAVERSINE);
					if (distance <= theShortestDistance) {
						theClosestRegion = region;
						theShortestDistance = distance;
					}
				}
			} catch (final Exception exception) {
				theClosestRegion = primaryRegion;
			}

			return theClosestRegion;
		} else {
			return THE_CLOSEST_REGION;
		}
	}

// Accessors and mutators
	public Region getPrimaryRegion() { return primaryRegion; }

	public Set<Region> getSecondaryRegions() { return secondaryRegions; }

	public boolean isPropagatedAcrossAllRegions() { return propagatedAcrossAllRegions; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
