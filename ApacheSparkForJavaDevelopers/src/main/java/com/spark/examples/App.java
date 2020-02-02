package com.spark.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("WordCount");
		JavaSparkContext javaSparkContext = new JavaSparkContext(conf);
		JavaRDD<String> inputData = javaSparkContext.parallelize(Arrays.asList("where there is  a will there is a way"));

		JavaPairRDD<String, Integer> flattenPairs = inputData
				.flatMapToPair(new PairFlatMapFunction<String, String, Integer>() {
					@Override
					public Iterator<Tuple2<String, Integer>> call(String text) throws Exception {
						List<Tuple2<String, Integer>> tupleList = new ArrayList<>();
						String[] textArray = text.split(" ");
						for (String word : textArray) {
							tupleList.add(new Tuple2<String, Integer>(word, 1));
						}
						return tupleList.iterator();
					}
				});
		JavaPairRDD<String, Integer> wordCountRDD = flattenPairs
				.reduceByKey(new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
				});
		wordCountRDD.foreach(System.out::println);
	}

}
