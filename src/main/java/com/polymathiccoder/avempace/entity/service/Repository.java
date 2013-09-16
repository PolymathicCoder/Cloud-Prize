package com.polymathiccoder.avempace.entity.service;

import java.util.List;

import com.polymathiccoder.avempace.entity.domain.EntityPropertyValueCriteria;
import com.polymathiccoder.avempace.entity.domain.EntityPropertyValueOperations;

public interface Repository<T> {
	T find(final EntityPropertyValueCriteria entityPropertyValueCriteria);

	List<T> findAllBy(final EntityPropertyValueCriteria entityPropertyValuePropertyValueCriteria);
	List<T> findAll();

	void save(final T pojo);

	void update(final EntityPropertyValueCriteria entityPropertyValueCriteria, final EntityPropertyValueOperations entityPropertyValueOperations);

	void remove(final T pojo);
	void removeAll();
}
