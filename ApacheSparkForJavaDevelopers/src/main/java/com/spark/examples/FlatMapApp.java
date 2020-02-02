package com.spark.examples;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class FlatMapApp {

	public static void main(String[] args) {

		SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("flat-map-app");
		JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
		
		List<String> stringsList = Arrays.asList("Hello java", "Hello spark");
		
		JavaRDD<String> stringRdd = sparkContext.parallelize(stringsList);
		//stringRdd.foreach(System.out::println);
		System.out.println(stringRdd.collect());
		JavaRDD<String> stringRdd1 = stringRdd.flatMap(str -> Arrays.asList(str.split(" ")).iterator());
		System.out.println(stringRdd1.collect());
		//stringRdd.foreach(System.out::println);
	}

}
