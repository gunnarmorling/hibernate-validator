/*
* JBoss, Home of Professional Open Source
* Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual contributors
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
package com.foo.module2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Registers a validator factory using this module's class loader and the
 * resource bundles of this application.
 *
 * @author Gunnar Morling
 */
public class Activator implements BundleActivator {

	private ValidationProviderResolverTracker tracker;

	public void start(BundleContext context) throws Exception {

		tracker = new ValidationProviderResolverTracker( context );
		tracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		tracker.close();
	}
}
