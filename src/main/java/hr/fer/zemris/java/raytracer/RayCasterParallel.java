package hr.fer.zemris.java.raytracer;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import hr.fer.zemris.java.raytracer.model.GraphicalObject;
import hr.fer.zemris.java.raytracer.model.IRayTracerProducer;
import hr.fer.zemris.java.raytracer.model.IRayTracerResultObserver;
import hr.fer.zemris.java.raytracer.model.LightSource;
import hr.fer.zemris.java.raytracer.model.Point3D;
import hr.fer.zemris.java.raytracer.model.Ray;
import hr.fer.zemris.java.raytracer.model.RayIntersection;
import hr.fer.zemris.java.raytracer.model.Scene;
import hr.fer.zemris.java.raytracer.viewer.RayTracerViewer;

/**
 * Class represents ray-caster that calculates ray-surface intersection to render 3D scene. </br>
 * It parallelizes calculation using Fork-Join framework and RecursiveAction.
 * 
 * @author Ante Gazibaric
 * @version 1.0
 *
 */
public class RayCasterParallel {
	
	/**
	 * Ambient color value
	 */
	private static final int AMBIENT_COLOR = 15;
	
	/**
	 * Main method. Accepts no arguments.
	 * 
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		RayTracerViewer.show(getIRayTracerProducer(),
				new Point3D(10, 0, 0), 
				new Point3D(0, 0, 0),
				new Point3D(0, 0, 10), 
				20, 20);
	}
	
	/**
	 * Class represents calculation job for ray caster.
	 * 
	 * @author Ante Gazibaric
	 * @version 1.0
	 *
	 */
	private static class Job extends RecursiveAction {

		/** width of window */
		int width;
		/** height of window */
		int height;
		/** min value for y component */
		int yMin;
		/** max value for y component */
		int yMax;
		/** horizontal screen size */
		double horizontal;
		/** vertical screen size */
		double vertical;
		/** viewer point */
		Point3D eye;
		/** scene that contains all objects and light sources */
		Scene scene;
		/** x axis */
		Point3D xAxis;
		/** y axis */
		Point3D yAxis;
		/** screen corner point */
		Point3D screenCorner;
		/** array for red color */
		short[] red;
		/** array for green color */
		short[] green;
		/** array for blue color */
		short[] blue;
		/** used to determine when to calculate job directly */
		static final int TRESHOLD = 16;
		/** serial ID */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor that creates new {@link Job} object.
		 * 
		 * @param width		   {@link #width}
		 * @param height	   {@link #height}
		 * @param yMin  	   {@link #yMin}
		 * @param yMax  	   {@link #yMax}
		 * @param horizontal   {@link #horizontal}
		 * @param vertical     {@link #vertical}
		 * @param eye		   {@link #eye}
		 * @param scene    	   {@link #scene}
		 * @param xAxis		   {@link #xAxis}
		 * @param yAxis        {@link #yAxis}
		 * @param screenCorner {@link #screenCorner}
		 * @param red		   {@link #red}
		 * @param green  	   {@link #green}
		 * @param blue		   {@link #blue}
		 */
		public Job(int width, int height, int yMin, int yMax, double horizontal, double vertical, Point3D eye,
				Scene scene, Point3D xAxis, Point3D yAxis, Point3D screenCorner, short[] red, short[] green, short[] blue) {
			this.width = width;
			this.height = height;
			this.yMin = yMin;
			this.yMax = yMax;
			this.horizontal = horizontal;
			this.vertical = vertical;
			this.eye = eye;
			this.scene = scene;
			this.xAxis = xAxis;
			this.yAxis = yAxis;
			this.screenCorner = screenCorner;
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public void compute() {
			if (yMax - yMin + 1 <= TRESHOLD) {
				computeDirect();
				return;
			}
			invokeAll(
					new Job(width, height, yMin, yMin + (yMax - yMin) / 2, horizontal, vertical, 
						eye, scene, xAxis, yAxis, screenCorner, red, green, blue),
					new Job(width, height, yMin + (yMax - yMin) / 2, yMax, horizontal, vertical, 
						eye, scene, xAxis, yAxis, screenCorner, red, green, blue)
			);
		}

		public void computeDirect() {
			calculate(width, height, yMin, yMax, horizontal, vertical, eye, scene, xAxis, yAxis, screenCorner, red, green, blue);
		}
	}

	/**
	 * Method returns implemented {@link IRayTracerProducer} object.
	 * 
	 * @return {@link IRayTracerProducer} object
	 */
	private static IRayTracerProducer getIRayTracerProducer() {
		return new IRayTracerProducer() {
			@Override
			public void produce(Point3D eye, Point3D view, Point3D viewUp, double horizontal, double vertical,
					int width, int height, long requestNo, IRayTracerResultObserver observer) {

				System.out.println("Započinjem izračune...");

				short[] red = new short[width * height];
				short[] green = new short[width * height];
				short[] blue = new short[width * height];

				Point3D eyeView = view.sub(eye).normalize();
				Point3D viewUpNormalized = viewUp.normalize();

				Point3D yAxis = viewUpNormalized
						.sub(eyeView.scalarMultiply(eyeView.scalarProduct(viewUpNormalized))).normalize();
				Point3D xAxis = eyeView.vectorProduct(yAxis).normalize();
				Point3D screenCorner = view
						.sub(xAxis.scalarMultiply(horizontal / 2.0))
						.add(yAxis.scalarMultiply(vertical / 2.0));

				Scene scene = RayTracerViewer.createPredefinedScene();
				
				ForkJoinPool pool = new ForkJoinPool();
				pool.invoke(new Job(width, height, 0, height - 1, horizontal, vertical, eye, scene, xAxis, yAxis, screenCorner, red, green, blue));
				pool.shutdown();
				

				System.out.println("Izračuni gotovi...");
				observer.acceptResult(red, green, blue, requestNo);
				System.out.println("Dojava gotova...");
			}
		};
	}
	
	/**
	 * Method calculates and fills red, green and blue arrays with calculated color values.
	 * 
	 * @param width        width of window
	 * @param height       height of window
	 * @param yMin         min value for y component
	 * @param yMax         max value for y component
	 * @param horizontal   horizontal size of screen
	 * @param vertical     vertical size of screen
	 * @param eye          viewer position
	 * @param scene        scene that contains all objects and light sources
	 * @param xAxis        x axis
	 * @param yAxis        y axis
	 * @param screenCorner corner of screen
	 * @param red		   array for red color
	 * @param green        array for green color
	 * @param blue         array for blue color
	 */
	private static void calculate(int width, int height, int yMin, int yMax, 
			double horizontal, double vertical,
			Point3D eye, Scene scene,
			Point3D xAxis, Point3D yAxis, Point3D screenCorner,
			short[] red, short[] green, short[] blue) {
		
		short[] rgb = new short[3];
		int offset = yMin * width;

		for (int y = yMin; y <= yMax; y++) {
			for (int x = 0; x < width; x++) {
				Point3D xComponent = xAxis.scalarMultiply(x * horizontal / (width - 1));
				Point3D yComponent = yAxis.scalarMultiply(y * vertical / (height - 1));
				Point3D screenPoint = screenCorner.add(xComponent).sub(yComponent);
				Ray ray = Ray.fromPoints(eye, screenPoint);
				tracer(scene, ray, rgb);
				red[offset] = rgb[0] > 255 ? 255 : rgb[0];
				green[offset] = rgb[1] > 255 ? 255 : rgb[1];
				blue[offset] = rgb[2] > 255 ? 255 : rgb[2];
				offset++;
			}
		}
		
	}
	
	/**
	 * Method calculates and fills rgb color array with ambient, diffuse and reflective component </br>
	 * considering given {@code ray} and objects and light sources in given {@code scene}.
	 * 
	 * @param scene scene that contains objects and light sources
	 * @param ray   ray from viewer point of view
	 * @param rgb   rgb color array that is filled
	 */
	protected static void tracer(Scene scene, Ray ray, short[] rgb) {
		rgb[0] = rgb[1] = rgb[2] =  0;
		RayIntersection closest = findClosestIntersection(scene, ray);
		if (closest == null) {
			return;
		}
		rgb[0] = rgb[1] = rgb[2] = AMBIENT_COLOR;
		
		for (LightSource source : scene.getLights()) {
			Point3D sourceStart = source.getPoint();
			Point3D direction = closest.getPoint().sub(sourceStart).normalize();
			Ray sourceRay = new Ray(sourceStart, direction);
			RayIntersection intersection = findClosestIntersection(scene, sourceRay);

			if (intersection != null) {
				double lightIntersectionDistance = intersection.getDistance();
				double d = sourceStart.sub(closest.getPoint()).norm();
				if (lightIntersectionDistance + 0.01 < d)
					continue;
			}
			
			Point3D intersectionSource = sourceStart.sub(closest.getPoint()).normalize();
			Point3D normal = closest.getNormal().normalize();

			addDiffuseComponent(intersectionSource, normal, closest, source, rgb);
			addReflectiveComponent(intersectionSource, normal, ray.start, intersection, source, rgb);
		}
		
	}
	
	/**
	 * Method adds diffuse color component.
	 * 
	 * @param intersectionSource vector from intersection to light the source
	 * @param normal             normal to an object in point of intersection
	 * @param intersection       intersection of ray and object
	 * @param lightSource        light source
	 * @param rgb                array of rgb colors
	 */
	private static void addDiffuseComponent(Point3D intersectionSource, Point3D normal, RayIntersection intersection,
			LightSource lightSource, short[] rgb) {

		double cosAngle = intersectionSource.scalarProduct(normal);
		rgb[0] += lightSource.getR() * intersection.getKdr() * cosAngle;
		rgb[1] += lightSource.getG() * intersection.getKdg() * cosAngle;
		rgb[2] += lightSource.getB() * intersection.getKdb() * cosAngle;
	}
	
	/**
	 * Method adds reflective color component.
	 * 
	 * @param intersectionSource vector from intersection to light the source
	 * @param normal			 normal to an object in point of intersection
	 * @param rayStart			 start point of viewer's ray
	 * @param intersection		 intersection of ray and object
	 * @param lightSource		 light source
	 * @param rgb				 array of rgb colors
	 */
	private static void addReflectiveComponent(Point3D intersectionSource, Point3D normal, Point3D rayStart, RayIntersection intersection,
			LightSource lightSource, short[] rgb) {
		
		Point3D reflected = intersectionSource.sub(normal.scalarMultiply(
								2 * intersectionSource.scalarProduct(normal))).normalize();
		Point3D viewer = rayStart.sub(intersection.getPoint()).normalize();
		double cosAngleN = Math.pow(reflected.scalarProduct(viewer), intersection.getKrn());
		rgb[0] += lightSource.getR() * intersection.getKrr() * cosAngleN;
		rgb[1] += lightSource.getG() * intersection.getKrg() * cosAngleN;
		rgb[2] += lightSource.getB() * intersection.getKrb() * cosAngleN;
	}
	
	/**
	 * Method returns closest intersection between given {@code ray} and any object in {@code scene}.
	 * 
	 * @param scene scene that contains objects
	 * @param ray   ray whose intersection with object is finding
	 * @return      closest intersection between ray and any object in scene,
	 * 				or {@code null} if there's no intersections
	 */
	private static RayIntersection findClosestIntersection(Scene scene, Ray ray) {
		List<GraphicalObject> objects = scene.getObjects();
		RayIntersection intersection = null;
		for (GraphicalObject object : objects) {
			RayIntersection newIntersection = object.findClosestRayIntersection(ray);
			if (intersection == null) {
				intersection = newIntersection;
			} else {
				if (newIntersection != null) {
					double distance1 = intersection.getDistance();
					double distance2 = newIntersection.getDistance();
					intersection = distance1 < distance2 ? intersection : newIntersection;
				}
			}
		}
		return intersection;
	}

}
