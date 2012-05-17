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
package org.hibernate.validator.osgitest;

import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;

import com.foo.module1.constraint.CustomConstraint;
import com.foo.module2.constraint.Email;
import com.foo.module2.model.MyBean;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.GenericConstraintDef;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemTimeout;

/**
 * Integration test for Bean Validation and Hibernate Validator under OSGi.
 *
 * @author Gunnar Morling
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class OsgiIntegrationTest {

	@Inject
	private ValidatorFactory validatorFactory;

	@Inject
	private ValidationProviderResolver providerResolver;

	@Configuration
	public Option[] config() {

		return options(
				mavenBundle( "org.hibernate", "hibernate-validator", "5.0.0-SNAPSHOT" ),
				mavenBundle( "org.jboss.logging", "jboss-logging", "3.1.0.GA" ),
				mavenBundle( "javax.validation", "validation-api-osgi", "1.1.0-SNAPSHOT" ),
				mavenBundle( "org.hibernate", "hibernate-validator-osgi-integrationtest-module-1", "5.0.0-SNAPSHOT" ),
				mavenBundle( "org.hibernate", "hibernate-validator-osgi-integrationtest-module-2", "5.0.0-SNAPSHOT" ),
				junitBundles(),
				systemTimeout( 10 * 60 * 1000 ),
				felix().version( "3.2.2" )
		);
	}

	@BeforeClass
	public static void setLocaleToEnglish() {
		Locale.setDefault( Locale.ENGLISH );
	}

	@Test
	public void shouldRegisterValidatorFactory() {

		assertNotNull( validatorFactory );
	}

	@Test
	public void shouldProvideValidator() {

		Set<ConstraintViolation<Foo>> constraintViolations = validatorFactory.getValidator().validate( new Foo() );

		assertEquals( 1, constraintViolations.size() );
		assertEquals( "must be greater than or equal to 1", constraintViolations.iterator().next().getMessage() );
	}

	@Test
	public void shouldValidateCustomConstraint() {

		Set<ConstraintViolation<Bar>> constraintViolations = validatorFactory.getValidator().validate( new Bar() );

		assertEquals( 1, constraintViolations.size() );
		assertEquals( "Not a valid e-mail", constraintViolations.iterator().next().getMessage() );
	}

	@Test
	public void shouldValidateCustomConstraintFromOtherOsgiBundle() {

		Validator validator = validatorFactory.getValidator();

		Set<ConstraintViolation<Baz>> constraintViolations = validator.validate( new Baz() );

		assertEquals( 1, constraintViolations.size() );
		assertEquals( "Custom constraint not valid", constraintViolations.iterator().next().getMessage() );
	}

	@Test
	public void shouldValidateConstraintDefinedInXml() {

		Validator validator = validatorFactory.getValidator();

		Set<ConstraintViolation<MyBean>> constraintViolations = validator.validate( new MyBean() );

		assertEquals( 1, constraintViolations.size() );
		assertEquals( "Custom constraint not valid", constraintViolations.iterator().next().getMessage() );
	}

	@Test
	public void shouldValidateConstraintDefinedViaApi() {

		HibernateValidatorConfiguration configuration = Validation
				.byProvider( HibernateValidator.class )
				.providerResolver( providerResolver )
				.configure()
				.userClassLoader( OsgiIntegrationTest.class.getClassLoader() )
				.messageInterpolator(
						new ResourceBundleMessageInterpolator(
								(ResourceBundleLocator) new AggregateResourceBundleLocator(
										Arrays.asList( "Module1ValidationMessages" )
								)
						)
				);

		ConstraintMapping mapping = configuration.createConstraintMapping();

		mapping.type( Qux.class ).property( "bar", ElementType.FIELD ).constraint(
				new GenericConstraintDef<CustomConstraint>( CustomConstraint.class )
		);

		configuration.addMapping( mapping );

		Validator validator = configuration.buildValidatorFactory().getValidator();

		Set<ConstraintViolation<Qux>> constraintViolations = validator.validate( new Qux() );

		assertEquals( 1, constraintViolations.size() );
		assertEquals( "Custom constraint not valid", constraintViolations.iterator().next().getMessage() );
	}

	private static class Foo {

		@SuppressWarnings("unused")
		@Min(1)
		private int foo = 0;
	}

	private static class Bar {

		@SuppressWarnings("unused")
		@Email
		private String bar = "";
	}

	private static class Baz {

		@SuppressWarnings("unused")
		@CustomConstraint
		private String bar = "";
	}

	private static class Qux {

		@SuppressWarnings("unused")
		private String bar = "";
	}
}
