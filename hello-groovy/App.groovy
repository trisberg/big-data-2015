@Grab('org.springframework.data:spring-data-hadoop-boot:2.1.2.RELEASE')

import org.apache.hadoop.fs.FileStatus
import org.springframework.data.hadoop.fs.FsShell

public class App implements CommandLineRunner {

	@Autowired FsShell fsShell

	void run(String... args) {
		println("Hello!")
		for (FileStatus fs : fsShell.ls("/")) {
			println("> ${fs.path.name}")
		}
	}

}