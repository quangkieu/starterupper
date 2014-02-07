package com.joeylawrance.starterupper.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Email2SchoolTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		assertThat(Email2School.schoolFromEmail("somebody@open.ac.uk"), is ("The Open University"));
		assertThat(Email2School.schoolFromEmail("somebody@csail.mit.edu"), is("Massachusetts Institute of Technology"));
	}

}
