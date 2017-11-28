import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
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
        iterateClustering(100, hallOfFame);
        hallOfFameCalcStats(hallOfFame);


        // Clustering on positions
        seeds = new Random().ints(0, playerData.size()).distinct().limit(7).toArray();
        ArrayList<Cluster> positions = new ArrayList<>();
        for (int i = 0; i < seeds.length; i++) {
            positions.add(new Cluster(playerData.get(seeds[i])));
        }
        iterateClustering(1000, positions);


        playerData.stream().map(player -> player.getPosition()).forEach(System.out::println);
    }

    static void iterateClustering(int iteration, ArrayList<Cluster> clusters) {
        for (int i = 0; i < iteration; i++) {
            clusters.stream().forEach(cluster -> cluster.resetPoints());
            for (Player p: playerData) {
                double [] distances = clusters.stream().mapToDouble(cluster -> L2Distance(cluster, p)).toArray();
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

    static void hallOfFameCalcStats(ArrayList<Cluster> hof) {
        ArrayList<Double> entropies = new ArrayList<>(), purities = new ArrayList<>();

        for (Cluster c : hof) {
            ArrayList<Player> cur = c.getPlayers();
            double numHof = cur.stream().mapToInt(player -> player.getHof()).sum() * 1.0;
            double numOther = cur.size() - numHof;
            double a = -numHof/cur.size() * Math.log(numHof/cur.size()) / Math.log(2);
            double b = -numOther/cur.size() * Math.log(numOther / cur.size()) / Math.log(2);
            entropies.add((a + b) * cur.size() / playerData.size());

            double max = numHof > numOther ? numHof : numOther;
            purities.add(max / playerData.size());
        }

        System.out.println("Hall of Fame Stats");
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
}
