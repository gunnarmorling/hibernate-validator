/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.xml;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedType;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;
import static org.hibernate.validator.internal.util.CollectionHelper.newHashSet;

/**
 * Builder for constraint types.
 *
 * @author Hardy Ferentschik
 */
class ConstrainedTypeBuilder {

	private ConstrainedTypeBuilder() {
	}

	static ConstrainedType buildConstrainedType(ClassType classType,
													   Class<?> beanClass,
													   String defaultPackage,
													   ConstraintHelper constraintHelper,
													   AnnotationProcessingOptionsImpl annotationProcessingOptions,
													   Map<Class<?>, List<Class<?>>> defaultSequences,
													   ClassLoader externalClassLoader) {
		if ( classType == null ) {
			return null;
		}

		// group sequence
		List<Class<?>> groupSequence = createGroupSequence( classType.getGroupSequence(), defaultPackage, externalClassLoader );
		if ( !groupSequence.isEmpty() ) {
			defaultSequences.put( beanClass, groupSequence );
		}

		// constraints
		ConstraintLocation constraintLocation = ConstraintLocation.forClass( beanClass );
		Set<MetaConstraint<?>> metaConstraints = newHashSet();
		for ( ConstraintType constraint : classType.getConstraint() ) {
			MetaConstraint<?> metaConstraint = MetaConstraintBuilder.buildMetaConstraint(
					constraintLocation,
					constraint,
					java.lang.annotation.ElementType.TYPE,
					defaultPackage,
					constraintHelper,
					null,
					externalClassLoader
			);
			metaConstraints.add( metaConstraint );
		}

		// ignore annotation
		if ( classType.getIgnoreAnnotations() != null ) {
			annotationProcessingOptions.ignoreClassLevelConstraintAnnotations(
					beanClass,
					classType.getIgnoreAnnotations()
			);
		}

		return new ConstrainedType(
				ConfigurationSource.XML,
				constraintLocation,
				metaConstraints
		);
	}

	private static List<Class<?>> createGroupSequence(GroupSequenceType groupSequenceType, String defaultPackage, ClassLoader externalClassLoader) {
		List<Class<?>> groupSequence = newArrayList();
		if ( groupSequenceType != null ) {
			for ( String groupName : groupSequenceType.getValue() ) {
				Class<?> group = ClassLoadingHelper.loadClass( groupName, defaultPackage, externalClassLoader );
				groupSequence.add( group );
			}
		}
		return groupSequence;
	}
}


