package com.spark.examples2;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class WordCountFromFile {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setAppName("word count from file").setMaster("local");
		
		JavaSparkContext sparkContext = new JavaSparkContext(conf);
		
		JavaRDD<String> linesRDD = sparkContext.textFile("D:\\project documents\\text_wc.txt");
		/*JavaRDD<String> wordsRdd = linesRDD.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
		JavaPairRDD<String, Integer> pairRdd = wordsRdd.mapToPair(word -> new Tuple2<>(word, 1));*/
		
		JavaPairRDD<String, Integer> pairRdd = linesRDD.flatMapToPair(line -> Arrays.asList(line.split(" ")).stream().map(word -> new Tuple2<>(word, 1)).collect(Collectors.toList()).iterator());
		JavaPairRDD<String, Integer> countPairRdd = pairRdd.reduceByKey((e1, e2) -> e1 + e2);
		System.out.println(countPairRdd.collect());
		//countPairRdd.saveAsTextFile("D:\\project documents\\text_wc3.txt");
		System.out.println("Done");
		
	}

}
