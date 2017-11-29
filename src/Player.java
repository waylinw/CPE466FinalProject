import java.io.*;
import java.util.*;
import java.lang.*;
public class Player{
   private int id;
   private String name;
   private int HOF;
   private String position;
   private ArrayList<Double> stats;
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
      this.stats=center;
   }

   public double getStat(int index){
      return stats.get(index);
   }

   public int getNumberOfStats(){
      return stats.size();
   }

   public int getHof() {
      return HOF == 0 ? 0 : 1;
   }

   public int getPosition() {
      ArrayList<Integer> done = new ArrayList<Integer>(Arrays.asList(0,1,2,3));
      
      if (done.contains(position)){
         return Integer.parseInt(position);
      }
      HashMap<String,Integer> correspond=new HashMap<String,Integer>();
      correspond.put("S",6);
      correspond.put("O",7);
      correspond.put("C",5);
      correspond.put("D",8);
      return correspond.get(position);
   }
}
