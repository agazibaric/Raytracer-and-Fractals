package hr.fer.zemris.java.raytracer;

import java.util.List;

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
 * Class represents ray-caster that calculates ray-surface intersection to render 3D scene. 
 * 
 * @author Ante Gazibaric
 * @version 1.0
 *
 */
public class RayCaster {
	
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
				short[] rgb = new short[3];
				int offset = 0;

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						Point3D xComponent = xAxis.scalarMultiply(x * horizontal / (width - 1.0));
						Point3D yComponent = yAxis.scalarMultiply(y * vertical / (height - 1.0));
						Point3D screenPoint = screenCorner.add(xComponent).sub(yComponent);
						Ray ray = Ray.fromPoints(eye, screenPoint);
						tracer(scene, ray, rgb);
						red[offset] = rgb[0] > 255 ? 255 : rgb[0];
						green[offset] = rgb[1] > 255 ? 255 : rgb[1];
						blue[offset] = rgb[2] > 255 ? 255 : rgb[2];
						offset++;
					}
				}

				System.out.println("Izračuni gotovi...");
				observer.acceptResult(red, green, blue, requestNo);
				System.out.println("Dojava gotova...");
			}
		};
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
