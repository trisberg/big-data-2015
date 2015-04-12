package com.springdeveloper.demo;

import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.hadoop.fs.FsShell;

@ComponentScan
@EnableAutoConfiguration
public class Application implements CommandLineRunner {

    @Autowired
    FsShell fsShell;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("*** HDFS content:");
        for (FileStatus fs : fsShell.ls("/")) {
            System.out.println(fs.getOwner() +
                    " " +  fs.getGroup() +
                    ": /" + fs.getPath().getName());
        }
    }

}
