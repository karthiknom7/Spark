package com.spark.examples;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;

import scala.Tuple2;

public class JoinApp {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setMaster("local").setAppName("join-test-app");
		
		JavaSparkContext sparkContext = new JavaSparkContext(conf);
		
		JavaPairRDD<String, String> javaPairRDD = sparkContext.parallelizePairs(Arrays.asList(new Tuple2<String,String>("A", "hello"), new Tuple2<String, String>("B", "hi"), new Tuple2<>("C", "karthik"), new Tuple2<>("D", "M")));
		JavaPairRDD<String, Integer> pairsRdd2 = sparkContext.parallelizePairs(Arrays.asList(new Tuple2<String, Integer>("A", 2), new Tuple2<String, Integer>("B", 7), new Tuple2<>("C", 1), new Tuple2<>("J", 77)));
		
		JavaPairRDD<String, Tuple2<String, Integer>> joinRdd = javaPairRDD.join(pairsRdd2);
		System.out.println(joinRdd.collect());
		
		JavaPairRDD<String, Tuple2<String, Optional<Integer>>> leftJoin = javaPairRDD.leftOuterJoin(pairsRdd2);
		
		System.out.println(leftJoin.collect());
		
		JavaPairRDD<String, Tuple2<Optional<String>, Integer>> rightOuter = javaPairRDD.rightOuterJoin(pairsRdd2);
		System.out.println(rightOuter.collect());
		
		JavaPairRDD<String, Tuple2<Optional<String>, Optional<Integer>>> fullOuter = javaPairRDD.fullOuterJoin(pairsRdd2);
		System.out.println(fullOuter.collect());
		
	}
}
