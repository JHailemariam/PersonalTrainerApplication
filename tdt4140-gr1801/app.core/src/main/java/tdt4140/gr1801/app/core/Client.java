package tdt4140.gr1801.app.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.image.Image;
import tdt4140.gr1801.web.server.GetURL;

// Class with information about a client 
public class Client {

	private String name;
	private int id;
	private int height;
	private int maxPulse;
	private PersonalTrainer pt;
	private HashMap<String,Double> weights; // (date, measured in float (kg))
	private HashMap<String,Double> fats; // (date, fat measured in float [0,1])
	private HashMap<String,Image> pictureDates; // string with corresponding date, and url to picture link
	private List<Nutrition> nutritions;
	private List<Strength> strengthTraining;
	private List<Endurance> enduranceTraining;
	private List<DayProgram> program; // daily program for client
    
    
	public Client(int id, String name, int height, PersonalTrainer pt, int maxPulse) {
    		this.id = id;
    		this.name = name;
    		this.height = height;
    		this.maxPulse = maxPulse;
    		this.pt = pt;
    		pt.addClient(this);

    		this.weights = new HashMap<String,Double>();
    		this.fats = new HashMap<String,Double>();
    		this.pictureDates = new HashMap<String,Image>();
    		this.nutritions = new ArrayList<Nutrition>();
    		this.strengthTraining = new ArrayList<Strength>();
    		this.enduranceTraining = new ArrayList<Endurance>();
    		this.program = new ArrayList<DayProgram>();
	}
	
	public Client(int id, String name, int height, PersonalTrainer pt) {
		this.id = id;
		this.name = name;
		this.height = height;
		this.pt = pt;
		pt.addClient(this);

    
		this.weights = new HashMap<String,Double>();
		this.fats = new HashMap<String,Double>();
		this.nutritions = new ArrayList<Nutrition>();
		this.strengthTraining = new ArrayList<Strength>();
		this.enduranceTraining = new ArrayList<Endurance>();
}
    
    // Getters
    public int getId() {
    		return id;
    }
    
    public String getName() {
    		return name;
    }
    
    public int getHeight() {
    		return height;
    }
    
    public int getMaxPulse() {
    	return this.maxPulse;
    }
    
    public String getPersonalTrainer() {
    		return pt.getUsername();
    }
    
    public PersonalTrainer getPersonalTrainerObject() {
    	return this.pt;
    }
    
    public Double getWeight(String date){
    		if (this.weights.containsKey(date)) {
    			return this.weights.get(date);
    		} else {
    			throw new IllegalArgumentException("No weight registered for this date");
    		}
    }
    
    public List<Strength> getStrengthList(){
    		return this.strengthTraining;
    }
    
    public List<Endurance> getEnduranceList(){
    		return this.enduranceTraining;
    }
    
    public List<Nutrition> getNutritionList(){
    		return this.nutritions;
    }
    
    public List<DayProgram> getDayProgramList(){
    	return this.program;
    }
    
    public HashMap<String,Double> getWeightMap(){
    		return this.weights;
    }
    
    public HashMap<String,Double> getFatMap(){
    		return this.fats;
    }
    
    public Double getFat(String date){
    		if (this.fats.containsKey(date)) {
    			return this.fats.get(date);
    		} else {
    			throw new IllegalArgumentException("No fat percent registered for this date");
    		}
    }
    
    public Nutrition getNutrition(String date) {
    		for (Nutrition nutrition : nutritions) {
    			if (nutrition.getDate().equals(date)){
				return nutrition;
    			}
    		} throw new IllegalArgumentException("No nutrition registered for this date");
    }
    
    public Image getImage(String date) {
		return this.pictureDates.get(date);
    }
    
    // returns a list of picture sorted by date
    public List<String> getPictureDates(){
    		List<String> dates = new ArrayList<String>();
    		for(String key : pictureDates.keySet()) {
    			dates.add(key);
    		}
    		Collections.sort(dates);
    		return dates;
    }
  
    // add-methods 
    public void addWeight(String date, double weight) {
    		if (weight>0.0 && weight<400.0) {
    			this.weights.put(date, weight);
    		}else {
    			throw new IllegalArgumentException("Not valid weight, must be in range [0,400]");
    		}
    }
    
    public void addFat(String date, Double fat) {
    		if (fat>0.0 && fat<100) {
    			this.fats.put(date,fat);
    		} else {
    			throw new IllegalArgumentException("Not valid fat percent, must be in range [0,100]");
    		}
    }
    
    public void addNutrition(Nutrition nutrition) {
    		this.nutritions.add(nutrition);
    }
 
    public void addStrengthTraining(Strength training) {
    		this.strengthTraining.add(training);
    }
    
    public void addEnduranceTraining(Endurance training) {
    		this.enduranceTraining.add(training);
    }
    
    // check-methods: 
    // checking for valid input in different variables for client 
    public static boolean checkFirstName(String firstName) {
		return (firstName.matches("[a-zA-Z]+"));
	}
	
	public static boolean checkLastName(String lastName) {
		return (lastName.matches("[a-zA-Z]+"));
	}
	
	public static boolean checkHeight(int height) {
		return height < 272 && height > 130;
	}
	
	public static boolean checkmaxPulse(int maxPulse) {
		return maxPulse < 500 && maxPulse > 100;
	}
    
    // function that adds client to client tab in the database
    public void createClient() throws IOException {
		JSONObject json = new JSONObject();
		json.put("Navn", this.name);
		json.put("Height", this.height);
		json.put("PT_ID", this.pt.getUsername());
		json.put("MaxPulse", this.getMaxPulse());
		System.out.println(json);
		String respons = GetURL.postRequest("/signup/client", json);
		System.out.println(respons);
    }
    
    // function that adds client's weekly program to program-tab in the database
    public void createWeeklyProgram(DayProgram dp) throws IOException {
    	JSONObject json = new JSONObject();
		json.put("ClientID",this.id);
		json.put("Day", dp.getWeekday());
		json.put("Duration", dp.getDuration());
		json.put("Distance", dp.getDistance());
		json.put("Speed", dp.getAvgSpeed());
		json.put("Description", dp.getDescription());
		String exercises = "";
		for (Exercise e : dp.getExercises()) {
			exercises += e.getName() + ",";
			exercises += e.getWeight() + ",";
			for (int rep : e.getRepsPerSet()) {
				exercises += rep + "-";
			}
			exercises = exercises.substring(0, exercises.length()-1);
			exercises += "#";
		}
		exercises = exercises.substring(0, exercises.length()-1);
		json.put("Exercises", exercises);
		System.out.println("inside createWeeklyProgram in Client");
		System.out.println(json);
		String respons = GetURL.postRequest("/weeklyprogram/client", json);
		System.out.println(respons);
    }
    
    // the next get-functions are functions to collect data from the database and into the application 
    // collects all nutrition data for a client 
    public void getClientNutrition() throws ClientProtocolException, IOException {
    		String data = GetURL.getRequest("/nutrition/"+this.id);
    		JSONArray json = new JSONArray(data);
    		for (int n = 0; n < json.length(); n++) {
    			JSONObject object = json.getJSONObject(n);
    			String date = object.getString("Dato");
    			int cals = object.getInt("Calories");
    			int fat = object.getInt("Fat");
    			int carbs = object.getInt("Carbs");
    			int protein = object.getInt("Protein");
    			
    			Nutrition nutrition = new Nutrition(date, cals, fat, carbs, protein, this.id);
    			this.nutritions.add(nutrition);
    		}
    }
    
    // collects weekly program for a client
    public void getClientProgram() throws ClientProtocolException, IOException {
    	String data = GetURL.getRequest("/weeklyprogram/"+Integer.toString(this.id));
    	JSONArray json = new JSONArray(data);
		for (int n = 0; n < json.length(); n++) {
			DayProgram dayprogram;
			JSONObject object = json.getJSONObject(n);
			String day = object.getString("Day");
			// Check if row is endurance or strength
			if (object.getString("Exercises").length() < 4) {
				// if row is endurance
				int duration = object.getInt("Duration");
				double distance = object.getDouble("Distance");
				double speed = object.getDouble("Speed");
				String description = object.getString("Description");
				dayprogram = new DayProgram(day, duration, distance, speed, 
						description, null);
			}
			else {
				// if row is strength
				List<Exercise> exercises = new ArrayList<>();
				// iterating over the string to get the different exercises
				List<String> stringExercises = new ArrayList<String>(Arrays.asList(object.getString("Exercises").split("#")));
				for (String ex : stringExercises) {
					String[] info = ex.split(",");
					String name = info[0];
					double weight = Double.parseDouble(info[1]);
					List<Integer> repsList = Arrays.asList(info[2].split("-"))
			    			.stream().map(r -> Integer.parseInt(r)).collect(Collectors.toList());
					Exercise e = new Exercise(name, weight, repsList);
					exercises.add(e);
				}
				dayprogram = new DayProgram(day, null, null, null,
						null, exercises);
			}
			program.add(dayprogram);
		}
    }
    
    // collects all endurance training data for a client from database
    public void getClientEnduranceTraining() throws ClientProtocolException, IOException {
    		String data = GetURL.getRequest("/training/endurance/"+this.id);
    		JSONArray json = new JSONArray(data);
    		for (int n = 0; n < json.length(); n++) {
    			JSONObject object = json.getJSONObject(n);
    			String date = object.getString("Dato");
    			int duration = object.getInt("Duration");
    			double distance = object.getDouble("Distance");
    			int calories = object.getInt("CaloriesBurned");
    			int maxPulse = object.getInt("MaxPulse");
    			int avgPulse = object.getInt("AvgPulse");
    			Endurance e = new Endurance(date, duration, distance, calories, maxPulse, avgPulse);
    			this.enduranceTraining.add(e);
    			
    		}
    }
    
 // collects all strength training data for a client from database
    public void getStrengthTrainings() throws ClientProtocolException, IOException {
    	String strengthData = GetURL.getRequest("/training/strength/"+ this.id);
    	JSONArray strengthJson = new JSONArray(strengthData);
    	for (int n = 0 ; n < strengthJson.length() ; n++ ) {
    		JSONObject strengthObject  = strengthJson.getJSONObject(n);
    		int strengthID = strengthObject.getInt("StrengthID");
    		String date = strengthObject.getString("Dato");
    		int duration = strengthObject.getInt("Duration");    		
    		
    		// getting all exercises for every strength training, we do not need sets,
    		// because that would be the length of repsList anyway
    		List<Exercise> exerciseList = new ArrayList<Exercise>();
    		String exData = GetURL.getRequest("/training/exercise/"+strengthID);
    		JSONArray exJson = new JSONArray(exData);
    		for (int m = 0 ; m < exJson.length() ; m++) {
    			JSONObject exObject = exJson.getJSONObject(m);
    			String navn = exObject.getString("Navn");
    			double weight = exObject.getDouble("Weight");
    	
    			// have to split the reps into a list of integers. 
    			// String format "d-d-d-d...." where d  is how many reps that set
    			String reps = exObject.getString("Reps");
    			List<Integer> repsList = Arrays.asList(reps.split("-")).stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
    			// making a new Exercise object that is going to be added to exerciseList.
    			exerciseList.add(new Exercise(navn, weight, repsList));
    		}
    		
    		// for every Strength training we have to make a new Strength object 
    		// that is going to be added this objects StrengthTraining list
    		Strength strength  = new Strength(date, duration, exerciseList); 
    		addStrengthTraining(strength);
    	}
    }
    
    // getting all the Weight and Fats measurements from the database for one given Client
    public void getClientWeightFat() throws ClientProtocolException, IOException {
    		String data = GetURL.getRequest("/client/weightfat/"+this.id);
    		if (!data.equals("[]")) {
    			JSONArray json = new JSONArray(data);
    			for (int i = 0; i < json.length() ; i++ ) {
    				JSONObject jsonObj = json.getJSONObject(i);
    				String date = jsonObj.getString("Dato");
    				Double weight = jsonObj.getDouble("Weight");
    				Double fat = jsonObj.getDouble("Fat");
    				this.addWeight(date, weight);
    				this.addFat(date, fat);
    			}
    		}
    }
    
    // getting all the ImageUrl´s from the database for one given client
    public void getClientPictures() throws ClientProtocolException, IOException{
    		String data = GetURL.getRequest("/client/pics/"+this.id);
    		if(!data.equals("[]")) {
    			JSONArray json = new JSONArray(data);
    			for(int i = 0; i < json.length(); i ++) {
    				JSONObject jsonObj = json.getJSONObject(i);
    				String date = jsonObj.getString("Dato");
    				String url = jsonObj.getString("ImageURL");
    				Image image = new Image(url);
    				pictureDates.put(date, image);
    			}
    		}
    }
   
    public String toString() {
    		return name;
    }
}