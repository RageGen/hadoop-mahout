package com.example; 
 
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.fs.FileSystem; 
import org.apache.hadoop.fs.Path; 
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.SequenceFile; 
import org.apache.hadoop.io.Text; 
import org.apache.mahout.clustering.Cluster; 
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver; 
import org.apache.mahout.common.distance.ManhattanDistanceMeasure; 
import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure; 
import org.apache.mahout.math.DenseVector; 
import org.apache.mahout.math.VectorWritable; 
import org.apache.mahout.clustering.WeightedVectorWritable; 
import org.apache.mahout.common.distance.DistanceMeasure; 
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator; 
 
import java.io.*; 
import java.util.ArrayList; 
import java.util.Set; 
import java.util.HashSet; 
 
public class FuzzyKMeans { 
 
    public static void convertToSequenceFile(String inputFile, String sequenceFile) throws IOException { 
        Configuration conf = new Configuration(); 
        FileSystem fs = FileSystem.get(conf); 
        Path path = new Path(sequenceFile); 
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, IntWritable.class, VectorWritable.class); 
 
 
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) { 
            String line; 
            int key = 0; 
            while ((line = br.readLine()) != null) { 
                String[] coordinates = line.split(","); 
                double x = Double.parseDouble(coordinates[0].trim()); 
                double y = Double.parseDouble(coordinates[1].trim()); 
                DenseVector vector = new DenseVector(new double[]{x, y}); 
                writer.append(new IntWritable(key++), new VectorWritable(vector)); 
            } 
        } 
        writer.close(); 
    } 
 
    public static void runFuzzyKMeans(String inputPath, String outputPath, DistanceMeasure distanceMeasure) throws Exception { 
        Configuration conf = new Configuration(); 
        Path input = new Path(inputPath); 
        Path output = new Path(outputPath); 
        Path clusters = new Path(outputPath, "clusters"); 
 
        RandomSeedGenerator.buildRandom(conf, input, clusters, 5, distanceMeasure); 
 
        FuzzyKMeansDriver.run( 
                input,       
                clusters,            
                output,      
                distanceMeasure,   
                0.01,      
                10,                     
                1.5f,    
                true,                    
                true,                 
                0.01,                   
                false                   
        ); 
    } 
 
    public static void main(String[] args) { 
        try { 
            Configuration conf = new Configuration(); 
            FileSystem fs = FileSystem.get(conf); 
            String inputFile = "/lab6/points.csv"; 
            String sequenceFile = "points.seq"; 
 
            convertToSequenceFile(inputFile, sequenceFile); 
 
            String outputDirSquared = "output_clusters_squared"; 
 
            runFuzzyKMeans(sequenceFile, outputDirSquared, new SquaredEuclideanDistanceMeasure()); 
 
            String outputDirManhattan = "output_clusters_manhattan"; 
            runFuzzyKMeans(sequenceFile, outputDirManhattan, new ManhattanDistanceMeasure()); 
 
            SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path("output_clusters_squared/" + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000"), conf); 
            IntWritable key = new IntWritable(); 
            WeightedVectorWritable value = new WeightedVectorWritable(); 
            File output_squared = new File("output_squared.csv"); 
            FileWriter writer = new FileWriter(output_squared); 
            writer.write("cluster,weight,x,y\n"); 
            while (reader.next(key, value)) { 
                System.out.println(value.toString() + " belongs to cluster " + key.toString()); 
                writer.write(String.join(",", key.toString(), "" + value.getWeight(), "" + value.getVector().get(0), "" + value.getVector().get(1)) + "\n"); 
            } 
            writer.close(); 
            reader.close(); 
 
            SequenceFile.Reader reader1 = new SequenceFile.Reader(fs, new Path("output_clusters_manhattan/" + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000"), conf); 
            File output_manhattan = new File("output_manhattan.csv"); 
            FileWriter writer1 = new FileWriter(output_manhattan); 
            writer1.write("cluster,weight,x,y\n"); 
            while (reader1.next(key, value)) { 
                System.out.println(value.toString() + " belongs to cluster " + key.toString()); 
                writer1.write(String.join(",", key.toString(), "" + value.getWeight(), "" + value.getVector().get(0), "" + value.getVector().get(1)) + "\n"); 
            } 
            writer1.close(); 
            reader1.close(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
}