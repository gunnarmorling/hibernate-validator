/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.testutils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.test.internal.engine.cascaded.CascadedClassConstraintTest;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

/**
 * @author Gunnar Morling
 *
 */
public class TestRunner {

	public static void main(String[] args) {
		XmlSuite suite = new XmlSuite();
		suite.setName( "HV engine test suite" );

		XmlTest test = new XmlTest(suite);
		test.setName( "HV engine test" );

//		List<XmlPackage> packages = Arrays.asList( new XmlPackage( "org.hibernate.validator.test.*" ) );
		List<XmlPackage> packages = Arrays.asList( new XmlPackage( "org.hibernate.validator.test.internal.metadata.descriptor.*" ) );

//		test.setXmlPackages( packages );

//		 Alternatively e.g. use this for running single tests
		List<XmlClass> classes = Arrays.asList(
//				new XmlClass( ReturnValueDescriptorTest.class ) //,
//				new XmlClass( ExecutableMetaDataTest.class ),
				new XmlClass( CascadedClassConstraintTest.class )
//				new XmlClass( ValidatorTest.class )
//				new XmlClass( ValidatorFactoryTest.class ),
//				new XmlClass( AnnotationBasedMethodValidationTest.class )

		);
		test.setXmlClasses( classes );

		Thread.currentThread().setContextClassLoader( HibernateValidator.class.getClassLoader() );

		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		testng.setXmlSuites( Collections.singletonList( suite ) );
		testng.addListener( tla );
		testng.run();

		for ( ITestResult failure: tla.getConfigurationFailures() ) {
			System.out.println( "Failure: " + failure.getName() );
			failure.getThrowable().printStackTrace();
		}

		for ( ITestResult result : tla.getFailedTests() ) {
			System.out.println( "Failed:" + result.getName() );
			result.getThrowable().printStackTrace();
		}
	}
}
