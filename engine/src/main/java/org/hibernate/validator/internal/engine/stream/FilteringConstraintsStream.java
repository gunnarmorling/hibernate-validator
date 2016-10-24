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
 *
 */
public class FilteringConstraintsStream implements ConstraintsStream {

	private final ConstraintsStream delegate;

	private GroupWithInheritance groupWithInheritance;
	private Group group;
	private boolean isPartOfRedefinedDefaultGroupSequence;
	private MetaConstraint<?> constraint;

	private MetaConstraint<?> nextConstraint;
	private GroupWithInheritance nextGroupWithInheritance;
	private Group nextGroup;
	private boolean nextIsPartOfRedefinedDefaultGroupSequence;

	private final ConstraintsStreamFilter filter;

	public FilteringConstraintsStream(ConstraintsStream delegate, ConstraintsStreamFilter filter) {
		this.delegate = delegate;
		this.filter = filter;
		prepareNext();
	}

	private void prepareNext() {
		while ( delegate.hasNext() ) {
			delegate.next();
			MetaConstraint<?> candidate = delegate.currentConstraint();

			if ( filter.matches( candidate, delegate.currentGroup() ) ) {
				nextGroupWithInheritance = delegate.currentGroupWithInheritance();
				nextGroup = delegate.currentGroup();
				nextIsPartOfRedefinedDefaultGroupSequence = delegate.isPartOfRedefinedDefaultGroupSequence();
				nextConstraint = delegate.currentConstraint();

				return;
			}
		}

		nextConstraint = null;
	}

	@Override
	public boolean hasNext() {
		return nextConstraint != null;
	}

	@Override
	public void next() {
		if ( nextConstraint != null ) {
			constraint = nextConstraint;
			groupWithInheritance = nextGroupWithInheritance;
			group = nextGroup;
			isPartOfRedefinedDefaultGroupSequence = nextIsPartOfRedefinedDefaultGroupSequence;
			prepareNext();
		}
		else {
			throw new IllegalStateException();
		}
	}

	@Override
	public GroupWithInheritance currentGroupWithInheritance() {
		return groupWithInheritance;
	}

	@Override
	public Group currentGroup() {
		return group;
	}

	@Override
	public MetaConstraint<?> currentConstraint() {
		return constraint;
	}

	@Override
	public boolean isPartOfRedefinedDefaultGroupSequence() {
		return isPartOfRedefinedDefaultGroupSequence;
	}
}
