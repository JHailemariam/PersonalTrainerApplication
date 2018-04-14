package tdt4140.gr1801.app.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.image.Image;
import tdt4140.gr1801.web.server.GetURL;

public class Client {
	private String name;
    
	private int id;
	private int height;
	private PersonalTrainer pt;
	private HashMap<String,Double> weights; // measured in float (kg)
	private HashMap<String,Double> fats; // measured in float [0,1]
	private HashMap<String,Image> pictureDates; //String with corresponding date, and url to picture link
	private List<Nutrition> nutritions;
    
	private List<Strength> strengthTraining;
	private List<Endurance> enduranceTraining;
    
    
	public Client(int id, String name, int height, PersonalTrainer pt) {
    		this.id = id;
    		this.name = name;
    		this.height = height;
    		this.pt = pt;
    		pt.addClient(this);

	    
    		this.weights = new HashMap<String,Double>();
    		this.fats = new HashMap<String,Double>();
    		this.pictureDates = new HashMap<String,Image>();
    		this.nutritions = new ArrayList<Nutrition>();
    		this.strengthTraining = new ArrayList<Strength>();
    		this.enduranceTraining = new ArrayList<Endurance>();
	}
    
    
    public int getId() {
    		return id;
    }
    
    public String getName() {
    		return name;
    }
    
    public int getHeight() {
    		return height;
    }
    
    public String getPersonalTrainer() {
    		return pt.getUsername();
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
    
    public static boolean checkFirstName(String firstName) {
		return (firstName.matches("[a-zA-Z]+"));
	}
	
	public static boolean checkLastName(String lastName) {
		return (lastName.matches("[a-zA-Z]+"));
	}
	
	public static boolean checkHeight(int height) {
		return height < 272 && height > 130;
	}
    
    //Funksjon som legger Client til i Klient-tablen i DB.
    public void createClient() throws IOException {
		JSONObject json = new JSONObject();
		json.put("Navn", this.name);
		json.put("Height", this.height);
		json.put("PT_ID", this.pt.getUsername());
		System.out.println(json);
		String respons = GetURL.postRequest("/signup/client", json);
		System.out.println(respons);
    }
    
    //Get all the Nutrition measurements for one given Client
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
    
    //Get all the EnduranceTraining´s for one given Client
    public void getClientEnduranceTraining() throws ClientProtocolException, IOException {
    		String data = GetURL.getRequest("/training/endurance/"+this.id);
    		JSONArray json = new JSONArray(data);
    		for (int n = 0; n < json.length(); n++) {
    			JSONObject object = json.getJSONObject(n);
    			String date = object.getString("Dato");
    			int duration = object.getInt("Duration");
    			double distance = object.getDouble("Distance");
    			int calories = object.getInt("CaloriesBurned");
    			Endurance e = new Endurance(date, duration, distance, calories);
    			this.enduranceTraining.add(e);
    			
    		}
    }
    
    public void getStrengthTrainings() throws ClientProtocolException, IOException {
    	//Get all strength training
    	String strengthData = GetURL.getRequest("/training/strength/"+ this.id);
    	JSONArray strengthJson = new JSONArray(strengthData);
    	for (int n = 0 ; n < strengthJson.length() ; n++ ) {
    		JSONObject strengthObject  = strengthJson.getJSONObject(n);
    		int strengthID = strengthObject.getInt("StrengthID");
    		String date = strengthObject.getString("Dato");
    		int duration = strengthObject.getInt("Duration");    		
    		
    		//Get all exercises for every strength training, we do not need sets, because that would be the length of repsList anyway
    		List<Exercise> exerciseList = new ArrayList<Exercise>();
    		String exData = GetURL.getRequest("/training/exercise/"+strengthID);
    		JSONArray exJson = new JSONArray(exData);
    		for (int m = 0 ; m < exJson.length() ; m++) {
    			JSONObject exObject = exJson.getJSONObject(m);
    			String navn = exObject.getString("Navn");
    			double weight = exObject.getDouble("Weight");
    			//Need to split the reps in to a list of integers. String format "d-d-d-d...." where d  is how many reps that set
    			String reps = exObject.getString("Reps");
    			List<Integer> repsList = Arrays.asList(reps.split("-")).stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
    			//Make a new Exercise object that is going to be added to exerciseList.
    			exerciseList.add(new Exercise(navn, weight, repsList));
    		}
    		
    		//For every Strength training we need to make a new Strength object that is going to be added this objects StrengthTraining list
    		Strength strength  = new Strength(date, duration, exerciseList); 
    		addStrengthTraining(strength);
    	}
    }
    
    //Gets all the Weight and Fats measurements for one given Client
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
    
    //Gets all the ImageUrl´s for one given client
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
    
    //Returns a list of picture dates sorted
    public List<String> getPictureDates(){
    		List<String> dates = new ArrayList<String>();
    		
    		for(String key : pictureDates.keySet()) {
    			dates.add(key);
    		}
    		Collections.sort(dates);
    		return dates;
    }
    
    public Image getImage(String date) {
    		return this.pictureDates.get(date);
    }
    
    
    public String toString() {
    	return name;
    }
    

}