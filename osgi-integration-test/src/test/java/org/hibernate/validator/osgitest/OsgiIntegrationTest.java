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

import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

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

	@Configuration
	public Option[] config() {

		return options(
				mavenBundle( "org.hibernate", "hibernate-validator", "4.3.0-SNAPSHOT" ),
				mavenBundle( "org.jboss.logging", "jboss-logging", "3.1.0.GA" ),
				mavenBundle( "javax.validation", "validation-api", "1.0.1-SNAPSHOT" ),
				junitBundles(),
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

	private static class Foo {

		@SuppressWarnings("unused")
		@Min(1)
		private int bar = 0;
	}
}
