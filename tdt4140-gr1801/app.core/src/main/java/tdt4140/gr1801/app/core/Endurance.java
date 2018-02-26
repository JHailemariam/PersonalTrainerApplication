package tdt4140.gr1801.app.core;

import java.util.Date;

public class Endurance extends Training{
	
	private double distance; // I km
	private double averageSpeed; // I km/t
	private int caloriesBurned;
	
	
	public Endurance(Date date, int duration, double distance, int caloriesBurned) {
		super(date, duration);
		if (distance < 0) {
			throw new IllegalArgumentException("Distance cannot be negative");
		}
		if (caloriesBurned < 0) {
			throw new IllegalArgumentException("Calories burned cannot be negative");
		}
		this.distance = distance;
		this.averageSpeed = distance/(duration/60); // duration er i minutter, m� deles p� 60 for � f� i timer
		this.caloriesBurned = caloriesBurned;
		
		// TODO: m�linger av hjertefrekvens
	}
	
	public double getDistance() {
		return distance;
	}
	
	public double getAverageSpeed() {
		return averageSpeed;
	}
	
	public int getCaloriesBurned() {
		return caloriesBurned;
	}
	
	
	
	

}
