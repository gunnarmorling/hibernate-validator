/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.integration.wildfly.rest;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.http.ContentType;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

/**
 * @author Gunnar Morling
 */
@RunWith(Arquillian.class)
public class ValidationThroughRestIT {

	private static final String WAR_FILE_NAME = ValidationThroughRestIT.class.getSimpleName() + ".war";

	@Deployment(testable=false)
	public static Archive<?> createTestArchive() {
		return ShrinkWrap.create( WebArchive.class, WAR_FILE_NAME )
				.addClasses( HikingApplication.class )
				.addClasses( HikeResource.class )
				.addAsWebInfResource( EmptyAsset.INSTANCE, "beans.xml" );
	}

	@Test
	public void validationOfFieldAndParameterOfEjbResource(@ArquillianResource URL baseURI) {
		String responseBody =
			given()
				.contentType( ContentType.JSON )
				.accept( ContentType.TEXT )
				.body( "-1" )
			.when()
				.post( "/ValidationThroughRestIT/hiking-manager/hikes/createHike" )
			.getBody()
				.asString();

		assertTrue( responseBody.contains( "must be greater than or equal to 1" ) );

		// This assertion fails; It will pass when removing the @Stateless in HikeResource
		assertTrue( responseBody.contains( "may not be null" ) );
	}
}
