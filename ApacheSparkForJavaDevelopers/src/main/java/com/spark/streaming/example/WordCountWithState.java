package com.spark.streaming.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaMapWithStateDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.spark.streaming.serdes.FlightDetailsDeserializer;

import scala.Tuple2;

public class WordCountWithState {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("wordCount-with-state");

		JavaSparkContext sparkContext = new JavaSparkContext(conf);
		JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(30));

		// check point
		streamingContext.checkpoint("D:\\logs\\sparkcheckpoint");

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("bootstrap.servers", "localhost:9092");
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", "use_a_separate_group_id_for_each_strea");
		kafkaParams.put("auto.offset.reset", "latest");

		List<String> topics = Arrays.asList("sparkTest");

		JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(streamingContext,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String> Subscribe(topics, kafkaParams));
		
		JavaDStream<String> dStream = stream.map(record -> record.value());
		JavaDStream<String> flatStream = dStream.flatMap(str-> Arrays.asList(str.split(" ")).iterator());
		JavaPairDStream<String, Integer> javaPairDStream = flatStream.mapToPair(str -> new Tuple2(str, 1));
		
		Function3<String, Optional<Integer>,  State<List<String>>, Tuple2<String, Integer>> mapperFun = (word, intValue , state ) ->{
			List<String> list = state.exists() ? state.get(): new ArrayList<>();
			list.add(word);
			state.update(list);
			return new Tuple2<String, Integer>(word, list.size());
		};
		
		JavaMapWithStateDStream<String, Integer, List<String>, Tuple2<String, Integer>> stateMappedStream = javaPairDStream.mapWithState(StateSpec.function(mapperFun));
		stateMappedStream.print();
		
		streamingContext.start();
		System.out.println("Started......");
		try {
			streamingContext.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
