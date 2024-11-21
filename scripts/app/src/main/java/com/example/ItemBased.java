package com.example; 
import org.apache.mahout.cf.taste.common.TasteException; 
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator; 
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel; 
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender; 
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity; 
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity; 
import org.apache.mahout.cf.taste.model.DataModel; 
import org.apache.mahout.cf.taste.recommender.Recommender; 
import org.apache.mahout.cf.taste.similarity.ItemSimilarity; 
import org.apache.mahout.cf.taste.recommender.RecommendedItem; 
 
import java.io.File; 
import java.io.IOException; 
import java.util.List; 
 
public class ItemBased { 
 
    public static void main(String[] args) throws IOException, TasteException { 
 
        DataModel model = new FileDataModel(new File("/scripts/app/ratings.csv")); 
 
        ItemSimilarity similarityPearson = new PearsonCorrelationSimilarity(model); 
        ItemSimilarity similarityCosine = new UncenteredCosineSimilarity(model); 
 
        Recommender recommenderPearson = new GenericItemBasedRecommender(model, similarityPearson); 

 
        Recommender recommenderCosine = new GenericItemBasedRecommender(model, similarityCosine); 
 
        LongPrimitiveIterator users = model.getUserIDs(); 
 
        while (users.hasNext()) { 
            long userId = users.nextLong(); 
            List<RecommendedItem> recommendationsPearson = recommenderPearson.recommend(userId, 5); 
            List<RecommendedItem> recommendationsCosine = recommenderCosine.recommend(userId, 5); 
 
            System.out.println("User ID: " + userId); 
            System.out.println("Recommendations with Pearson:"); 
            for (RecommendedItem recommendation : recommendationsPearson) { 
                System.out.println(recommendation); 
            } 
 
            System.out.println("Recommendations with Cosine:"); 
            for (RecommendedItem recommendation : recommendationsCosine) { 
                System.out.println(recommendation); 
            } 
        } 
    } 
} 