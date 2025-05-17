import java.util.*;
import java.util.concurrent.*;

public class ItemsHandler{
    private static final ConcurrentHashMap<String, Integer> AVAILABLEITEMS = new ConcurrentHashMap<>();
    private static double dropChanceSum;
    static {
        //Use static initializer block to make hashmap of ITEM - DROPCHANCE
        // AVAILABLEITEMS.put("None", 60);
        // AVAILABLEITEMS.put("Redfish", 15);
        // AVAILABLEITEMS.put("Cat Treat", 15);
        // AVAILABLEITEMS.put("Milk", 4);
        // AVAILABLEITEMS.put("Premium Cat Food++", 4);
        // AVAILABLEITEMS.put("Goldfish", 2);
        // AVAILABLEITEMS.put("Light Scarf", 2);
        // AVAILABLEITEMS.put("Thick Sweater", 2);
        // AVAILABLEITEMS.put("Bag of Catnip", 2);
        // AVAILABLEITEMS.put("Loud Bell", 2);
        // AVAILABLEITEMS.put("Pringles Can", 2);

        AVAILABLEITEMS.put("None", 0);
        AVAILABLEITEMS.put("Redfish", 0);
        AVAILABLEITEMS.put("Cat Treat", 0);
        AVAILABLEITEMS.put("Milk", 0);
        AVAILABLEITEMS.put("Premium Cat Food++", 0);
        AVAILABLEITEMS.put("Goldfish", 0);
        AVAILABLEITEMS.put("Light Scarf", 0);
        AVAILABLEITEMS.put("Thick Sweater", 0);
        AVAILABLEITEMS.put("Bag of Catnip", 0);
        AVAILABLEITEMS.put("Loud Bell",0);
        AVAILABLEITEMS.put("Pringles Can", 100);
        for (Integer dropChance: AVAILABLEITEMS.values()){
            dropChanceSum += dropChance;
        }
    }
    
    public Item rollItem(Enemy enemy){
        //Get a random number between 0 to whatever drop chance is (in this case 100)
        double roll = Math.random() * dropChanceSum;
        double cumulativeChance = 0;

        for (Map.Entry<String, Integer> entry : AVAILABLEITEMS.entrySet()){
            double dropChance = entry.getValue();

            //Add to var to project all of the dropchances within their respective domains from the interval [0,100]
            cumulativeChance += dropChance;
            
            //Check to see if random number from 0-100 is within the selection interval for a specific item
            if (roll < cumulativeChance) {
                //Create a new item from the middle of the enemy
                System.out.print("SELECTED FROM SERVER: ");
                switch(entry.getKey()){
                    case "None":
                        System.out.println("0");
                        return null;
                    case "Redfish":
                        System.out.println("1");
                        return new RedFish(enemy.getWorldX(), enemy.getWorldY());
                    case "Cat Treat":
                        System.out.println("2");
                        return new CatTreat(enemy.getWorldX(), enemy.getWorldY());
                    case "Milk":
                        System.out.println("3");
                        return new Milk(enemy.getWorldX(), enemy.getWorldY());
                    case "Premium Cat Food++":
                        System.out.println("4");    
                        return new PremiumCatFood(enemy.getWorldX(), enemy.getWorldY());
                    case "Goldfish":
                        System.out.println("5");
                        return new Goldfish(enemy.getWorldX(), enemy.getWorldY());
                    case "Light Scarf":
                        System.out.println("6");
                        return new LightScarf(enemy.getWorldX(), enemy.getWorldY());
                    case "Thick Sweater":
                        System.out.println("7");
                        return new ThickSweater(enemy.getWorldX(), enemy.getWorldY());
                    case "Bag of Catnip":
                        System.out.println("8");
                        return new BagOfCatnip(enemy.getWorldX(), enemy.getWorldY());
                    case "Loud Bell":
                        System.out.println("9");
                        return new LoudBell(enemy.getWorldX(), enemy.getWorldY());
                    case "Pringles Can":
                        System.out.println("10");
                        return new PringlesCan(enemy.getWorldX(), enemy.getWorldY());
                }
            }
        }
        return null;
    }

}