package com.spark.examples2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class AggegateExample {

	public static void main(String[] args) {

		SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("MapExample");
		JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
		List<Integer> intsList = Arrays.asList(8, 6, 4, 5, 3, 2);

		JavaRDD<Integer> javaRDD = sparkContext.parallelize(intsList, 2);
		List<Integer> aggregatedList = javaRDD.aggregate(new ArrayList<Integer>(), (list, ele) -> {
			list.add(ele);
			return list;
		}, (l1, l2) -> {
			l1.addAll(l2);
			return l1;
		});
		System.out.println(aggregatedList);
	}

}
