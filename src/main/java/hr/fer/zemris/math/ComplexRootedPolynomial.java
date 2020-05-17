package hr.fer.zemris.math;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class represents complex polynomial in root form.
 * 
 * @author Ante Gazibaric
 * @version 1.0
 *
 */
public class ComplexRootedPolynomial {

	/** list of roots */
	private List<Complex> rootsList = new LinkedList<>();
	
	/**
	 * Constructor that creates new {@link ComplexRootedPolynomial} object.
	 * 
	 * @param roots roots of complex polynomial
	 * @throws      NullPointerException if given {@code roots} is {@code null}
	 * @throws      IllegalArgumentException if given {@code roots} contains no elements
	 */
	public ComplexRootedPolynomial(Complex ...roots) {
		Objects.requireNonNull(roots, "Given roots must not be null");
		if (roots.length == 0)
			throw new IllegalArgumentException("Roots must contain at least one complex roots.");
		
		for (Complex root : roots) {
			this.rootsList.add(root);
		}
	}
	
	/**
	 * Method computes polynomial value at given {@code z}.
	 * 
	 * @param z complex number whose value of this polynomial is computed
	 * @return  value of this polynomial at given complex number {@code z}
	 * @throws  NullPointerException if given {@code z} is {@code null}
	 */
	public Complex apply(Complex z) {
		Objects.requireNonNull(z, "Given complex number must not be null");
		
		List<Complex> intermediateList = new LinkedList<>();
		for (Complex root : rootsList) {
			intermediateList.add(z.sub(root));
		}
		return rootsList.stream()
				.reduce((z1, z2) -> z1.multiply(z2))
				.get();
	}
	
	/**
	 * Method converts this root polynomial form to {@link ComplexPolynomial}.
	 * 
	 * @return {@link ComplexPolynomial} that represents this object.
	 */
	public ComplexPolynomial toComplexPolynom() {
		Complex firstRoot = rootsList.get(0).multiply(Complex.ONE_NEG);
		ComplexPolynomial resultPoly = new ComplexPolynomial(Complex.ONE, firstRoot);
		
		for (int i = 1, n = rootsList.size(); i < n; i++) {
			Complex root = rootsList.get(i).multiply(Complex.ONE_NEG);
			ComplexPolynomial newPoly = new ComplexPolynomial(Complex.ONE, root);
			resultPoly = resultPoly.multiply(newPoly);
		}
		return resultPoly;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Complex root : rootsList) {
			sb.append("(z-(").append(root.toString()).append("))");
		}
		return sb.toString();
	}
	
	/**
	 * Method finds index of closes root for given complex number {@code z}
	 * that is within given {@code treshold}. </br>
	 * If there is no such root, method returns -1.
	 * 
	 * @param z        complex number for which index of closest root is computed
	 * @param treshold treshold within value is looked for
	 * @return         index of closes root for given complex number {@code z} that is within treshold,
	 * 				   or -1 if there's no such root
	 */
	public int indexOfClosestRootFor(Complex z, double treshold) {
		if (z == null || treshold <= 0)
			return -1;
		
		int index = -1;
		double minDelta = Double.MAX_VALUE;
		for (int i = 0, n = rootsList.size(); i < n; i++) {
			Complex deltaComplex = rootsList.get(i).multiply(Complex.ONE_NEG).sub(z);
			double delta = deltaComplex.module();
			if (delta <= treshold && delta < minDelta) {
				index = i;
				minDelta = delta;
			}
		}
		return index;
	}
	
}
