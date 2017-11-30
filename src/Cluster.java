import java.lang.*;
import java.util.*;
public class Cluster{
   private ArrayList<Player> players;
   private Player center;

   public Cluster(Player center) {
      players = new ArrayList<>();
      this.center = center;
   }

   public void addPlayer(Player addition){
      players.add(addition);
   }
   
   public void computeCentroid(){
      ArrayList<Double> attributes = new ArrayList<Double>();
      for (int column = 0; column < center.getNumberOfStats(); column++) {
         double sum = 0.0;
         for (Player p : players) {
            sum += p.getStat(column);
         }
         attributes.add(sum / players.size());
      }

      this.center = new Player(attributes);
   }
//   public void computeMedianCentroid(){
//      ArrayList<Double> attributes=new ArrayList<Double>();
//      for (int column=0; column<center.getNumberOfStats(); column++){
//         ArrayList<Double> values=new ArrayList<Double>();
//         for (Player p: players){
//            values.add(p.getStat(column));
//         }
//         Collections.sort(values);
//         attributes.add(values.get(values.size()/2));
//      }
//      this.center=new Player(attributes);
//   }

   public Player getCenter() {
      return center;
   }

   public void resetPoints() {
      players = new ArrayList<>();
   }

   public ArrayList<Player> getPlayers() {
      return players;
   }
}
