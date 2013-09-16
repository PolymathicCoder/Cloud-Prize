package com.polymathiccoder.avempace.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.polymathiccoder.avempace.util.error.UtilsException;

public final class IPUtils {
// Static fields
	private static final String VALID_IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static URL PUBLIC_IP_LOOKUP_SERVICE_URL;
	static {
		try {
			PUBLIC_IP_LOOKUP_SERVICE_URL = new URL("http://checkip.amazonaws.com");
		} catch (MalformedURLException e) {
        	throw new IpUtilsException(
        			String.format(IpUtilsException.ERROR_PUBLIC_IP__UNREACHABLE_SERVICE));
		}
	}

// Static behavior
    public static String lookupPublicIP() {
        try (
        	final InputStreamReader inputStreamReader = new InputStreamReader(PUBLIC_IP_LOOKUP_SERVICE_URL.openStream());
        	final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            return bufferedReader.readLine();
        } catch (final IOException ioException) {
        	throw new IpUtilsException(
        			String.format(IpUtilsException.ERROR_PUBLIC_IP__UNREACHABLE_SERVICE));
        }
    }

    public static boolean validateIP(final String ip) {
    	final Pattern pattern = Pattern.compile(VALID_IP_PATTERN);
    	final Matcher matcher = pattern.matcher(ip);
    	return matcher.matches();
    }

// Types
    @SuppressWarnings("serial")
	public static final class IpUtilsException extends UtilsException {
    // Static fields
        public static final String ERROR_PUBLIC_IP__UNREACHABLE_SERVICE = "Public IP Resolution: Service is unreachable";

    // Life cycle
    	private IpUtilsException(final String message) {
    		super(message);
    	}

    	private IpUtilsException(final String message, final Throwable cause) {
    		super(message, cause);
    	}
    }
}
