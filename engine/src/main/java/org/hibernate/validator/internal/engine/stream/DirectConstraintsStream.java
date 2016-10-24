/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.engine.stream;

import java.util.Iterator;

import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.GroupWithInheritance;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;

public class DirectConstraintsStream implements ConstraintsStream {

	private final Iterator<MetaConstraint<?>> metaConstraints;
	private MetaConstraint<?> current;

	public DirectConstraintsStream(BeanMetaData<?> beanMetaData) {
		metaConstraints = beanMetaData.getDirectMetaConstraints().iterator();
	}

	@Override
	public void next() {
		current = metaConstraints.next();
	}

	@Override
	public boolean isPartOfRedefinedDefaultGroupSequence() {
		return false;
	}

	@Override
	public boolean hasNext() {
		return metaConstraints.hasNext();
	}

	@Override
	public GroupWithInheritance currentGroupWithInheritance() {
		return GroupWithInheritance.DEFAULT;
	}

	@Override
	public Group currentGroup() {
		return Group.DEFAULT_GROUP;
	}

	@Override
	public MetaConstraint<?> currentConstraint() {
		return current;
	}
}