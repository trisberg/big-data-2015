package com.springdeveloper.hadoop.batch.hive;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.Assert;

public class HiveDataMapper implements LineMapper<Map<String, Object>> {

	private String tweetDate;

	public void setTweetDate(String tweetDate) {
		this.tweetDate = tweetDate;
	}

	public Map<String, Object> mapLine(String line, int lineNum) throws Exception {
		Assert.notNull(line, "Expecting line not to be null");
		String[] tokens = line.split("\u0001");
		if (tokens.length != 2) {
			throw new DataIntegrityViolationException("Expecting 2 tokens in input line: " + line);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("user_name", tokens[0]);
		data.put("followers", tokens[1]);
		data.put("tweet_date", tweetDate);
		return data;
	}

}
