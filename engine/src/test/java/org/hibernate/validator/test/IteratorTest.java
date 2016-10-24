/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.Test;

/**
 * @author Gunnar Morling
 *
 */
public class IteratorTest {

	@Test
	public void testIterator() {
		List<Integer> l1 = Arrays.asList( 1, 2, 3, 4, 5 );
		List<Integer> l2 = Arrays.asList( 6, 7, 8 );
		List<Integer> l3 = Arrays.asList();
		List<Integer> l4 = Arrays.asList( 9, 10 );

		NumberIterator ni = new NumberIterator( Arrays.asList( l1, l2, l3, l4 ) );

		while( ni.hasNext() ) {
			ni.next();
			System.out.println( ni.getCurrent() );
		}
	}

	public class NumberIterator {

		private final Iterator<List<Integer>> listIterator;
		private Iterator<Integer> currentNumberIterator;
		private Integer current;

		public NumberIterator(List<List<Integer>> lists) {
			listIterator = lists.iterator();
			currentNumberIterator = getNextNumberIterator();
		}


		boolean hasNext() {
			if ( !currentNumberIterator.hasNext() ) {
				currentNumberIterator = getNextNumberIterator();
			}

			return currentNumberIterator.hasNext();
		}

		private Iterator<Integer> getNextNumberIterator() {
			while( listIterator.hasNext() ) {
				Iterator<Integer> candidate = listIterator.next().iterator();
				if ( candidate.hasNext() ) {
					return candidate;
				}
			}

			return Collections.emptyIterator();
		}

		void next() {
			current = currentNumberIterator.next();
		}
		int getCurrent() {
			return current;
		}
	}
}
