package hr.fer.zemris.math;

import org.junit.Test;
import org.junit.Assert;

public class Vector3Test {
	
	private static final double TRESHOLD = Vector3.TRESHOLD;
	
	@Test
	public void testNormOfVector() {
		Vector3 v = new Vector3(1, 1, 1);
		double expected = Math.sqrt(3);
		Assert.assertEquals(expected, v.norm(), TRESHOLD);
	}
	
	@Test
	public void testNormalizedVector() {
		Vector3 v = new Vector3(2, 2, 2);
		double norm = 2 * Math.sqrt(3);
		Vector3 expected = new Vector3(2 / norm, 2 / norm, 2 / norm);
		
		Assert.assertTrue(expected.equals(v.normalized()));
	}
	
	@Test
	public void testAddingVectors() {
		Vector3 v1 = new Vector3(2, 2, 2);
		Vector3 v2 = new Vector3(-1, -1, -1);
		Vector3 actual = v1.add(v2);
		Vector3 expected = new Vector3(1, 1, 1);
		
		Assert.assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testSubtractingVectors() {
		Vector3 v1 = new Vector3(2, 2, 2);
		Vector3 v2 = new Vector3(-1, -1, -1);
		Vector3 actual = v1.sub(v2);
		Vector3 expected = new Vector3(3, 3, 3);
		
		Assert.assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testDotProductOfVectors() {
		Vector3 v1 = new Vector3(2, 2, 2);
		Vector3 v2 = new Vector3(-1, -1, -1);
		double actual = v1.dot(v2);
		double expected = -6;
		
		Assert.assertEquals(expected, actual, TRESHOLD);
	}
	
	@Test
	public void testCrossProductOfVectors() {
		Vector3 v1 = new Vector3(1, 0, 0);
		Vector3 v2 = new Vector3(0, 1, 0);
		Vector3 actual = v1.cross(v2);
		Vector3 expected = new Vector3(0, 0, 1);
		
		Assert.assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testVectorScaling() {
		Vector3 v1 = new Vector3(1, -1, 0);
		double s = 6;
		Vector3 actual = v1.scale(s);
		Vector3 expected = new Vector3(6, -6, 0);
		
		Assert.assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testCosineOfAngleBetweenVectors() {
		Vector3 v1 = new Vector3(1, 1, 0);
		Vector3 v2 = new Vector3(0, 1, 0);
		double actual = v1.cosAngle(v2);
		double expected = Math.cos(Math.PI / 4.0);
		
		Assert.assertEquals(expected, actual, TRESHOLD);
	}
	
	@Test (expected = NullPointerException.class)
	public void testNullAsArgument() {
		Vector3 v1 = new Vector3(1, 1, 0);
		v1.add(null);
	}
	

}
