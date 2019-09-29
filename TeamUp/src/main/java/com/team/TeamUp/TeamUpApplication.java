package com.team.TeamUp;

import com.team.TeamUp.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TeamUpApplication {

	@Autowired
	UserUtils userUtils;

	public static void main(String[] args) {
		SpringApplication.run(TeamUpApplication.class, args);
		System.out.println("Server Started");
	}

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			userUtils.createAdminIfNoneExistent();
		};
	}
}
