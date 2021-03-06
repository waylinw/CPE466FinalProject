import java.io.*;
import java.util.*;
import java.lang.*;
public class Player{
   private int id;
   private String name;
   private int HOF;
   private String position;
   private ArrayList<Double> stats;
   private ArrayList<Double> normalStats=new ArrayList<Double>();
   public void addNormal(double addVal){
      normalStats.add(addVal);
   }
   public Player(String fileLine){
      String[] split = fileLine.split("\t");
      this.id = Integer.parseInt(split[0].replace("\"", ""));
      this.name = split[1].replace("\"", "");
      this.position=split[25].replace("\"", "");
      this.HOF=Integer.parseInt(split[27]);

      stats = new ArrayList<Double>();
      for (int i = 2; i < 25; i++){
         String value = split[i].replace("\"", "");
         if (value.equals(".")) {
            stats.add(0.0);
         }
         else {
            stats.add(Double.parseDouble(value));
         }
      }
      stats.add(Double.parseDouble(split[26]));
   }
   public Player(ArrayList<Double> center){
      this.stats = center;
      this.normalStats = center;
   }

   public double getOldStat(int index) {
      return stats.get(index);
   }

   public double getStat(int index){
      return normalStats.get(index);
   }

   public int getNumberOfStats(){
      return stats.size();
   }

   public int getHof() {
      return HOF == 0 ? 0 : 1;
   }

   public int getPosition() {
      ArrayList<String> done = new ArrayList<>(Arrays.asList("1","2","3"));

      if (done.contains(position)){
         return Integer.parseInt(position);
      }
      HashMap<String,Integer> correspond=new HashMap<>();
      correspond.put("S",6);
      correspond.put("O",7);
      correspond.put("C",5);
      correspond.put("D",8);
      return correspond.get(position);
   }
   public String toString(){
      String returnVal="";
      returnVal+=name+", "+stats.toString().substring(1,stats.toString().length()-1)+", "+position+", "+HOF;
      return returnVal;
   }
}
