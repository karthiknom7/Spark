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

import com.spark.streaming.model.FlightDetails;
import com.spark.streaming.serdes.FlightDetailsDeserializer;

import scala.Tuple2;

public class SparkStreamsKafkaStateFullExample {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("Kafka-statefull-example");
		JavaSparkContext sparkContext = new JavaSparkContext(conf);

		JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(30));
		//check point
		streamingContext.checkpoint("D:\\logs\\sparkcheckpoint");

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

		JavaPairDStream<String, FlightDetails> flightDetailsPairStream = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
		flightDetailsPairStream.print();

		Function3<String, Optional<FlightDetails>, State<List<FlightDetails>>, Tuple2<String, Double>> mappingFunc = (
				flightId, curFlightDetail, state) -> {
			// fetch existing state else create new list.
			List<FlightDetails> details = state.exists() ? state.get() : new ArrayList<>();
			boolean isLanded = false;
			/*
			 * if optionsla object is present, add it in the list and check set
			 * the boolean flag 'islanded' to true if flight has landed
			 */
			if (curFlightDetail.isPresent()) {
				details.add(curFlightDetail.get());
				if (curFlightDetail.get().isLanded()) {
					isLanded = true;
				}
			}
			// Calculate average value of the all temperature values
			Double avgSpeed = details.stream().mapToDouble(f -> f.getTemperature()).average().orElse(0.0);

			/*
			 * Remove the state if flight has landed else update the state with
			 * the updated list of FlightDetails objects
			 */
			if (isLanded) {
				state.remove();
			} else {
				state.update(details);
			}
			// Return the current average
			return new Tuple2<String, Double>(flightId, avgSpeed);
		};
		
		JavaMapWithStateDStream<String, FlightDetails, List<FlightDetails>, Tuple2<String, Double>> mapWithState = flightDetailsPairStream.mapWithState(StateSpec.function(mappingFunc)); 
		 
		mapWithState.print(); 

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
