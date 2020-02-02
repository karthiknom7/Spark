package com.spark.streaming.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.spark.streaming.model.FlightDetails;
import com.spark.streaming.serdes.FlightDetailsDeserializer;

import scala.Tuple2;

public class WindowExample {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("Kafka-statefull-example");
		JavaSparkContext sparkContext = new JavaSparkContext(conf);

		JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(20));
		// check point
		// streamingContext.checkpoint("D:\\logs\\sparkcheckpoint");

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("bootstrap.servers", "localhost:9092");
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", FlightDetailsDeserializer.class);
		kafkaParams.put("group.id", "use_a_separate_group_id_for_each_strea");
		kafkaParams.put("auto.offset.reset", "latest");

		List<String> topics = Arrays.asList("flightTest");

		final JavaInputDStream<ConsumerRecord<String, FlightDetails>> stream = KafkaUtils.createDirectStream(
				streamingContext, LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, FlightDetails> Subscribe(topics, kafkaParams));

		JavaDStream<FlightDetails> flightDetailsStream = stream.map(record -> record.value());
		flightDetailsStream.print();

		JavaDStream<FlightDetails> windowedStream = flightDetailsStream.window(Durations.minutes(4),
				Durations.minutes(2));

		JavaPairDStream<String, Double> transformWindow = windowedStream
				.mapToPair(e -> new Tuple2<String, Double>(e.getFlightId(), e.getTemperature()))
				.mapValues(v -> new Tuple2<Double, Integer>(v, 1))
				.reduceByKey((t1, t2) -> new Tuple2<>(t1._1() + t2._1(), t1._2() + t2._2()))
				.mapValues(t -> t._1 / t._2);
		transformWindow.cache();

		transformWindow.print();

		streamingContext.start();
		System.out.println("Streaming started.....");

		try {
			streamingContext.awaitTermination();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
