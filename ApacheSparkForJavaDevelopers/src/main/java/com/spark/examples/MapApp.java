package com.spark.examples;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class MapApp {

	public static void main(String[] args) {

		SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("MapExample");
		JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
		List<Integer> intsList = Arrays.asList(8,6,4,5,3,2);
		
		JavaRDD<Integer> javaRDD = sparkContext.parallelize(intsList,2);
		JavaRDD<Integer> javaRDD2 = javaRDD.map(element-> element + 1);
		javaRDD2.foreach(System.out::println);
		javaRDD.filter(ele -> (ele % 2 != 0)).foreach(System.out::println);
	}

}
