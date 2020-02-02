package com.spark.examples;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class MapToPairApp {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setMaster("local").setAppName("map-pair-app");

		JavaSparkContext sparkContext = new JavaSparkContext(conf);

		List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5, 7, 9, 80, 6);

		JavaRDD<Integer> intRdd = sparkContext.parallelize(intList);

		JavaPairRDD<String, Integer> mapToPairRdd = intRdd
				.mapToPair(e -> e % 2 == 0 ? new Tuple2<>("Even", e) : new Tuple2<>("Odd", e));
		System.out.println(mapToPairRdd.collect());
		// Gorup by
		JavaPairRDD<String, Iterable<Integer>> groupByRdd = mapToPairRdd.groupByKey();
		System.out.println(groupByRdd.collect());
		// reduce example

		JavaPairRDD<String, Integer> reduceByRdd = mapToPairRdd.reduceByKey((v1,v2) -> v1 +v2);
		System.out.println(reduceByRdd.collect());
		
		JavaPairRDD<Integer, String> sortedbyKeyRdd = mapToPairRdd.mapToPair(tuple -> new Tuple2(tuple._2,tuple._1));
		System.out.println(sortedbyKeyRdd.collect());
	}

}
