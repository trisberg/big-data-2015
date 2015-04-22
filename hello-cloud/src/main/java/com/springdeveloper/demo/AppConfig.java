package com.springdeveloper.demo;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.io.IOException;

@Configuration
public class AppConfig {

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	Job job(org.apache.hadoop.conf.Configuration configuration,
	        @Value("${mapreduce.jar}") String jarPath,
	        @Value("${mapreduce.output}") String outputPath,
	        @Value("${mapreduce.input}") String inputPath) throws IOException {
		Job job = new Job(configuration);
		job.setJar(jarPath);
		job.setMapperClass(org.apache.hadoop.examples.WordCount.TokenizerMapper.class);
		job.setReducerClass(org.apache.hadoop.examples.WordCount.IntSumReducer.class);
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		return job;
	}
}
