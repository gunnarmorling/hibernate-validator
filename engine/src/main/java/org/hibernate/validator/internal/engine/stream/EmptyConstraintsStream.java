/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.engine.stream;

import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.GroupWithInheritance;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;

/**
 * @author Gunnar Morling
 */
public class EmptyConstraintsStream implements ConstraintsStream {

	public static final EmptyConstraintsStream INSTANCE = new EmptyConstraintsStream();

	private EmptyConstraintsStream() {
	}
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public void next() {
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupWithInheritance currentGroupWithInheritance() {
		return null;
	}

	@Override
	public Group currentGroup() {
		return null;
	}

	@Override
	public MetaConstraint<?> currentConstraint() {
		return null;
	}

	@Override
	public boolean isPartOfRedefinedDefaultGroupSequence() {
		return false;
	}
}
