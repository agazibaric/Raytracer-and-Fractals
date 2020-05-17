package hr.fer.zemris.math;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

import java.util.Objects;

/**
 * Class represents 3D vector that is determined by three points in space.
 * 
 * @author Ante Gazibaric
 * @version 1.0
 *
 */
public class Vector3 {

	/** x component */
	private double x;
	/** y component */
	private double y;
	/** z component */
	private double z;
	
	/** used for comparing vector's components */
	public static final double TRESHOLD = 1E-6;
	
	/**
	 * Constructor that creates new {@link Vector3} object.
	 * 
	 * @param x x component
	 * @param y y component
	 * @param z z component
	 */
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Method returns norm of vector.
	 * 
	 * @return norm of vector
	 */
	public double norm() {
		return sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));
	}
	
	/**
	 * Method returns normalized vector 
	 * 
	 * @return normalized vector
	 */
	public Vector3 normalized() {
		double norm = norm();
		return new Vector3(x / norm, y / norm, z / norm);
	}
	
	/**
	 * Method adds this vector and given {@code other} vector and returns resulting vector.
	 * 
	 * @param other vector with whom this vector is added
	 * @return      new vector that represents {@code other} vector added to this vector
	 */
	public Vector3 add(Vector3 other) {
		Objects.requireNonNull(other, "Given vector must not be null");
		
		return new Vector3(
				this.x + other.x,
				this.y + other.y,
				this.z + other.z);
	}
	
	/**
	 * Method subtracts given {@code other} vector from this vector and returns resulting vector.
	 * 
	 * @param other vector that is subtracted from this vector
	 * @return      new vector that represents result of subtracting this vector by given vector
	 */
	public Vector3 sub(Vector3 other) {
		Objects.requireNonNull(other, "Given vector must not be null");
		
		return new Vector3(
				this.x - other.x,
				this.y - other.y,
				this.z - other.z);
	}
	
	/**
	 * Method returns scalar product between this vector and given {@code other} vector.
	 * 
	 * @param other vector with whom scalar product operation is done
	 * @return      scalar product between this vector and given vector
	 */
	public double dot(Vector3 other) {
		Objects.requireNonNull(other, "Given vector must not be null");
		
		return this.x * other.x + 
			   this.y * other.y + 
			   this.z * other.z;
	}
	
	/**
	 * Method returns vector product between this vector and given {@code other} vector.
	 * 
	 * @param other vector with whom vector product operation is done
	 * @return      vector product between this vector and given {@code other} vector
	 */
	public Vector3 cross(Vector3 other) {
		Objects.requireNonNull(other, "Given vector must not be null");
		
		double x = this.y * other.z - this.z * other.y;
		double y = this.z * other.x - this.x * other.z;
		double z = this.x * other.y - this.y * other.x;
		return new Vector3(x, y, z);
	}
	
	/**
	 * Method returns new vector that represents this vector scaled by given scaler {@code s}.
	 * 
	 * @param s scaler used for scaling this vector
	 * @return  new vector that represents this vector scaled by given scaler {@code s}
	 */
	public Vector3 scale(double s) {
		return new Vector3(this.x * s, this.y * s, this.z * s);
	}
	
	/**
	 * Method returns cosine of angle between this vector and given {@code other} vector.
	 * 
	 * @param other vector used for calculating angle between this vector and other
	 * @return      cosine of angle between this vector and given {@code other} vector
	 */
	public double cosAngle(Vector3 other) {
		Objects.requireNonNull(other, "Given vector must not be null");
		return this.dot(other) / (this.norm() * other.norm());
	}
	
	/**
	 * Method returns x component of this vector.
	 * 
	 * @return x component of this vector
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Method returns y component of this vector.
	 * 
	 * @return y component of this vector
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Method returns z component of this vector.
	 * 
	 * @return z component of this vector
	 */
	public double getZ() {
		return z;
	}
	
	/**
	 * Method returns array that contains component of this vector.
	 * 
	 * @return array that contains component of this vector
	 */
	public double[] toArray() {
		return new double[] { x, y, z };
	}
	
	@Override
	public String toString() {
		return String.format("(%.6f, %.6f, %.6f)", x, y, z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Vector3))
			return false;
		Vector3 other = (Vector3) obj;
		if (Math.abs(this.x - other.x) > TRESHOLD)
			return false;
		if (Math.abs(this.y - other.y) > TRESHOLD)
			return false;
		if (Math.abs(this.z - other.z) > TRESHOLD)
			return false;
		return true;
	}
	
}
