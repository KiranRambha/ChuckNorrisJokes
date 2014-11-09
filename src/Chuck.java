import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Scanner;
/**
 * 
 * @author kiran
 */
public class Chuck {
	
		/**
		 * @param builder used for wrapping the characters
		 */
		static StringBuilder builder;
		
		/**
		 * @param buffer used to read the webpage
		 */
		static BufferedReader buffer;
		
		/**
		 * @param web The url from where the data is needed
		 */
		static URL web;
		
		/**
		 * @param connection used to open the connection and transfer the data
		 */
		static URLConnection connection;
		
		/**
		 * @param map this is a hashmap to store the jokes
		 */
		static HashMap<Integer, String> map;
		
		/**
		 * 
		 * @param json takes the url in json format and extracts the joke part in it
		 * @param tempstring This is to store the string in json format
		 * @return returns the joke part
		 */
		public static String extractJoke(String json){
			String tempstring = null;
			Integer startextractingat = null, finishextractingat = null;
			try{
				startextractingat = json.indexOf("\"joke\": \"") + "\"joke\": \"".length();;
				finishextractingat = startextractingat;
				while(json.charAt(finishextractingat) != '"'){
					finishextractingat++;
				}
				tempstring = json.substring(startextractingat, finishextractingat); 
				if(tempstring.contains("&quot;")){
					tempstring = tempstring.replaceAll("&quot;", Character.toString('"'));
				}
			}catch(Exception e){
				System.out.println("there is no joke here");
			}
			return tempstring;
		}
		
		/**
		 * 
		 * @param json takes the url in json format and extracts the id part in it
		 * @return returns the extracted id part
		 */
		public static int extractId(String json){
			int startextractingat,finishextractingat = 0;
			int id = 0;
			try{
				startextractingat = json.indexOf("id\": ") + "id\": ".length();
				finishextractingat = startextractingat;
				while(json.charAt(finishextractingat)!=','){
					finishextractingat++;
				}
				id = Integer.parseInt(json.substring(startextractingat, finishextractingat));
			}catch(Exception e){}
			return id;
		}
		
		/**
		 * 
		 * @param json takes the url in json format and count's the number of character in the url
		 * @return returns the total number of characters in the url
		 */
		public static int extractCount(String json){
			return json.length();
		}
		
		/**
		 * 
		 * @param url The url of the database online to which we need to connect, the connection will be established and
		 *  the data will be stored into a string 
		 * @return the whole data stored in the string will be returned
		 */
		public static String requestURL(String url){
			builder = new StringBuilder();
			try {
				web = new URL(url);
				connection = web.openConnection();
				buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String  temp;
				while((temp=buffer.readLine()) != null){
					builder.append(temp);
					}
				buffer.close();
			} catch (MalformedURLException e) {
				System.out.println("URL not valid!");
			} catch (IOException e) {
				System.out.println("Connection could not be made");
			}
			return builder.toString();
		}
		
		/**
		 * 
		 * @param joke this is the joke to be entered into the database
		 * @param nb this is the position at which the joke is to be inserted
		 */
		public static void addJoke(String joke, int nb){
			map.put(nb, joke);
		}
		
		/**
		 * 
		 * @param nb this is the joke number in the database
		 * @return returns true if there is a joke at the given number
		 */
		public static boolean haveJoke(int nb){
			String json = requestURL("http://api.icndb.com/jokes/"+nb);
			Integer i = null;
			i = extractId(json); 
			if(i!=null){
				return true;
			}else{
				return false;
			}
		}
		
		/**
		 * 
		 * @param nb this is the joke number in the database
		 * @return returns the joke from the given number in json format
		 */
		public static String getJoke(int nb){
			return requestURL("http://api.icndb.com/jokes/"+nb);
		}
		
		/**
		 * 
		 * @return Requests the database for a random joke and then returns the whole joke in json format
		 */
		public static String getJoke(){
			return requestURL("http://api.icndb.com/jokes/random");
		}
		
		/**
		 * 
		 * @return returns the number of jokes in the database
		 */
		public static Integer getCount(){
			String tempcount = requestURL("http://api.icndb.com/jokes/count");
			Integer count = null,startcount = null, finishcount = null;
			try{
				startcount = tempcount.indexOf("\"value\": ") + "\"value\": ".length();
				finishcount = startcount;
				while(tempcount.charAt(finishcount)!=' '){
					finishcount++;
				}
				count = Integer.parseInt(tempcount.substring(startcount, finishcount));
			}catch(Exception e){}
			return count;
		}
		
		public static void main(String[] args) throws Exception{
			Scanner scanner = new Scanner(System.in);
			map = new HashMap<Integer, String>();
			int i = 0, id = 0;
			String json = null;
			String temp = null;
			System.out.println("Welcome To the Internet Chuck Norris database!");
			System.out.println("The total number of jokes in the Chuck Norris database is: " + getCount());
			System.out.println("Joke of the Day: ");
			temp = getJoke();
			json = temp;
			System.out.println("Joke number " + extractId(temp) + ": " +extractJoke(json));
			System.out.println("*******************");
			System.out.println("(-1) Quit \n(0) Random Joke \n(n) Joke number n \nWhat do you want to do? ");
			System.out.println("*******************");
			while(i!=-1){
				i = scanner.nextInt();
				if(i==0){
					temp = getJoke();
					json = temp;
					json = extractJoke(json);
					id = extractId(temp);
				}else if(i==-1){
					System.out.println("Your joke's history is: \n" + map.toString());
					map.clear();
					System.out.println("****************************************************");
					System.out.println("Thanks for using the Internet Chuck Norris database!");
					System.out.println("****************************************************");
					break;
				}else if(i>0){
					if(!map.containsKey(i)){
						temp = getJoke(i);
						json = temp;
						json = extractJoke(json);
						id = extractId(temp);
					}else{
						json = null;
						id = 0;
						System.out.println("You already browsed joke number "+ i +": "+ map.get(i));
					}
				}else{
					json = null;
					id = 0;
					System.out.println("Sorry! input not valid \nEnter again! ");
				}
				map.put(id, json+"\n");
				System.out.println("Joke number " + id + ": " + json);
				System.out.println("*******************");
				System.out.println("(-1) Quit \n(0) Random Joke \n(n) Joke number n \nWhat do you want to do? ");
				System.out.println("*******************");
			}
			scanner.close();
		}
	}