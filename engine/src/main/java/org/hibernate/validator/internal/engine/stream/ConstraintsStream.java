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
 * A sequence of constraints to be validated.
 *
 * @author Gunnar Morling
 */
public interface ConstraintsStream {
	boolean hasNext();
	void next();
	GroupWithInheritance currentGroupWithInheritance();
	Group currentGroup();
	MetaConstraint<?> currentConstraint();
	boolean isPartOfRedefinedDefaultGroupSequence();
}