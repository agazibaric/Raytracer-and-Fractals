package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

/**
 * Program that allows user to enter complex roots and prints out related fractal image. </br>
 * Minimum number of complex roots is 2. </br>
 * Complex roots must be in form: a +/- ib, where 'a' and 'b' are any real numbers. </br>
 * 'b' must be positive, but you can make it negative by adding '-' in front of 'i'. </br>
 * User can leave out just real or just imaginary part considering those equal to zero, </br>
 * but it can not input empty string.
 * 
 * @author Ante Gazibaric
 * @version 1.0
 *
 */
public class Newton {
	
	/** prompt string format */
	private static final String promptFormat = "Root %d> ";
	/** string represents end of user's input */
	private static final String doneString = "done";
	/** roots of complex polynomial */
	private static Complex[] roots = new Complex[] {};
	/** complex polynomial in root form */
	private static ComplexRootedPolynomial rootedPoly;
	/** complex polynomial */
	private static ComplexPolynomial poly;
	/** derived complex polynomial */
	private static ComplexPolynomial derived;
	/** convergence treshold */
	private static final double CONVERGENCE_TRESHOLD = 1E-3;
	/** root index treshold */
	private static final double ROOT_TRESHOLD = 2E-3;
	/** min number of roots needed from user to input */
	private static final int minRoots = 2;
	/** message to user at the beginning of program */
	private static final String messageToUser = 
			"Welcome to Newton-Raphson iteration-based fractal viewer.\n" +
			"Please enter at least two roots, one root per line. Enter 'done' when done.";
	

	/**
	 * Main method. Accepts no arguments.
	 *  
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		
		try (Scanner sc = new Scanner(System.in)) {
			getUserInput(sc);
		} catch (IllegalArgumentException ex) {
			System.err.println(ex.getMessage());
			return;
		}
		System.out.println("Image of fractal will appear shortly. Thank you.");
		FractalViewer.show(new FractalProducerImpl());
	}
	
	/**
	 * Method gets complex root inputs from user
	 * 
	 * @param sc {@link Scanner} to receive inputs of complex roots
	 */
	private static void getUserInput(Scanner sc) {
		List<Complex> complexInputs = new LinkedList<>();
		int numberOfRoots = 1;
		System.out.println(messageToUser);
		while (true) {
			printPropt(numberOfRoots);
			String userInput = sc.nextLine().trim();
			if (userInput.toLowerCase().equals(doneString))
				break;
			if (userInput.isEmpty()) {
				System.out.println("Given root must not be empty string.");
				continue;
			}
			try {
				Complex complexInput = getComplexFrom(userInput);
				complexInputs.add(complexInput);
				numberOfRoots++;
			} catch (IllegalArgumentException ex) {
				System.out.println(ex.getMessage());
			}
		}
		
		if (complexInputs.size() < minRoots) {
			throw new IllegalArgumentException(String.format("You must enter at least 2 roots.%n"
					+ "Was: %d.%n", complexInputs.size()));
		} else {
			roots = complexInputs.toArray(roots);
			rootedPoly = new ComplexRootedPolynomial(roots);
			poly = rootedPoly.toComplexPolynom();
			derived = poly.derive();
		}
	}
	
	/**
	 * Method prints prompt symbol
	 * 
	 * @param i number of root to input
	 */
	private static void printPropt(int i) {
		System.out.format(promptFormat, i);
	}
	
	/**
	 * Method transforms given {@code input} into {@link Complex} number </br>
	 * if input is valid representation of complex number.
	 * 
	 * @param input input that represents complex number in string form
	 * @return      complex number obtained from given input
	 */
	private static Complex getComplexFrom(String input) {
		double re = 0.0;
		double im = 0.0;
		// Remove whitespaces
		String s = input.replaceAll("\\s+", "");
		
		// Complex number that contains just real part
		Pattern p1 = Pattern.compile("([-|+]?[0-9]+\\.?[0-9]*?)$");
		// Complex number that contains just imaginary part
		Pattern p2 = Pattern.compile("([-|+]?i[0-9]+\\.?[0-9]*?)$");
		// Complex number that contains real and imaginary part
		Pattern p3 = Pattern.compile("([-|+]?[0-9]+\\.?[0-9]*?)([-|+]i[0-9]+\\.?[0-9]*?)$");
		// Complex number that contains just single i or -i
		Pattern p4 = Pattern.compile("([-|+]?)(i$)");
		// Complex number that contains real part with imaginary part with single i or -i
		Pattern p5 = Pattern.compile("([-|+]?[0-9]+\\.?[0-9]*?)([-|+])i$");

		Matcher m1 = p1.matcher(s);
		Matcher m2 = p2.matcher(s);
		Matcher m3 = p3.matcher(s);
		Matcher m4 = p4.matcher(s);
		Matcher m5 = p5.matcher(s);

		if (m3.find()) {
			re = Double.parseDouble(m3.group(1));
			im = Double.parseDouble(m3.group(2).replace("i", ""));
		} else if (m2.find()) {
			im = Double.parseDouble(m2.group(1).replace("i", ""));
		} else if (m5.find()) {
			re = Double.parseDouble(m5.group(1));
			im = Double.parseDouble(m5.group(2).concat("1"));
		} else if (m1.find()) {
			re = Double.parseDouble(m1.group(1));
		} else if (m4.find()){
			// length must be less then 3, otherwise it's invalid input
			if (s.length() > 2)
				throw new IllegalArgumentException("Can not interepret input as a complex number. Was: " + input);
			if (s.length() == 2) {
				char firstChar = s.charAt(0);
				if (firstChar != '-' && firstChar != '+')
					throw new IllegalArgumentException("Can not interepret input as a complex number. Was: " + input);
			}
			im = Double.parseDouble(m4.group(1).concat("1"));
		} else {
			// No matching, invalid string input
			throw new IllegalArgumentException("Can not interepret input as a complex number. Was: " + input);
		}
		return new Complex(re, im);
	}
	
	/**
	 * Class represents implementation of {@link Callable} interface </br>
	 * that calculates one part of whole needed calculation job for Newton-Raphson fractal image.
	 * 
	 * @author Ante Gazibaric
	 * @version 1.0
	 *
	 */
	public static class Job implements Callable<Void> {
		
		/** min value for real part of complex number */
		double reMin;
		/** max value for real part of complex number */
		double reMax;
		/** min value for imaginary part of complex number */
		double imMin;
		/**  max value for imaginary part of complex number */
		double imMax;
		/** width of window */
		int width;
		/** height of window */
		int height;
		/** min value for y component */
		int yMin;
		/** max value for y component */
		int yMax;
		/** max number of iterations done to determine the value that is looked for */
		int maxIterations;
		/** array of data that is filled */
		short[] data;

		/**
		 * Constructor that creates new {@link Job} object.
		 * 
		 * @param reMin  		{@link #reMin}
		 * @param reMax  		{@link #reMax}
		 * @param imMin  		{@link #imMin}
		 * @param imMax  		{@link #imMax}
		 * @param width  		{@link #width}
		 * @param height 		{@link #height}
		 * @param yMin  	    {@link #yMin}
		 * @param yMax   		{@link #yMax}
		 * @param maxIterations {@link #maxIterations}
		 * @param data          {@link #data}
		 */
		public Job(double reMin, double reMax, double imMin,
				double imMax, int width, int height, int yMin, int yMax, 
				int maxIterations, short[] data) {
			super();
			this.reMin = reMin;
			this.reMax = reMax;
			this.imMin = imMin;
			this.imMax = imMax;
			this.width = width;
			this.height = height;
			this.yMin = yMin;
			this.yMax = yMax;
			this.maxIterations = maxIterations;
			this.data = data;
		}
		
		@Override
		public Void call() {
			calculate(reMin, reMax, imMin, imMax, width, height, yMin, yMax, maxIterations, data);
			return null;
		}
	}
	
	/**
	 * Method calculates and fills data array with needed informations.
	 * 
	 * @param reMin  min value for real part of complex number
	 * @param reMax  max value for real part of complex number
	 * @param imMin  min value for imaginary part of complex number
	 * @param imMax  max value for imaginary part of complex number
	 * @param width  width of window
	 * @param height height of window
	 * @param yMin   min value of y component
	 * @param yMax   max value of y component
	 * @param m      number of iterations
	 * @param data   data array that is filled with needed informations
	 */
	public static void calculate(double reMin, double reMax, double imMin,
				double imMax, int width, int height, int yMin, int yMax, 
				int m, short[] data) {
		
		int offset = yMin * width;
		for (int y = yMin; y <= yMax; y++) {
			for (int x = 0; x < width; x++) {
				Complex c = mapToComplexPlain(x, y, width, height, reMin, reMax, imMin, imMax);
				Complex zn = c, zn1 = null;
				int numberOfIterations = 0;
				double module;
				do {
					Complex numerator = poly.apply(zn);
					Complex denominator = derived.apply(zn);
					Complex fraction = numerator.divide(denominator);
					zn1 = zn.sub(fraction);
					module = zn1.sub(zn).module();
					zn = zn1;
					numberOfIterations++;
				} while (module > CONVERGENCE_TRESHOLD && numberOfIterations < m);
				
				int index = rootedPoly.indexOfClosestRootFor(zn1, ROOT_TRESHOLD);
				data[offset++] = (short) (index + 1);
			}
		}
		
	}
	
	/**
	 * Method maps given {@code x} and {@code y} to the complex plain.
	 * 
	 * @param x      x component
	 * @param y		 y component
	 * @param width  width of window
	 * @param height height of window
	 * @param reMin  min value for real part of complex number
	 * @param reMax  max value for real part of complex number
	 * @param imMin  min value for imaginary part of complex number
	 * @param imMax  max value for imaginary part of complex number
	 * @return       {@link Complex} that represents given x and y mapped to complex plain
	 */
	private static Complex mapToComplexPlain(int x, int y,int width, int height, double reMin,
			double reMax, double imMin, double imMax) {

		double re = x / (width - 1.0) * (reMax - reMin) + reMin;
		double im = (height - 1.0 - y) / (height - 1.0) * (imMax - imMin) + imMin;
		return new Complex(re, im);
	}
	
	/**
	 * Class represents implementation of {@link IFractalProducer} that creates Newton-Raphson fractal.
	 * 
	 * @author Ante Gazibaric
	 * @version 1.0
	 *
	 */
	public static class FractalProducerImpl implements IFractalProducer {
		
		/** thread pool */
		private ExecutorService pool;
		
		/**
		 * Constructor that creates new {@link FractalProducerImpl} object.
		 */
		public FractalProducerImpl() {
			initProducer();
		}
		
		/**
		 * Method initialize {@code FractalProducerImpl} object.
		 */
		private void initProducer() {
			pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread worker = new Thread(r);
					worker.setDaemon(true);
					return worker;
				}
			});
		}

		@Override
		public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height,
				long requestNo, IFractalResultObserver observer) {
			System.out.println("Zapocinjem izracun...");
			int maxIterations = 16 * 16 * 16;
			short[] data = new short[width * height];
			final int numberOfTracks = 8 * Runtime.getRuntime().availableProcessors();
			int brojYPoTraci = height / numberOfTracks;

			List<Future<Void>> jobs = new ArrayList<>();

			for (int j = 0; j < numberOfTracks; j++) {
				int yMin = j * brojYPoTraci;
				int yMax = (j + 1) * brojYPoTraci - 1;
				if (j == numberOfTracks - 1) {
					yMax = height - 1;
				}
				Job job = new Job(reMin, reMax, imMin, imMax, width, height, yMin, yMax, maxIterations, data);
				jobs.add(pool.submit(job));
			}
			
			for (Future<Void> job : jobs) {
				try {
					job.get();
				} catch (InterruptedException | ExecutionException e) {
				}
			}

			System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
			observer.acceptResult(data, (short) (poly.order() + 1), requestNo);
		}
		
	}

}
