package com.example; 
import java.io.File; 
import java.util.Set; 
import java.util.HashSet; 
import java.util.Map; 
import java.util.HashMap; 
import java.util.ArrayList; 
import java.lang.Runtime; 
import java.lang.Process; 
import java.lang.StringBuilder; 
import java.lang.Process; 
import java.io.BufferedReader; 
import java.io.InputStreamReader; 
import java.io.FileWriter; 
import java.io.IOException; 
import org.apache.mahout.clustering.Cluster; 
import org.apache.hadoop.util.ToolRunner; 
import org.apache.hadoop.fs.Path; 
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.SequenceFile; 
import org.apache.mahout.math.VectorWritable; 
import org.apache.mahout.clustering.WeightedVectorWritable; 
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.fs.FileSystem; 
 
public class Clustering { 
     
    public static void main(String[] args) { 
        try { 
            String s; 
            Configuration conf = new Configuration(); 
            FileSystem fs = FileSystem.get(conf); 
            Process p = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c",  
                "mahout seqdirectory " 
                + "-i /lab6/app/reuters-sgm " 
                + "-o /lab6/app/reuters-seqdir "  
                + "--charset UTF-8 -chunk 64 ; " 
                + "mahout seq2sparse "  
                + "-i /lab6/app/reuters-seqdir "  
                + "-o /lab6/app/reuters-sparse "  
                + "--maxDFPercent 85 --namedVector ; " 
                + "mahout canopy " 
                + "-i /lab6/app/reuters-sparse/tf-vectors " 
                + "-o /lab6/app/reuters-clusters " 
                + "-dm org.apache.mahout.common.distance.EuclideanDistanceMeasure" 
                + "-t1 1500 -t2 2000 "}); 
            BufferedReader br = new BufferedReader( new InputStreamReader(p.getInputStream())); 
 
            while ((s = br.readLine()) != null) 
                System.out.println(s); 
 
            p.waitFor(); 
            p.destroy(); 
             
            Process p2 = Runtime.getRuntime().exec(new String[]{"/bin/bash", "c",  
                "mahout kmeans " 
                + "-i /lab6/app/reuters-sparse/tfidf-vectors/ " 
                + "-c /lab6/app/reuters-clusters " 
                + "-o /lab6/app/reuters-kmeans " 
                + "-dm org.apache.mahout.common.distance.EuclideanDistanceMeasure " 
                + "-x 10 -k 10 -ow --clustering -cl -cd 0.1 ; " 
                + "mahout clusterdump " 
                + "-s /lab6/app/reuters-kmeans/clusters-1 " 
                + "-o /lab6/app/reuters-clusterdump "  
                + "-d /lab6/app/reuters-sparse/dictionary.file-0 " 
                + "-dt sequencefile -b 100 -n 20 " 
                + "-p /lab6/app/reuters-kmeans/clusteredPoints "}); 
 
            BufferedReader br1 = new BufferedReader( new InputStreamReader(p2.getInputStream())); 
 
            while ((s = br1.readLine()) != null) 
                System.out.println(s); 
 
            p2.waitFor(); 
            p2.destroy(); 
 
            SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path("/lab6/app/reuters-kmeans/" + Cluster.CLUSTERED_POINTS_DIR + "/partm-00000"), conf); 
            IntWritable key = new IntWritable(); 
            WeightedVectorWritable value = new WeightedVectorWritable(); 
            Map<String, ArrayList<String>> map = new HashMap<>(); 
            while (reader.next(key, value)) { 
                if (!map.containsKey(key.toString())){ 
                    map.put(key.toString(), new ArrayList<>()); 
                } 
                map.get(key.toString()).add(value.toString().split("\\=")[0].split("\\/")[1]); 
            } 
            for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()){ 
                System.out.println("cluster " + entry.getKey() + " has " + entry.getValue().size() + " files: " + String.join(", ", entry.getValue())); 
            } 
            reader.close(); 
 
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }  
    } 
} 