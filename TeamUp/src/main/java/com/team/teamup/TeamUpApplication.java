package com.team.teamup;

import com.team.teamup.utils.ImageCompressor;
import com.team.teamup.utils.StartUpManager;
import com.team.teamup.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
@Slf4j
public class TeamUpApplication {

	@Autowired
	private StartUpManager startUpManager;

	public static void main(String[] args) {
		SpringApplication.run(TeamUpApplication.class, args);
		log.info("---> Server Started");
		// in case of failing: change database
		// remove logo from email
		// check download links
	}

	@Bean
	public CommandLineRunner demo() {
		return args -> {
			startUpManager.createAdmin();
			startUpManager.createFiles();
			startUpManager.createDefaultTaskStatuses();
		};
	}

}
