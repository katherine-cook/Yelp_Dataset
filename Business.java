package hw6;
import java.util.*;

public class Business {
  String businessID;
  String businessName;
  String businessAddress;
  String reviews;
  int reviewCharCount;
  int wordOccurence;

  public Business(String id, String name, String addr, String revs){ //constructor to initialize all of the variables each time a new business is read in
    businessID = id; 
    businessName = name; 
    businessAddress = addr; 
    reviews = revs; 
    this.charCount();
  }

  public String getID(){
    return businessID; //index arrOfBuisElems[0]
  }

  public String getName(){
    return businessName; //index arrOfBuisElems[1]
  }

  public String getAddress(){
    return businessAddress; //index arrOfBuisElems[2]
  }

  public String getReview(){
    return reviews; //index arrOfBuisElems[3]
  }

  public int charCount(){
    reviewCharCount = this.getReview().length();
    return reviewCharCount;
  }

  public Map<String, Integer> repeatedWordCount(){
    Map<String, Integer> reviewWordCount = new HashMap<String, Integer>();
    
    String[] words = this.getReview().split(" ");    
    
    for(String word: words){
      Integer oldCount = reviewWordCount.get(word); //tells you how many times the word occurs
      if(oldCount==null){ //if there are no repetitions of the word, its 0
        oldCount = 0;
      }
      reviewWordCount.put(word, oldCount +1);
    }
    return reviewWordCount;
  }

  
  public String toString() {
    return "\n------------------------------------------------------------------------------- \n"
          + "Business ID: " + businessID + "\n"
          + "Business Name: " + businessName + "\n"
          + "Business Address: " + businessAddress + "\n"
          //+ "Reviews: " + reviews + "\n"
          + "Character Count: " + reviewCharCount;
  }
}