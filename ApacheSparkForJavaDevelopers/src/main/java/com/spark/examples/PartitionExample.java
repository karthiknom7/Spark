package com.spark.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.Partitioner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class PartitionExample {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setAppName("partition-example").setMaster("local");
		JavaSparkContext context = new JavaSparkContext(conf);
		
		JavaPairRDD<String, String> pairRDD = context.parallelizePairs(Arrays.asList(new Tuple2<>("Aaaaa", "XYZ"), new Tuple2<>("Awe", "XYZ"), new Tuple2<>("Bolll", "1234"),new Tuple2<>("cat", "ab"), new Tuple2<>("doll", "allout")), 3);
		
		JavaPairRDD<String, String> customePartitioned = pairRDD.partitionBy(new CustomerPartitioner());
		
		JavaRDD<String> mapPartitionsWithIndex = customePartitioned.mapPartitionsWithIndex((index, tupleIterator)->{
			List<String> allEle = new ArrayList<String>();
			while(tupleIterator.hasNext()){ allEle.add("Partition number:"+index+",key:"+tupleIterator.next()._1());}
		return allEle.iterator();}, true);
		
		System.out.println(mapPartitionsWithIndex.collect());
	}
	
	public static class CustomerPartitioner extends Partitioner{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int maxPartitions = 2;
		@Override
		public int getPartition(Object key) {
			return ((String) key).length() % maxPartitions;
		}

		@Override
		public int numPartitions() {
			return maxPartitions;
		}
		
	}

}
