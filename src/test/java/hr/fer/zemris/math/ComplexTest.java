package hr.fer.zemris.math;

import org.junit.Test;

import java.util.List;

import org.junit.Assert;

public class ComplexTest {
	
	public static final double TRESHOLD = Complex.TRESHOLD;
	
	@Test
	public void testGetComponents() {
		double re = -0.45;
		double im = 7.88;
		Complex c = new Complex(re, im);
		
		Assert.assertEquals(re, c.getRe(), TRESHOLD);
		Assert.assertEquals(im, c.getIm(), TRESHOLD);
	}
	
	@Test
	public void testAddingComplexNumber() {
		double re1 = -0.45;
		double im1 = 7.88;
		double re2 = 3.1;
		double im2 = -12.03;
		Complex c1 = new Complex(re1, im1);
		Complex c2 = new Complex(re2, im2);
		Complex sum = c1.add(c2);
		Complex expected = new Complex(re1 + re2, im1 + im2);
		
		Assert.assertEquals(re1 + re2, sum.getRe(), TRESHOLD);
		Assert.assertEquals(im1 + im2, sum.getIm(), TRESHOLD);
		Assert.assertTrue(sum.equals(expected));
	}
	
	
	@Test
	public void testSubtractingComplexNumber() {
		double re1 = -0.45;
		double im1 = 7.88;
		double re2 = 3.1;
		double im2 = -12.03;
		Complex c1 = new Complex(re1, im1);
		Complex c2 = new Complex(re2, im2);
		Complex c = c1.sub(c2);
		Complex expected = new Complex(re1 - re2, im1 - im2);
		
		Assert.assertTrue(c.equals(expected));
	}
	
	@Test
	public void testMultiplyingComplexNumber() {
		Complex c1 = new Complex(1, 1);
		Complex c2 = new Complex(2, 3);
		Complex c = c1.multiply(c2);
		Complex expected = new Complex(-1, 5);
		
		Assert.assertTrue(c.equals(expected));
	}
	
	@Test
	public void testDividingComplexNumber() {
		Complex c1 = new Complex(3, 2);
		Complex c2 = new Complex(0, 1);
		Complex c = c1.divide(c2);
		Complex expected = new Complex(2, -3);
		
		Assert.assertTrue(c.equals(expected));
	}
	
	@Test
	public void testPowerOfComplexNumber() {
		Complex c1 = new Complex(1, 2);
		int exponent = 4;
		Complex c = c1.power(exponent);
		Complex expected = new Complex(-7.0, -24.0);
		
		Assert.assertTrue(c.equals(expected));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidPowerExponent() {
		Complex c1 = new Complex(2, 2);
		int exponent = -4;
		c1.power(exponent);
	}
	
	@Test
	public void testRootOfComplexNumber() {
		Complex c1 = new Complex(4, 4);
		int n = 2;
		double expectedRe1 = 0.028871235;
		double expectedIm1 = 0.011958857;
		double expectedRe2 = -expectedRe1;
		double expectedIm2 = -expectedIm1;
		Complex rootExpected1 = new Complex(expectedRe1, expectedIm1);
		Complex rootExpected2 = new Complex(expectedRe2, expectedIm2);
		List<Complex> actualRoots = c1.root(n);
		Complex rootActual1 = actualRoots.get(0);
		Complex rootActual2 = actualRoots.get(1);
		
		Assert.assertTrue(rootExpected1.equals(rootActual1));
		Assert.assertTrue(rootExpected2.equals(rootActual2));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidRootDegree() {
		Complex c1 = new Complex(2, 2);
		int root = -4;
		c1.root(root);
	}
	
}
