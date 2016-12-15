/*
* JBoss, Home of Professional Open Source
* Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.validator.internal.util;

import java.lang.reflect.Method;

import com.fasterxml.classmate.Filter;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedMethod;

/**
 * Helper class dealing with executables.
 * <p>
 * Note: this logic has been backported from 5.0 (HV-618), where it's located in {@code ExecutableElement}.
 *
 * @author Gunnar Morling
 */
public class ExecutableHelper {

	/**
	 * Used for resolving type parameters. Thread-safe. Not static to avoid HV-838.
	 */
	private final TypeResolver typeResolver = new TypeResolver();

	public ExecutableHelper() {
	}

	/**
	 * Checks, whether the first method overrides the second method.
	 */
	public boolean overrides(Method first, Method other) {

		Contracts.assertValueNotNull( other, "other" );

		if ( !first.getName().equals( other.getName() ) ) {
			return false;
		}

		if ( first.getParameterTypes().length != other.getParameterTypes().length ) {
			return false;
		}

		if ( !other.getDeclaringClass().isAssignableFrom( first.getDeclaringClass() ) ) {
			return false;
		}

		return parametersResolveToSameTypes( first, other );
	}

	private boolean parametersResolveToSameTypes(Method subTypeMethod, Method superTypeMethod) {
		if ( subTypeMethod.getParameterTypes().length == 0 ) {
			return true;
		}

		ResolvedType resolvedSubType = typeResolver.resolve( subTypeMethod.getDeclaringClass() );

		MemberResolver memberResolver = new MemberResolver( typeResolver );
		memberResolver.setMethodFilter( new SimpleMethodFilter( subTypeMethod, superTypeMethod ) );
		ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve( resolvedSubType, null, null );

		ResolvedMethod[] resolvedMethods = typeWithMembers.getMemberMethods();

		// The ClassMate doc says that overridden methods are flattened to one
		// resolved method. But that is the case only for methods without any
		// generic parameters.
		if ( resolvedMethods.length == 1 ) {
			return true;
		}

		// For methods with generic parameters I have to compare the argument
		// types (which are resolved) of the two filtered member methods.
		for ( int i = 0; i < resolvedMethods[0].getArgumentCount(); i++ ) {

			if ( !resolvedMethods[0].getArgumentType( i ).equals( resolvedMethods[1].getArgumentType( i ) ) ) {
				return false;
			}
		}

		return true;
	}

	/**
	 * A filter implementation filtering methods matching given methods.
	 *
	 * @author Gunnar Morling
	 */
	private static class SimpleMethodFilter implements Filter<RawMethod> {
		private final Method method1;
		private final Method method2;

		private SimpleMethodFilter(Method method1, Method method2) {
			this.method1 = method1;
			this.method2 = method2;
		}

		@Override
		public boolean include(RawMethod element) {
			return element.getRawMember().equals( method1 ) || element.getRawMember().equals( method2 );
		}
	}
}
