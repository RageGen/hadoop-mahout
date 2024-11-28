package com.example;

import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;

import java.io.File;

public class Evaluate {

    public static void main(String[] args) throws Exception {

        DataModel model = new FileDataModel(new File("/lab5/app/ratings.csv"));

        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder recommenderBuilder = model1 -> {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model1);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model1);
            return new GenericUserBasedRecommender(model1, neighborhood, similarity);
        };

        double resultUserPearson = evaluator.evaluate(recommenderBuilder, null, model, 0.7, 1.0);

        recommenderBuilder = model12 -> {
            UserSimilarity similarity = new UncenteredCosineSimilarity(model12);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model12);
            return new GenericUserBasedRecommender(model12, neighborhood, similarity);
        };

        double resultUserCosine = evaluator.evaluate(recommenderBuilder, null, model, 0.7, 1.0);

        RecommenderEvaluator evaluator1 = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder recommenderBuilder1 = model2 -> {
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model2);
            return new GenericItemBasedRecommender(model2, similarity);
        };

        double resultItemPearson = evaluator1.evaluate(recommenderBuilder1, null, model, 0.7, 1.0);

        recommenderBuilder1 = model22 -> {
            ItemSimilarity similarity = new UncenteredCosineSimilarity(model22);
            return new GenericItemBasedRecommender(model22, similarity);
        };
        double resultItemCosine = evaluator1.evaluate(recommenderBuilder1, null, model, 0.7, 1.0);
        System.out.println("UserBased Pearson evaluator result: " + resultUserPearson);
        System.out.println("UserBased Cosine evaluator result: " + resultUserCosine);
        System.out.println("ItemBased Pearson evaluator result: " + resultItemPearson);
        System.out.println("ItemBased Cosine evaluator result: " + resultItemCosine);
    }
}