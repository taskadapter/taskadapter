package com.taskadapter.web.magic;

import java.io.IOException;

import org.junit.Test;

/**
 * Tests for interfaces.
 * 
 * @author maxkar
 * 
 */
public class InterfacesTest {

	/**
	 * Tests interfaces.
	 */
	@Test
	public void testInterfaces() {
		final Runnable r = Interfaces.fromMethod(Runnable.class, this, "doRun");
		r.run();
	}

	/**
	 * Tests interfaces.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalThrowable() {
		final Runnable r = Interfaces.fromMethod(Runnable.class, this, "doRun1");
		r.run();
	}

	/**
	 * Runs a method.
	 */
	@SuppressWarnings("unused")
	private void doRun1() throws IOException {
		// just a mock.
	}
	
	/**
	 * Runs a method.
	 */
	@SuppressWarnings("unused")
	private void doRun() {
		// just a mock.
	}
}
