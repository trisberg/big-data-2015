package com.springdeveloper.demo

@Grab("org.thymeleaf:thymeleaf-spring4:2.1.4.RELEASE")
@Grab("org.webjars:jquery:2.0.3-1")
@Grab('org.springframework.data:spring-data-hadoop-boot:2.1.2.RELEASE')
@GrabExclude('javax.servlet:servlet-api')

import org.apache.hadoop.fs.FileStatus
import org.springframework.data.hadoop.fs.FsShell

@Controller
public class HdfsApp {

	@Autowired FsShell fsShell

    @RequestMapping("/")
    @ResponseBody
    String home() {
        "Hello Hadoop!"
    }

    @RequestMapping("/env")
	public String env(Model model) {
		def envVars = new ArrayList()
		Map<String, String> env = System.getenv()
		for (Map.Entry<String, String> entry : env.entrySet()) {
			envVars.add(entry.key + " = " + entry.value)
		}
		model.addAttribute("envvars", envVars)
		return "env"
	}

    @RequestMapping("/fspath")
	public String fspath(@RequestParam(value="name", required=false, defaultValue="/") String name, Model model) {
		def dirs = new ArrayList()
		def files = new ArrayList()
		for (FileStatus fs : fsShell.ls(name)) {
			if (name != fs.path.toUri().rawPath) {
				if (fs.isDirectory()) {
					dirs.add(fs.path.name)	
				} else {
					files.add(fs.path.name)
				}
			}
		}
		model.addAttribute("name", name)
		model.addAttribute("base", name == "/" ? "" : name)
		model.addAttribute("dirs", dirs)
		model.addAttribute("files", files)
		return "fspath"
	}

}
