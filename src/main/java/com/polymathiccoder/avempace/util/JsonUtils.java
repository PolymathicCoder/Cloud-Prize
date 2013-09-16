package com.polymathiccoder.avempace.util;

import java.lang.reflect.Type;

import org.apache.commons.lang3.ClassUtils;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public final class JsonUtils {
// Static fields
	private static final Gson GSON;

	static {
		GSON = new GsonBuilder()
				.enableComplexMapKeySerialization()
				.create();
	}

// Static behavior
	public static <T> String toJson(final Class<? extends T> clazz, final Object value) {
		Preconditions.checkArgument(ClassUtils.isAssignable(clazz, value.getClass()));

    	Type type = new TypeToken<T>() {}.getType();
    	return GSON.toJson(value, type);
    }

    public static <T> T fromJson(final Class<? extends T> clazz, final Object value) {
    	Type type = new TypeToken<T>() {}.getType();
    	return GSON.fromJson(value.toString(), type);
    }
}
