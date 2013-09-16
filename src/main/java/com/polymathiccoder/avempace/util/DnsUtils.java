package com.polymathiccoder.avempace.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.polymathiccoder.avempace.util.error.UtilsException;

public final class DnsUtils {
// Static behavior
    public static String reverseLookup(final String ip) throws DnsUtilsException {
    	Preconditions.checkArgument(!StringUtils.isEmpty(ip) && IPUtils.validateIP(ip));

        try {
            final Resolver resolver = new ExtendedResolver();

            final Name name = ReverseMap.fromAddress(ip);
            final Record record = Record.newRecord(name, Type.PTR, DClass.IN);
            final Message query = Message.newQuery(record);
            final Message response = resolver.send(query);
            final Record[] answers = response.getSectionArray(Section.ANSWER);
            return answers[0].rdataToString();
        } catch (final IOException ioException) {
            throw new DnsUtilsException(
            		String.format(DnsUtilsException.ERROR_UNRESOLVABLE_IP, ip));
        }
    }

    public static String lookup(final String hostname) throws DnsUtilsException {
    	Preconditions.checkArgument(!StringUtils.isEmpty(hostname));

        try {
        	String ip = StringUtils.EMPTY;
            final Lookup lookup = new Lookup(hostname, Type.A);
            final Record[] records = lookup.run();

            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                for (Record record : records) {
                    if (record instanceof ARecord) {
                        ip = ((ARecord) record).getAddress().getHostAddress();
                    }
                }
            }
            return ip;
        }catch (final TextParseException textParseException) {
            throw new DnsUtilsException(
            		String.format(DnsUtilsException.ERROR_UNRESOLVABLE_HOSTNAME, hostname));
        }
    }

// Types
    @SuppressWarnings("serial")
	public static class DnsUtilsException extends UtilsException {
    // Static fields
		public static final String ERROR_UNRESOLVABLE_HOSTNAME = "DNS Reverse-Lookup failed: Could not resolve the hostname '%s' to an IP";
        public static final String ERROR_UNRESOLVABLE_IP = "DNS Lookup failed: Could not resolve the IP '%s' to a hostname";
    // Life cycle
        protected DnsUtilsException(final String message) {
			super(message);
		}

    	protected DnsUtilsException(final String message, final Throwable cause) {
    		super(message, cause);
    	}
    }
}

