/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.engine.stream;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.GroupWithInheritance;
import org.hibernate.validator.internal.engine.groups.Sequence;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;

/**
 * @author Gunnar Morling
 *
 * @param <T>
 */
public class RedefinedDefaultGroupSequenceConstraintsStream<T> implements ConstraintsStream {

	private final Set<MetaConstraint<?>> metaConstraints;

	private final Iterator<Sequence> sequenceIterator;
	Iterator<GroupWithInheritance> currentGroupWithInheritanceIterator = Collections.emptyIterator();
	Iterator<Group> currentGroupIterator = Collections.emptyIterator();
	Iterator<MetaConstraint<?>> currentConstraintsIterator = Collections.emptyIterator();
	GroupWithInheritance currentGroupWithInheritance;
	Group currentGroup;
	MetaConstraint<?> current;
	GroupWithInheritance nextGroupWithInheritance;
	Group nextGroup;
	MetaConstraint<?> next;

	public RedefinedDefaultGroupSequenceConstraintsStream(BeanMetaData<? super T> beanMetaData, T state) {
		sequenceIterator = beanMetaData.getDefaultValidationSequence( state );
		metaConstraints = beanMetaData.getMetaConstraints();

		prepareNext();
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public void next() {
		if ( next != null ) {
			current = next;
			currentGroup = nextGroup;
			currentGroupWithInheritance = nextGroupWithInheritance;
			prepareNext();
		}
		else {
			throw new IllegalStateException( "No more constraints" );
		}
	}

	@Override
	public GroupWithInheritance currentGroupWithInheritance() {
		return currentGroupWithInheritance;
	}

	@Override
	public Group currentGroup() {
		return currentGroup;
	}

	@Override
	public MetaConstraint<?> currentConstraint() {
		return current;
	}

	private void prepareNext() {
		if ( currentConstraintsIterator.hasNext() ) {
			next = currentConstraintsIterator.next();
			return;
		}
		else if ( currentGroupIterator.hasNext() ) {
			nextGroup = currentGroupIterator.next();
			currentConstraintsIterator = metaConstraints.iterator();
		}
		else if ( currentGroupWithInheritanceIterator.hasNext() ) {
			nextGroupWithInheritance = currentGroupWithInheritanceIterator.next();
			currentGroupIterator = nextGroupWithInheritance.iterator();
			nextGroup = currentGroupIterator.next();
			currentConstraintsIterator = metaConstraints.iterator();
		}
		else if ( sequenceIterator.hasNext() ) {
			currentGroupWithInheritanceIterator = sequenceIterator.next().iterator();
			nextGroupWithInheritance = currentGroupWithInheritanceIterator.next();
			currentGroupIterator = nextGroupWithInheritance.iterator();
			nextGroup = currentGroupIterator.next();
			currentConstraintsIterator = metaConstraints.iterator();
		}

		if( currentConstraintsIterator.hasNext() ) {
			next = currentConstraintsIterator.next();
		}
		else {
			next = null;
			nextGroup = null;
		}
	}

	@Override
	public boolean isPartOfRedefinedDefaultGroupSequence() {
		return true;
	}
}