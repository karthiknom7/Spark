package com.spark.examples;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class FlatMapToPairs {

	public static void main(String[] args) {

		SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("flat-map-pairs-app");
		JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
		
		List<String> stringsList = Arrays.asList("Hello java", "Hello spark");
		
		JavaRDD<String> stringRdd = sparkContext.parallelize(stringsList);
		
		JavaPairRDD<String, Integer> javaPairRDD = stringRdd.flatMapToPair(e -> Arrays.asList(e.split(" ")).stream().map(token -> new Tuple2<>(token, token.length())).collect(Collectors.toList()).iterator());
		System.out.println(javaPairRDD.collect());
		// Gorup by
		JavaPairRDD<String, Iterable<Integer>> groupByRdd = javaPairRDD.groupByKey();
		System.out.println(groupByRdd.collect());
		
	}

}
