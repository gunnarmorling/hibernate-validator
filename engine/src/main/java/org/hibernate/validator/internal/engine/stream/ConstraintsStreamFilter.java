/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.engine.stream;

import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;

/**
 * @author Gunnar Morling
 *
 */
@FunctionalInterface
public interface ConstraintsStreamFilter {

	boolean matches(MetaConstraint<?> constraint, Group group);
}
