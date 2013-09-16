package com.polymathiccoder.avempace.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.commons.lang3.ClassUtils;

import com.google.common.base.Preconditions;
import com.polymathiccoder.avempace.util.error.UtilsException;

public final class BinaryUtils {
// Static behavior
    @SuppressWarnings("unchecked")
	public static <T> ByteBuffer toBinary(final Class<? extends T> clazz, final Object value) {
		Preconditions.checkArgument(ClassUtils.isAssignable(clazz, value.getClass()));

    	final DatumWriter<T> writer = new ReflectDatumWriter<T>(getSchema(clazz));
    	final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // NOPMD
        final Encoder encoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

        try {
			writer.write((T) value, encoder);
			encoder.flush();
        } catch (IOException ioException) {
        	throw new BinaryUtilsException(
        			String.format(BinaryUtilsException.ERROR_SERIALIZATION__UNREACHABLE_SERVICE, clazz));
		}

        byte[] bytes = byteArrayOutputStream.toByteArray();

        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes, 0, bytes.length);
        buffer.position(0);
        return buffer;
    }

    public static <T> T fromBinary(Class<? extends T> clazz, final Object value) {
    	byte[] bytes = ((ByteBuffer) value).array();
        final ReflectDatumReader<T> reader = new ReflectDatumReader<T>(getSchema(clazz));
        final Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        T object;

        try {
			object = reader.read(null, decoder); // NOPMD
		} catch (final IOException ioException) {
			throw new BinaryUtilsException(
					String.format(BinaryUtilsException.ERROR_DESERIALIZATION__UNREACHABLE_SERVICE, clazz));
		}

        return object;
    }

    private static Schema getSchema(final Class<?> clazz) {
    	final ReflectData reflectData = ReflectData.get();
        return reflectData.getSchema(clazz);
    }

// Types
    @SuppressWarnings("serial")
	public static final class BinaryUtilsException extends UtilsException {
    // Static fields
    	public static final String ERROR_DESERIALIZATION__UNREACHABLE_SERVICE = "Deserialization: Could not deserialize into type '%s'";
        public static final String ERROR_SERIALIZATION__UNREACHABLE_SERVICE = "Serialization: Could not serialize type '%s'";

    // Life cycle
    	private BinaryUtilsException(final String message) {
    		super(message);
    	}

    	private BinaryUtilsException(final String message, final Throwable cause) {
    		super(message, cause);
    	}
    }
}
