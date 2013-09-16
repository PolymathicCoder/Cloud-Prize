package com.polymathiccoder.avempace.config;

import com.polymathiccoder.avempace.entity.service.RepositoryFactory;

import dagger.ObjectGraph;

public class Avempace {
	public static RepositoryFactory getRepositoryFactory() {
		final ObjectGraph objectGraph = ObjectGraph.create(new AvempaceModule());
		objectGraph.injectStatics();

		return objectGraph.get(RepositoryFactory.class);
	}
}
