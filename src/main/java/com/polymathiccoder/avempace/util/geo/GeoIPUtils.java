package com.polymathiccoder.avempace.util.geo;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.City;
import com.polymathiccoder.avempace.util.error.UtilsException;

public class GeoIPUtils {
// Static fields
	private static final File GEO_IP_CITY_DB_FILE = new File("/Users/abdel/Dev/workspace/sts/nimble/src/main/resources/META-INF/geoIPDB/GeoLite2-City.mmdb");
	@SuppressWarnings("unused")
	private static final File GEO_IP_COUNTRY_DB_FILE = new File("/Users/abdel/Dev/workspace/sts/nimble/src/main/resources/META-INF/geoIPDB/GeoLite2-City.mmdb");

// Static behavior
	public static GeoLocation lookupGeoLocation(final String ip) {
		try (
			final DatabaseReader databaseReader = new DatabaseReader(GEO_IP_CITY_DB_FILE);
		) {
			final City city = databaseReader.city(InetAddress.getByName(ip));
			return new GeoLocation(city.getLocation().getLatitude(), city.getLocation().getLongitude());
		} catch (final IOException ioException) {
			throw new IpUtilsException(
        			String.format(IpUtilsException.ERROR_GEO__UNREACHABLE_SERVICE));
		} catch (final GeoIp2Exception geoIp2Exception) {
			throw new IpUtilsException(
					String.format(IpUtilsException.ERROR_GEO__NO_RESOLUTION_OF_IP_TO_GEO_LOCATION, ip));
		}
	}

// Types
    @SuppressWarnings("serial")
	public static final class IpUtilsException extends UtilsException {
    // Static fields
        public static final String ERROR_GEO__UNREACHABLE_SERVICE = "Geo IP Resolution: Service is unreachable";
        public static final String ERROR_GEO__NO_RESOLUTION_OF_IP_TO_GEO_LOCATION = "Geo IP Resolution: Could not resolve the IP '%s' to a location";

    // Life cycle
    	private IpUtilsException(final String message) {
    		super(message);
    	}

    	private IpUtilsException(final String message, final Throwable cause) {
    		super(message, cause);
    	}
    }
}
