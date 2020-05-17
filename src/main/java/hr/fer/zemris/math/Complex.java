package hr.fer.zemris.math;

import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

/**
 * Class represents complex number.
 * 
 * @author Ante Gazibaric
 * @version 1.0
 *
 */
public class Complex {

	/** real part */
	private double re;
	/** imaginary part */
	private double im;
	
	/** complex number with re = im = 0 */
	public static final Complex ZERO = new Complex(0,0);
	/** complex number with re = 1, im = 0 */
	public static final Complex ONE = new Complex(1,0);
	/** complex number with re = -1, im = 0 */
	public static final Complex ONE_NEG = new Complex(-1,0);
	/** complex number with re = 0, im = 1 */
	public static final Complex IM = new Complex(0,1);
	/** complex number with re = 0, im = -1 */
	public static final Complex IM_NEG = new Complex(0,-1);
	/** treshold for comparing complex numbers */
	public static final double TRESHOLD = 1E-6;
	
	/**
	 * Constructor that creates new {@link Complex} object.
	 * 
	 * @param re real part of complex number
	 * @param im imaginary part of complex number
	 */
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}
	
	/**
	 * Constructor that creates new {@link Complex} object with re = im = 0
	 */
	public Complex() {
		this(0.0, 0.0);
	}
	
	/**
	 * Method returns module of complex number
	 * 
	 * @return module of complex number
	 */
	public double module() {
		return sqrt(pow(re, 2) + pow(im, 2));
	}
	
	/**
	 * Method returns new Complex number 
	 * that is product of multiplying this and given {@code c} complex numbers.
	 * 
	 * @param c complex number with whom this complex number is multiplied
	 * @return  new Complex number that represents
	 * 		    product of multiplying this and given {@code c} complex numbers.
	 * @throws  NullPointerException if given {@code c} is {@code null}
	 */
	public Complex multiply(Complex c) {
		Objects.requireNonNull(c, "Given complex number must not be null");
		
		return new Complex(
				this.re * c.re - this.im * c.im, 
				this.re * c.im + this.im * c.re);
	}
	
	/**
	 * Method returns new Complex number 
	 * that is product of dividing this and given {@code c} complex numbers.
	 * 
	 * @param c complex number with whom this complex number is divided
	 * @return  new Complex number that represents
	 * 		    product of dividing this and given {@code c} complex numbers.
	 * @throws  NullPointerException if given {@code c} is {@code null}
	 * @throws  IllegalArgumentException if given {@code c} is zero complex number
	 */
	public Complex divide(Complex c) {
		Objects.requireNonNull(c, "Given complex number must not be null");
		
		double denominator = pow(c.re, 2) + pow(c.im, 2);
		if (denominator == 0.0)
			throw new IllegalArgumentException("Given complex number must not be zero complex number");
		
		double reNumerator = this.re * c.re + this.im * c.im;
		double imNumerator = this.im * c.re - this.re * c.im;
		
		return new Complex(reNumerator / denominator, imNumerator / denominator);
	}
	
	/**
	 * Method returns new Complex number 
	 * that is product of adding this and given {@code c} complex numbers.
	 * 
	 * @param c complex number with whom this complex number is added
	 * @return  new Complex number that represents
	 * 		    product of adding this and given {@code c} complex numbers.
	 * @throws  NullPointerException if given {@code c} is {@code null}
	 */
	public Complex add(Complex c) {
		Objects.requireNonNull(c, "Given complex number must not be null");
		return new Complex(this.re + c.re, this.im + c.im);
	}
	
	/**
	 * Method returns new Complex number 
	 * that is product of subtracting this and given {@code c} complex numbers.
	 * 
	 * @param c complex number from which this complex number is subtracted
	 * @return  new Complex number that represents
	 * 		    product of subtracting this and given {@code c} complex numbers.
	 * @throws  NullPointerException if given {@code c} is {@code null}
	 */
	public Complex sub(Complex c) {
		Objects.requireNonNull(c, "Given complex number must not be null");
		return new Complex(this.re - c.re, this.im - c.im);
	}
	
	/**
	 * Method returns new Complex number with 
	 * re = -this.re and im = -this.im.
	 * 
	 * @return new Complex number that is opposite from this complex number
	 */
	public Complex negate() {
		return new Complex(-re, -im);
	}
	
	/**
	 * Method returns new Complex number 
	 * that is product of power operation with exponent {@code n}.
	 * 
	 * @param n exponent of power operation
	 * @return  new Complex number that represents
	 * 		    product of power operation with given exponent {@code n}.
	 * @throws  IllegalArgumentException if given {@code n} is negative.
	 */
	public Complex power(int n) {
		if(n < 0)
			throw new IllegalArgumentException("Exponent must not be less then zero. Was: " + n);
		
		double angle = n * getAngle();
		double module = pow(module(), n);
		return getComplexfromModuleAndAngle(module, angle);
	}
	
	/**
	 * Method performs root operation with given root degree {@code n}. </br>
	 * It returns list of complex numbers that represents roots of this complex number.
	 * 
	 * @param n roots degree
	 * @return  list of complex numbers that represents roots of this complex number.
	 * @throws  IllegalArgumentException if given root degree {@code n} is not positive
	 */
	public List<Complex> root(int n) {
		if(n <= 0) 
			throw new IllegalArgumentException("Root degree must be greater then zero. Was: " + n);
		
		List<Complex> roots = new ArrayList<>();
		double angle = getAngle();
		double module = pow(module(), -n);
		for (int k = 0; k < n; k++) {
			double newAngle = (angle + 2 * k * Math.PI) / n;
			roots.add(getComplexfromModuleAndAngle(module, newAngle));
		}
		return roots;
	}
	
	@Override
	public String toString() {
		if (re == 0.0 && im == 0.0)
			return "" + 0;
		if(re == 0.0) 
			return im + "i";
		if(im == 0.0)
			return "" + re;
		if(im > 0)
			return re + "+" + im + "i";
		return re + "-" + -im + "i";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(im);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(re);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Complex))
			return false;
		Complex other = (Complex) obj;
		if (Math.abs(this.im - other.im) > TRESHOLD)
			return false;
		if (Math.abs(this.re - other.re) > TRESHOLD)
			return false;
		return true;
	}
	
	/**
	 * Method returns real part of complex number.
	 * 
	 * @return real part of complex number
	 */
	public double getRe() {
		return re;
	}
	
	/**
	 * Method returns imaginary part of complex number.
	 * 
	 * @return imaginary part of complex number
	 */
	public double getIm() {
		return im;
	}

	/**
	 * Helper method that returns angle of this complex number.
	 * 
	 * @return angle of this complex number
	 */
	private double getAngle() {
		return atan2(im, re);
	}
	
	/**
	 * Helper method that returns new Complex number form given module and angle of complex number.
	 * 
	 * @param module module of complex number
	 * @param angle  angle of complex number
	 * @return       new Complex number form given module and angle of complex number
	 */
	private Complex getComplexfromModuleAndAngle(double module, double angle) {
		double re = module * cos(angle);
		double im = module * sin(angle);
		return new Complex(re, im);
	}
	
}
