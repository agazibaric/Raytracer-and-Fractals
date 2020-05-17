package hr.fer.zemris.java.raytracer.model;

/**
 * Class represents sphere graphical object.
 * 
 * @author Ante Gazibaric
 * @version 1.0
 *
 */
public class Sphere extends GraphicalObject {

	/** center of sphere */
	private Point3D center;
	/** sphere radius */
	private double radius;
	/** coefficient for diffuse component for red color */
	private double kdr;
	/** coefficient for diffuse component for green color */
	private double kdg;
	/** coefficient for diffuse component for blue color */
	private double kdb;
	/** coefficient for reflective component for red color */
	private double krr;
	/** coefficient for reflective component for red green */
	private double krg;
	/** coefficient for reflective component for red blue */
	private double krb;
	/** coefficient n for reflective component */
	private double krn;
	/** treshold used for comparing double values */
	private static final double TRESHOLD = 1E-6;
	
	/**
	 * Constructor that creates new {@link Sphere} object.
	 * 
	 * @param center {@link #center}
	 * @param radius {@link #radius}
	 * @param kdr 	 {@link #kdr}
	 * @param kdg 	 {@link #kdg}
	 * @param kdb	 {@link #kdb}
	 * @param krr    {@link #krr}
	 * @param krg    {@link #krg}
	 * @param krb	 {@link #krb}
	 * @param krn	 {@link krn}
	 */
	public Sphere(Point3D center, double radius, double kdr, double kdg, double kdb, double krr, double krg, double krb,
			double krn) {
		this.center = center;
		this.radius = radius;
		this.kdr = kdr;
		this.kdg = kdg;
		this.kdb = kdb;
		this.krr = krr;
		this.krg = krg;
		this.krb = krb;
		this.krn = krn;
	}

	@Override
	public RayIntersection findClosestRayIntersection(Ray ray) {
		Point3D startCenter = ray.start.sub(center);
		
		double a = ray.direction.scalarProduct(ray.direction); 
		double b = 2 * ray.direction.scalarProduct(startCenter);
		double c = startCenter.scalarProduct(startCenter) - radius * radius;

		double[] lambdas = getSolutions(a, b, c);
		if (lambdas == null)
			return null;

		double first = lambdas[0];
		double second = lambdas[1];

		if (first < 0 && second < 0)
			return null;

		if (Math.abs(first - second) < TRESHOLD) {
			boolean outer = true;
			Point3D point = ray.start.add(ray.direction.scalarMultiply(first));
			double distance = ray.start.sub(point).norm();
			return new RayIntersectionImpl(point, distance, outer);
		}

		if (first > 0 && second > 0) {
			boolean outer = true;
			double closerLambda = Math.min(first, second);
			Point3D point = ray.start.add(ray.direction.scalarMultiply(closerLambda));
			double distance = ray.start.sub(point).norm();
			return new RayIntersectionImpl(point, distance, outer);
		}

		boolean outer = false;
		double greaterLambda = Math.max(first, second);
		Point3D point = ray.start.add(ray.direction.scalarMultiply(greaterLambda));
		double distance = ray.start.sub(point).norm();
		return new RayIntersectionImpl(point, distance, outer);
	}
	
	/**
	 * Method calculates solutions of quadratic equation. </br>
	 * If solutions are complex it returns null;
	 * 
	 * @param a 'a' part of quadratic equation
	 * @param b 'b' part of quadratic equation
	 * @param c 'c' part of quadratic equation
	 * @return  two solutions in array length == 2 
	 * 			or {@code null} if solutions are complex
	 */
	private double[] getSolutions(double a, double b, double c) {
		double discriminant = b*b - 4*a*c;
		if (discriminant < 0)
			return null;
		double root = Math.sqrt(discriminant);
		double first = (-b + root) / 2;
		double second = (-b - root) / 2;
		
		return new double[] { first, second };	
	}
	
	/**
	 * Implementation of {@link RayIntersection}  that represents
	 * intersection of ray with sphere object.
	 * 
	 * @author Ante Gazibaric
	 * @version 1.0
	 *
	 */
	private class RayIntersectionImpl extends RayIntersection {

		/**
		 * Constructor that creates new {@link RayIntersectionImpl} object.
		 * 
		 * @param point    point of intersection between ray and sphere
		 * @param distance distance between ray start point and the intersection
		 * @param outer    is intersection outer flag
		 */
		protected RayIntersectionImpl(Point3D point, double distance, boolean outer) {
			super(point, distance, outer);
		}

		@Override
		public Point3D getNormal() {
			return getPoint().sub(center).normalize();
		}

		@Override
		public double getKdr() {
			return kdr;
		}

		@Override
		public double getKdg() {
			return kdg;
		}

		@Override
		public double getKdb() {
			return kdb;
		}

		@Override
		public double getKrr() {
			return krr;
		}

		@Override
		public double getKrg() {
			return krg;
		}

		@Override
		public double getKrb() {
			return krb;
		}

		@Override
		public double getKrn() {
			return krn;
		}
			
	}
	
}
