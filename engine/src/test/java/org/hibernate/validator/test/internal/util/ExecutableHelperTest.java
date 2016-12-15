/*
* JBoss, Home of Professional Open Source
* Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual contributors
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
package org.hibernate.validator.test.internal.util;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;

import org.hibernate.validator.internal.util.ExecutableHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
/**
 * Unit test for {@link ExecutableHelper}.
 *
 * @author Gunnar Morling
 */
public class ExecutableHelperTest {

	private ExecutableHelper executableHelper;

	@BeforeMethod
	public void setupExecutableHelper() {
		executableHelper = new ExecutableHelper();
	}

	@Test
	public void methodFromSubTypeOverridesSuperTypeMethod() throws Exception {

		Method methodFromBase = Foo.class.getDeclaredMethod( "zap" );
		Method methodFromImpl = Bar.class.getDeclaredMethod( "zap" );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodFromSubTypeOverridesInterfaceTypeMethod() throws Exception {

		Method methodFromBase = IBaz.class.getDeclaredMethod( "zap" );
		Method methodFromImpl = Baz.class.getDeclaredMethod( "zap" );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodFromSuperTypeDoesNotOverrideSubTypeMethod() throws Exception {

		Method methodFromBase = Foo.class.getDeclaredMethod( "zap" );
		Method methodFromImpl = Bar.class.getDeclaredMethod( "zap" );

		assertFalse( executableHelper.overrides( methodFromBase, methodFromImpl ) );
	}

	@Test
	public void methodWithDifferentNameDoesNotOverride() throws Exception {

		Method methodFromBase = Foo.class.getDeclaredMethod( "zap" );
		Method methodFromImpl = Bar.class.getDeclaredMethod( "zip" );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodWithDifferentParameterTypesDoesNotOverride() throws Exception {

		Method methodFromBase = Foo.class.getDeclaredMethod( "zap" );
		Method methodFromImpl = Bar.class.getDeclaredMethod( "zap", int.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodDefinedInOtherTypeHierarchyDoesNotOverride() throws Exception {

		Method first = Foo.class.getDeclaredMethod( "zap" );
		Method other = Baz.class.getDeclaredMethod( "zap" );

		assertFalse( executableHelper.overrides( other, first ) );
	}

	@Test
	public void methodWithNarrowedParameterTypeDoesNotOverride() throws Exception {

		Method methodFromBase = SimpleServiceBase.class.getDeclaredMethods()[0];

		Method methodFromImpl = SimpleServiceImpl1.class.getDeclaredMethod( "doSomething", Number.class );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );

		methodFromImpl = SimpleServiceImpl1.class.getDeclaredMethod( "doSomething", Integer.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodWithIntermediateClass() throws Exception {

		Method methodFromBase = SimpleServiceBase.class.getDeclaredMethods()[0];

		Method methodFromImpl = SimpleServiceImpl2.class.getDeclaredMethod( "doSomething", Number.class );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );

		methodFromImpl = SimpleServiceImpl2.class.getDeclaredMethod( "doSomething", Integer.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodWithGenerics() throws Exception {

		Method methodFromBase = GenericServiceBase.class.getDeclaredMethods()[0];

		Method methodFromImpl = GenericServiceImpl1.class.getDeclaredMethod( "doSomething", Number.class );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );

		methodFromImpl = GenericServiceImpl1.class.getDeclaredMethod( "doSomething", Integer.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodWithGenericsAndIntermediateClass() throws Exception {

		Method methodFromBase = GenericServiceBase.class.getDeclaredMethods()[0];

		Method methodFromImpl = GenericServiceImpl2.class.getDeclaredMethod( "doSomething", Number.class );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );

		methodFromImpl = GenericServiceImpl2.class.getDeclaredMethod( "doSomething", Integer.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodWithGenericsAndMultipleIntermediateClasses() throws Exception {

		Method methodFromBase = GenericServiceBase.class.getDeclaredMethods()[0];

		Method methodFromImpl = GenericServiceImpl3.class.getDeclaredMethod( "doSomething", Number.class );

		assertTrue( executableHelper.overrides( methodFromImpl , methodFromBase ) );

		methodFromImpl = GenericServiceImpl2.class.getDeclaredMethod( "doSomething", Integer.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodWithParameterizedSubType() throws Exception {

		Method methodFromBase = GenericServiceBase.class.getDeclaredMethods()[0];

		Method methodFromImpl = ParameterizedSubType.class.getDeclaredMethod( "doSomething", Object.class );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );

		methodFromImpl = ParameterizedSubType.class.getDeclaredMethod( "doSomething", Integer.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodWithGenericInterface() throws Exception {

		Method methodFromBase = GenericInterface.class.getDeclaredMethods()[0];

		Method methodFromImpl = GenericInterfaceImpl1.class.getDeclaredMethod( "doSomething", Number.class );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );

		methodFromImpl = GenericInterfaceImpl1.class.getDeclaredMethod( "doSomething", Integer.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	@Test
	public void methodWithWildcard() throws Exception {

		Method methodFromBase = WildcardInterface.class.getDeclaredMethods()[0];

		Method methodFromImpl = WildcardInterfaceImpl.class.getDeclaredMethod( "doSomething", Integer.class );

		assertTrue( executableHelper.overrides( methodFromImpl, methodFromBase ) );

		methodFromImpl = WildcardInterfaceImpl.class.getDeclaredMethod( "doSomething", Long.class );

		assertFalse( executableHelper.overrides( methodFromImpl, methodFromBase ) );
	}

	public abstract static class GenericServiceBase<T> {
		public abstract void doSomething(T t);
	}

	public static class GenericServiceImpl1 extends GenericServiceBase<Number> {
		@Override
		public void doSomething(Number t) {
		}

		public void doSomething(Integer t) {
		}
	}

	public abstract static class GenericServiceBaseExt extends GenericServiceBase<Number> {
	}

	public abstract static class GenericParameterizedServiceBaseExt<D> extends GenericServiceBase<D> {
	}

	public static class GenericServiceImpl2 extends GenericServiceBaseExt {
		@Override
		public void doSomething(Number t) {
		}

		public void doSomething(Integer t) {
		}
	}

	public static class GenericServiceImpl3 extends GenericParameterizedServiceBaseExt<Number> {
		@Override
		public void doSomething(Number t) {
		}

		public void doSomething(Integer t) {
		}
	}

	public abstract static class SimpleServiceBase {
		public abstract void doSomething(Number t);
	}

	public abstract static class SimpleServiceBaseExt extends SimpleServiceBase {
	}

	public static class SimpleServiceImpl1 extends SimpleServiceBase {
		@Override
		public void doSomething(Number t) {
		}

		public void doSomething(Integer t) {
		}
	}

	public static class SimpleServiceImpl2 extends SimpleServiceBaseExt {
		@Override
		public void doSomething(Number t) {
		}

		public void doSomething(Integer t) {
		}
	}

	public static class ParameterizedSubType<U> extends GenericServiceBase<U> {

		@Override
		public void doSomething(U t) {
		}

		public void doSomething(Integer t) {
		}
	}

	public interface GenericInterface<T> {
		void doSomething(T t);
	}

	public static class GenericInterfaceImpl1 implements GenericInterface<Number> {
		@Override
		public void doSomething(Number t) {
		}

		public void doSomething(Integer t) {
		}
	}

	public interface WildcardInterface<T extends Number> {
		void doSomething(T t);
	}

	public static class WildcardInterfaceImpl implements WildcardInterface<Integer> {
		@Override
		public void doSomething(Integer t) {
		}

		public void doSomething(Long t) {
		}
	}

	public static class Foo {

		public Foo() {
		}

		public void zap() {
		}
	}

	public static class Bar extends Foo {

		public Bar() {
		}

		public void Foo() {
		}

		public void zip() {
		}

		public void zap(int i) {
		}

		@Override
		public void zap() {
		}
	}

	public interface IBaz {
		void zap();
	}

	public static class Baz implements IBaz {

		@Override
		public void zap() {
		}
	}
}
