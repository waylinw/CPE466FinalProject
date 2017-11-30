import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static ArrayList<Player> playerData;

    public static void main(String[] args) {
        playerData = readInput("data.txt");

        // Clustering on Hall of Fame, only need 2 clusters. Yes or No, so randomly pick 2 as starting
        int[] seeds = new Random().ints(0, playerData.size()).distinct().limit(2).toArray();
        ArrayList<Cluster> hallOfFame = new ArrayList<>();
        hallOfFame.add(new Cluster(playerData.get(seeds[0])));
        hallOfFame.add(new Cluster(playerData.get(seeds[1])));
        iterateClustering(100, hallOfFame, Main::L2Distance);
        hallOfFameCalcStats(hallOfFame);

       hallOfFame = new ArrayList<>();
       hallOfFame.add(new Cluster(playerData.get(seeds[0])));
       hallOfFame.add(new Cluster(playerData.get(seeds[1])));
       iterateClustering(100, hallOfFame, Main::L1Distance);

       hallOfFame = new ArrayList<>();
       hallOfFame.add(new Cluster(playerData.get(seeds[0])));
       hallOfFame.add(new Cluster(playerData.get(seeds[1])));
       iterateClustering(100, hallOfFame, Main::ChebychevDistance);
        
        // Clustering on positions
        seeds = new Random().ints(0, playerData.size()).distinct().limit(7).toArray();
        ArrayList<Cluster> positions = new ArrayList<>();
        for (int i = 0; i < seeds.length; i++) {
            positions.add(new Cluster(playerData.get(seeds[i])));
        }
        iterateClustering(1000, positions, Main::L2Distance);
        positionCalcStats(positions);
        
         seeds = new Random().ints(0, playerData.size()).distinct().limit(7).toArray();
        positions = new ArrayList<>();
        for (int i = 0; i < seeds.length; i++) {
            positions.add(new Cluster(playerData.get(seeds[i])));
        }
        iterateClustering(1000, positions, Main::L1Distance);
        positionCalcStats(positions);
        
         seeds = new Random().ints(0, playerData.size()).distinct().limit(7).toArray();
        positions = new ArrayList<>();
        for (int i = 0; i < seeds.length; i++) {
            positions.add(new Cluster(playerData.get(seeds[i])));
        }
        iterateClustering(1000, positions, Main::ChebychevDistance);
        positionCalcStats(positions);

        //playerData.stream().map(player -> player.getPosition()).forEach(System.out::println);
    }

    static void iterateClustering(int iteration, ArrayList<Cluster> clusters, BiFunction<Cluster, Player, Double> distanceFunction) {
        for (int i = 0; i < iteration; i++) {
            clusters.stream().forEach(cluster -> cluster.resetPoints());
            for (Player p: playerData) {
                double [] distances = clusters.stream().mapToDouble(cluster -> distanceFunction.apply(cluster, p)).toArray();
                clusters.get(IntStream.range(0, clusters.size()).reduce((a, b) -> distances[a] < distances[b] ? a : b).getAsInt()).addPlayer(p);
            }
            clusters.stream().forEach(cluster -> cluster.computeCentroid());
        }
    }

    static double L2Distance(Cluster c, Player p) {
        double sumOfSqrs = 0.0;
        Player center = c.getCenter();
        for (int i = 0; i < p.getNumberOfStats(); i++) {
            sumOfSqrs += Math.pow(center.getStat(i) - p.getStat(i), 2);
        }
        return Math.sqrt(sumOfSqrs);
    }

    static double L1Distance(Cluster c, Player p) {
      double sumOfAbs=0.0;
      Player center=c.getCenter();
      for (int i = 0; i < p.getNumberOfStats(); i++) {
         sumOfAbs += Math.abs(center.getStat(i) - p.getStat(i));
      }
      return sumOfAbs;
    }
    static double ChebychevDistance(Cluster c, Player p){
      double max=0.0;
      Player center=c.getCenter();
      for (int i = 0; i < p.getNumberOfStats(); i++) {
         double current=Math.abs(center.getStat(i) - p.getStat(i));
         if (current>max){
            max=current;
         }
      }
      return max;
    }


    static void hallOfFameCalcStats(ArrayList<Cluster> hof) {
        ArrayList<Double> entropies = new ArrayList<>(), purities = new ArrayList<>();
        System.out.println("Hall of Fame Stats");
        for (Cluster c : hof) {
            ArrayList<Player> cur = c.getPlayers();
            double numHof = cur.stream().mapToInt(player -> player.getHof()).sum() * 1.0;
            double numOther = cur.size() - numHof;
            System.out.println("hof: " + numHof + " other: " + numOther);
            double a = -numHof/cur.size() * Math.log(numHof/cur.size()) / Math.log(2);
            double b = -numOther/cur.size() * Math.log(numOther / cur.size()) / Math.log(2);
            System.out.println(a+b);
            entropies.add((a + b) * cur.size() / playerData.size());

            double max = numHof > numOther ? numHof : numOther;
            purities.add(max / playerData.size());
            System.out.println(max / cur.size());
        }
        System.out.println("Entropy: " + entropies.stream().mapToDouble(item -> item).sum());
        System.out.println("Purity: " + purities.stream().mapToDouble(item -> item).sum());
    }

    static void positionCalcStats(ArrayList<Cluster> positions) {
       ArrayList<Double> entropies = new ArrayList<>(), purities = new ArrayList<>();
       System.out.println("Position stats");
       for (Cluster c: positions) {
          System.out.println("Cluster info");
          ArrayList<Player> cur = c.getPlayers();
          HashMap<Integer, Integer> positionCount = new HashMap<>();
          cur.stream().mapToInt(player -> player.getPosition()).forEach(pos -> {
             int count = 1;
             if (positionCount.containsKey(pos)) {
                count = positionCount.get(pos) + 1;
             }
             positionCount.put(pos, count);
          });
          positionCount.entrySet().stream().forEach(ent -> System.out.println("Position: " + ent.getKey() + " Count: " + ent.getValue()));

          double[] rawEnts = positionCount.entrySet().stream().mapToDouble(ent ->
                  -1.0 * ent.getValue() / cur.size() *
                          Math.log(1.0 * ent.getValue() / cur.size()) / Math.log(2)).toArray();
          System.out.println("ent: " + Arrays.stream(rawEnts).sum());
          entropies.add(Arrays.stream(rawEnts).sum() * cur.size() / playerData.size());

          double max = positionCount.values().stream().mapToInt(ent->ent).max().getAsInt();
          System.out.println("pur: " + max / cur.size());
          purities.add(max / playerData.size());
       }

       System.out.println("Entropy: " + entropies.stream().mapToDouble(item -> item).sum());
       System.out.println("Purity: " + purities.stream().mapToDouble(item -> item).sum());
    }

    static ArrayList<Player> readInput(String path) {
        ArrayList<Player> retVal = new ArrayList<Player>();

        try (Stream<String> lines = Files.lines(Paths.get(path), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                Player temp = new Player(line);
                retVal.add(temp);
            });
        }
        catch (Exception e) {
            System.err.println(e);
        }

        return retVal;
    }
    public void toCSV(ArrayList<Cluster> clusters,String fileName){
      try{
         PrintWriter writer = new PrintWriter(fileName, "UTF-8");
         writer.println("Name,Seasons,Games,AB,Runs,Hits,Doubles,Triples,HR,RBI,BB,SO,BA,OBP,SLG,AdjPro,BatRun,AdjBatRun,RC,SB,CS,SBRuns,FieldAverage,FieldRuns,TotalPyrRate,Position,HOFStatus,Cluster");
         for (int i=0;i<clusters.size(); i++){
            Cluster current=clusters.get(i);
            ArrayList<Player> players=current.getPlayers();
            for (int j=0; j<players.size(); j++){
               writer.println(players.get(j).toString()+", "+i);
            }
         }
         writer.close();
      }
      catch(Exception e){
         System.out.println("Error connecting");
      }
   }
}
