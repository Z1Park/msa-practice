package org.example.firstservice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/first-service")
public class FirstServiceController {

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to the first service!!";
	}

	@GetMapping("/message")
	public String message(@RequestHeader("first-request") String header) {
		log.info("header = {}", header);
		return "Hello world in first service";
	}

	@GetMapping("/check")
	public String check(HttpServletRequest request) {
		log.info("Server port = " + request.getServerPort());
		return "Hi, there. This is a message from First Service On Port " + request.getServerPort();
	}
}