package com.springdeveloper.demo;

import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.hadoop.fs.FsShell;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Controller
public class HdfsApp {

	@Autowired
	FsShell fsShell;

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

}
