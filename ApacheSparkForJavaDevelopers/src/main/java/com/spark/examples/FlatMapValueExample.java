package com.spark.examples;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class FlatMapValueExample {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setMaster("local").setAppName("Flat-map-values");
		JavaSparkContext context = new JavaSparkContext(conf);

		JavaPairRDD<String, String> monExpRDD = context
				.parallelizePairs(Arrays.asList(new Tuple2<String, String>("Jan", "50,100,214,10"),
						new Tuple2<String, String>("Feb", "60,314,223,77")));

		
		
		JavaPairRDD<String, Integer> flatMapVaue = monExpRDD.flatMapValues(t -> Arrays.asList(t.split(",")).stream()
				.map(ele -> Integer.parseInt(ele)).collect(Collectors.toList()));
		
		System.out.println(flatMapVaue.collect());

	}

}
