package com.abc.core.data.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import java.util.Date;

public class RedisSerializerUtil {

	public static enum StringSerializer implements RedisSerializer<String> {
		INSTANCE;

		@Override
		public byte[] serialize(String s) throws SerializationException {
			return (null != s ? s.getBytes() : new byte[0]);
		}

		@Override
		public String deserialize(byte[] bytes) throws SerializationException {
			if (bytes.length > 0) {
				return new String(bytes);
			} else {
				return null;
			}
		}
	}

	public static enum LongSerializer implements RedisSerializer<Long> {
		INSTANCE;

		@Override
		public byte[] serialize(Long aLong) throws SerializationException {
			if (null != aLong) {
				return aLong.toString().getBytes();
			} else {
				return new byte[0];
			}
		}

		@Override
		public Long deserialize(byte[] bytes) throws SerializationException {
			if (bytes.length > 0) {
				return Long.parseLong(new String(bytes));
			} else {
				return null;
			}
		}
	}

	public static enum IntSerializer implements RedisSerializer<Integer> {
		INSTANCE;

		@Override
		public byte[] serialize(Integer i) throws SerializationException {
			if (null != i) {
				return i.toString().getBytes();
			} else {
				return new byte[0];
			}
		}

		@Override
		public Integer deserialize(byte[] bytes) throws SerializationException {
			if (bytes.length > 0) {
				return Integer.parseInt(new String(bytes));
			} else {
				return null;
			}
		}
	}

	public static enum FloatSerializer implements RedisSerializer<Float>{
		INSTANCE;

		@Override
		public byte[] serialize(Float f) throws SerializationException {
			if (null != f) {
				return f.toString().getBytes();
			} else {
				return new byte[0];
			}
		}

		@Override
		public Float deserialize(byte[] bytes) throws SerializationException {
			if (bytes.length > 0) {
				return Float.parseFloat(new String(bytes));
			} else {
				return null;
			}
		}
		
	}
	
	public static enum DoubleSerializer implements RedisSerializer<Double>{
		INSTANCE;

		@Override
		public byte[] serialize(Double d) throws SerializationException {
			if (null != d) {
				return d.toString().getBytes();
			} else {
				return new byte[0];
			}
		}

		@Override
		public Double deserialize(byte[] bytes) throws SerializationException {
			if (bytes.length > 0) {
				return Double.parseDouble(new String(bytes));
			} else {
				return null;
			}
		}
		
	}
	
	public static enum DateSerializer implements RedisSerializer<Date>{
		INSTANCE;

		@Override
		public byte[] serialize(Date d) throws SerializationException {
			if (null != d) {
				return d.toLocaleString().getBytes();
			} else {
				return new byte[0];
			}
		}

		@Override
		public Date deserialize(byte[] bytes) throws SerializationException {
			if (bytes.length > 0) {
				return new Date(new String(bytes));
			} else {
				return null;
			}
		}
		
	}
}
