package hr.fer.zemris.math;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class represents complex polynomial.
 * 
 * @author Ante Gazibaric
 * @version 1.0
 *
 */
public class ComplexPolynomial {

	/** list of complex factors of polynomial */
	private List<Complex> factorsList = new LinkedList<>();
	
	/**
	 * Constructor that creates new {@link ComplexPolynomial} object.
	 * 
	 * @param factors complex factors of polynomial
	 * @throws        NullPointerException if given {@code factors} is {@code null}
	 * @throws        IllegalArgumentException if given {@code factors} contains no elements
	 */
	public ComplexPolynomial(Complex ...factors) {
		Objects.requireNonNull(factors, "Given factors must not be null");
		if (factors.length == 0)
			throw new IllegalArgumentException("Factors must contain at least one element");
		
		for (Complex factor : factors) {
			factorsList.add(factor);
		}
	}
	
	/**
	 * Method returns order of complex polynomial.
	 * 
	 * @return order of complex polynomial
	 */
	public short order() {
		return (short) (factorsList.size() - 1);
	}
	
	/**
	 * Method returns new complex polynomial 
	 * that represents product of multiplying this and given {@code p} polynomials.
	 * 
	 * @param p complex polynomial with whom this polynomial is multiplied
	 * @return  new complex polynomial that represents product of multiplying this and given {@code p} polynomials
	 * @throws  NullPointerException if given {@code p} is {@code null}
	 */
	public ComplexPolynomial multiply(ComplexPolynomial p) {
		Objects.requireNonNull(p, "Given complex polynomial must not be null");
		
		short thisOrder = this.order();
		short otherOrder = p.order();
		Complex[] newFactors = new Complex[thisOrder + otherOrder + 1];
		
		for (short k = 0; k <= thisOrder + otherOrder; k++) {
			newFactors[k] = Complex.ZERO;
		}
		for (short i = 0; i <= thisOrder; i++) {
			for (short j = 0; j <= otherOrder; j++) {
				newFactors[i + j] = newFactors[i + j].add(this.factorsList.get(i).multiply(p.factorsList.get(j)));
			}
		}
		return new ComplexPolynomial(newFactors);
	}
	
	/**
	 * Method computes first derivative of this complex polynomial.
	 * 
	 * @return new complex polynomial that represents
	 * 		   first derivative of this complex polynomial
	 */
	public ComplexPolynomial derive() {
		int length = factorsList.size() - 1;
		Complex[] factors = new Complex[length];
		
		for (int i = 0; i < length; i++) {
			factors[i] = factorsList.get(i).multiply(new Complex(length - i, 0));
		}
		return new ComplexPolynomial(factors);
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
		
		Complex result = null;
		for (short i = 0, n = order();  i <= n; i++) {
			if (result == null) {
				result = factorsList.get(i).multiply(z.power(n - i));
			} else {
				result = result.add(factorsList.get(i).multiply(z.power(n - i)));
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		int length = factorsList.size();
		StringBuilder sb = new StringBuilder();
		for (short i = 0, n = order(); i < n; i++) {
			Complex factor = factorsList.get(i);
			if (!factor.equals(Complex.ZERO)) {
				sb.append("(").append(factorsList.get(i).toString()).append(")").append("z^").append(n - i).append("+");
			}
		}
		Complex lastFactor = factorsList.get(length -1);
		if (!lastFactor.equals(Complex.ZERO)) {
			sb.append("(").append(factorsList.get(length -1)).append(")");
		} else {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}

}
