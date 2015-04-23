package com.springdeveloper.demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.hadoop.fs.FsShell;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Controller
public class HdfsApp implements EnvironmentAware {

	@Value("${mapreduce.input}")
	private String inputDir;

	@Value("${mapreduce.output}")
	private String outputDir;

	@Autowired
	private Configuration configuration;

	@Autowired
	private FsShell fsShell;

	@Autowired
	private Job job;

	private String profile;

	public static void main(String[] args) {
		SpringApplication.run(HdfsApp.class, args);
	}

	@RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello Hadoop!";
    }

    @RequestMapping("/env")
	public String env(Model model) {
	    model.addAttribute("profile", profile);
		List envVars = new ArrayList();
		Map<String, String> env = System.getenv();
		for (Map.Entry<String, String> entry : env.entrySet()) {
			envVars.add(entry.getKey() + " = " + entry.getValue());
		}
		model.addAttribute("envvars", envVars);
		return "env";
	}

    @RequestMapping("/ip")
    @ResponseBody
	public String ip() throws UnknownHostException {
		return Inet4Address.getLocalHost().getHostAddress() + " : " + Inet4Address.getLocalHost().getHostName();
	}

    @RequestMapping("/fspath")
	public String fspath(@RequestParam(value="name", required=false, defaultValue="/") String name, Model model) {
	    List dirs = new ArrayList();
	    List files = new ArrayList();
		for (FileStatus fs : fsShell.ls(name)) {
			if (!name.equals(fs.getPath().toUri().getRawPath())) {
				if (fs.isDirectory()) {
					dirs.add(fs.getPath().getName());
				} else {
					files.add(fs.getPath().getName());
				}
			}
		}
		model.addAttribute("name", name);
		model.addAttribute("base", name.equals("/") ? "" : name);
		model.addAttribute("dirs", dirs);
		model.addAttribute("files", files);
		return "fspath";
	}

	@RequestMapping("/wc")
	@ResponseBody
	public String wc() throws UnknownHostException {
		if (fsShell.test(inputDir)) {
			int count = 0;
			for (FileStatus fs : fsShell.ls(inputDir)) {
				count++;
			}
			if (count <= 1) {
				return "No input found - did you put a text file in HDFS in this directory: " + inputDir + "?";
			}
		}
		else {
			return "No input directory found - put a text file in HDFS in this directory: " + inputDir;
		}
		if (fsShell.test(outputDir)) {
			fsShell.rmr(outputDir);
		}
		try {
			boolean success = job.waitForCompletion(true);
			if (success) {
				StringBuilder results = new StringBuilder();
				results.append(job.getJobState().toString() + "<br>");
				if (fsShell.test(outputDir)) {
					for (FileStatus fs : fsShell.ls(outputDir)) {
						results.append(fs.getPath() + "<br>");
						if (fs.isFile() && fs.getLen() > 0) {
							FileSystem hdfs = FileSystem.get(configuration);
							BufferedReader br = new BufferedReader(new InputStreamReader(hdfs.open(fs.getPath())));
							String line;
							line = br.readLine();
							while (line != null) {
								results.append(line + "<br>");
								line = br.readLine();
							}
						}
					}
				}
				return results.toString();
			} else {
				return job.getJobState().toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public void setEnvironment(Environment environment) {
		this.profile = Arrays.asList(environment.getActiveProfiles()).toString();
	}
}
