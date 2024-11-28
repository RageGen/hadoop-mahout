package com.example; 
import org.apache.mahout.cf.taste.common.TasteException; 
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator; 
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel; 
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood; 
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender; 
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity; 
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity; 
import org.apache.mahout.cf.taste.model.DataModel; 
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood; 
import org.apache.mahout.cf.taste.recommender.Recommender; 
import org.apache.mahout.cf.taste.similarity.UserSimilarity; 
import org.apache.mahout.cf.taste.recommender.RecommendedItem; 
import java.io.File; 
import java.io.IOException; 
import java.util.List; 
public class UserBased { 
    public static void main(String[] args) throws IOException, TasteException { 
 
        DataModel model = new FileDataModel(new File("/lab5/app/ratings.csv")); 
 
        UserSimilarity similarityPearson = new PearsonCorrelationSimilarity(model); 
        UserSimilarity similarityCosine = new UncenteredCosineSimilarity(model); 
 
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarityPearson, model); 
 
        Recommender recommenderPearson = new GenericUserBasedRecommender(model, neighborhood, similarityPearson); 
        Recommender recommenderCosine = new GenericUserBasedRecommender(model, neighborhood, similarityCosine); 
 
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
