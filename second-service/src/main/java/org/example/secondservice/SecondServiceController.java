package org.example.secondservice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/second-service")
public class SecondServiceController {

	@GetMapping("/welcome")
	public String welcome() {
		return "welcome to the second service!!";
	}

	@GetMapping("/message")
	public String message(@RequestHeader("second-request") String header) {
		log.info("header = {}", header);
		return "Hello world in second service";
	}

	@GetMapping("/check")
	public String check(HttpServletRequest request) {
		log.info("Server port = " + request.getServerPort());
		return "Hi, there. This is a message from Second Service On Port " + request.getServerPort();
	}
}
