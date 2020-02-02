package com.spark.streaming.serdes;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spark.streaming.model.FlightDetails;

public class FlightDetailsDeserializer implements Deserializer<FlightDetails>{

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(Map<String, ?> arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FlightDetails deserialize(String arg0, byte[] bytes) {
		 if (bytes == null)
	            return null;

		 FlightDetails data;
	        try {
	            data = objectMapper.readValue(bytes, FlightDetails.class);
	        } catch (Exception e) {
	            throw new SerializationException(e);
	        }

	        return data;
	}

}
