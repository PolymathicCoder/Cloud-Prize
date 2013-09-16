package com.polymathiccoder.avempace.entity.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.lang3.StringUtils;

import com.polymathiccoder.avempace.config.Region;

public class CrossRegionRepository<T> implements InvocationHandler {
	private final Repository<T> repository;

	@SuppressWarnings("unchecked")
	public static <T> Repository<T> newInstance(final Repository<T> repository) {
		return (Repository<T>) Proxy.newProxyInstance(
				repository.getClass().getClassLoader(),
				repository.getClass().getInterfaces(),
				new CrossRegionRepository<T>(repository));
	}

	private CrossRegionRepository(Repository<T> repository) {
		this.repository = repository;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result;
		try {
///System.out.println(method.getName() + (args.length > 0 && args[args.length - 1] instanceof Region ? args[args.length - 1] : StringUtils.EMPTY));
			result = method.invoke(repository, args);
		} catch (final InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getTargetException();
		} catch (final Exception exception) {
			throw new RuntimeException("unexpected invocation exception: " + exception.getMessage());
		} finally {
		}
		return result;
	}
}
