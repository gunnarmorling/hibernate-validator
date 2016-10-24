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
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;

/**
 * @author Gunnar Morling
 */
public class DefaultGroupValidationConstraintStream<T> implements ConstraintsStream {

	private final BeanMetaDataManager beanMetaDataManager;
	private final T state;
	private final Iterator<Class<? super T>> hierarchyIterator;

	private ConstraintsStream currentDelegate;
	private ConstraintsStream nextDelegate;
	private boolean foundRedefinedDefaultGroupSequence = false;

	public DefaultGroupValidationConstraintStream(BeanMetaDataManager beanMetaDataManager, Class<T> clazz, T state) {
		this.beanMetaDataManager = beanMetaDataManager;
		this.state = state;
		this.hierarchyIterator = beanMetaDataManager.getBeanMetaData( clazz ).getClassHierarchy().iterator();

		currentDelegate = getNext();
		nextDelegate = getNext();
	}

	private ConstraintsStream getNext() {
		ConstraintsStream next;
		while ( hierarchyIterator.hasNext() && !foundRedefinedDefaultGroupSequence ) {
			Class<? super T> clazz = hierarchyIterator.next();
			BeanMetaData<? super T> beanMetaData = beanMetaDataManager.getBeanMetaData( clazz );

			// if the current class redefined the default group sequence, this sequence has to be applied to all the class hierarchy.
			if ( beanMetaData.defaultGroupSequenceIsRedefined() ) {
				foundRedefinedDefaultGroupSequence = true;

				next = new FilteringConstraintsStream (
						new RedefinedDefaultGroupSequenceConstraintsStream<>( beanMetaData, state ),
						(c, g) -> c.getGroupList().contains( g.getDefiningClass() )
				);
			}
			else {
				next = new DirectConstraintsStream( beanMetaData );
			}

			if ( next.hasNext() ) {
				return next;
			}

		}

		return EmptyConstraintsStream.INSTANCE;
	}

	@Override
	public boolean hasNext() {
		return currentDelegate.hasNext() || nextDelegate.hasNext();
	}

	@Override
	public void next() {
		if ( !currentDelegate.hasNext() ) {
			currentDelegate = nextDelegate;
			nextDelegate = getNext();
		}

		currentDelegate.next();
	}

	@Override
	public GroupWithInheritance currentGroupWithInheritance() {
		return currentDelegate.currentGroupWithInheritance();
	}

	@Override
	public Group currentGroup() {
		return currentDelegate.currentGroup();
	}

	@Override
	public MetaConstraint<?> currentConstraint() {
		return currentDelegate.currentConstraint();
	}

	@Override
	public boolean isPartOfRedefinedDefaultGroupSequence() {
		return currentDelegate.isPartOfRedefinedDefaultGroupSequence();
	}
}
