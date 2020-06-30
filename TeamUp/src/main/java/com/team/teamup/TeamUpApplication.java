package com.team.teamup;

import com.team.teamup.utils.ImageCompressor;
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
	private UserUtils userUtils;

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
			userUtils.createAdminIfNoneExistent();
			createFiles();
		};
	}

	private static void createFiles(){
		//create directory and download Avatar
		String home = System.getProperty("user.home");
		File file = new File(home + "/.TeamUpData");
		if (!file.exists()) {
			file.mkdirs();
		}
		ImageCompressor imageCompressor = new ImageCompressor();
		String avatarURI = "https://ucb1bf8cdb070b26be3f1a687d03.previews.dropboxusercontent.com/p/thumb/AA1W26q8Xh_TJE2TRvckuDgd7XurL3ZOrPCbLx4Gwq2BRdfpf31tjpGiV4t9BMGmvsAEiBNZZGXGGw3HKwT4F3g6k6NIQcbHwOXUXAS3sIQa2pjLSy0fUPdnzz0yAlC3iPMMhmGOMasJclfWHyPVfyJYeAfVaMHfrzYtP1qNkwguawqytSOVrrU-7kj7H1NdVRG5o5Ga215z7NouHLYT8z5mjqznJrsvD_igXf6EOJGza-hwtFA0yAtBmOIN8ORibNEoPjbC9R-kVUqurzmDNTJlBLA03D-caix7zTl4Pks18bgEEnYrOTGAm3zfSyYrejwRrwk8HeEyzRoZLFqbioknnhR8bNOxn6Q3idrj6sYBZO-XqpjeStEutgeGnGVtu9NJoEky_XtoSbu4A5wDOFbZ/p.png?fv_content=true&size_mode=5";
		String logoURI = "https://uc70693ab5639b8cd2be406547d5.dl.dropboxusercontent.com/cd/0/get/A1v5FjEX_TkLUkqu6ShXocv2QqSkEQ8xyX65pBbnfYu9MIHaI2iASsgWjvgSoEW3dBO60x2vD9T766n7ZqSWySG6dQwTrqXff_aBUE4OAGHJhoibNOG-5uHMcsh7RqtI31c/file?_download_id=2834776476044081526213627308252551087371116758876803216535281285&_notify_domain=www.dropbox.com&dl=1";
		String mailURI = "https://uc83eb289fdd519d31c6e1d53ace.dl.dropboxusercontent.com/cd/0/get/A1sqrE1UP_ZXmHaTiRaaO3PbPD9O_-oxKXdeK5W5iMZ8lrpF8ln7lmLggb8VAtSaGdtYaFW0FGOVr8bOZLhwYrf8LOIsVQc9A1RAJ0k5bADgt1k-MICNkGXif75_xEea6zI/file?_download_id=3521662688113973482907172788592283372855001390067024666847760364208&_notify_domain=www.dropbox.com&dl=1";
		String bootstrapURI = "https://uc9713b927f7e1b9c7fead178dff.dl.dropboxusercontent.com/cd/0/get/A1tdqF80S2cXG1L8tUgNGo2QExU7-xyFhjfVvESZzvEvSK8EftDTMXFjBaUGVyTBjm_jBsfYi0Poif1g6zh7dyoL7Yl2OWa5IwnIA9HKlv0Cwy3esD--ym9g6TeS24bya7E/file?_download_id=29337331885310498012872057261391577771793038110347170712334640275&_notify_domain=www.dropbox.com&dl=1";

		try {
			imageCompressor.downloadImage(file.getAbsolutePath() + "/avatar.png", avatarURI); //avatar
			Thread.sleep(1L);
			imageCompressor.downloadImage(file.getAbsolutePath() + "/logo.png", logoURI); //logo
			Thread.sleep(1L);
			imageCompressor.downloadImage(file.getAbsolutePath() + "/mail-template.html", mailURI); //mail
			Thread.sleep(1L);
			imageCompressor.downloadImage(file.getAbsolutePath() + "/bootstrap.min.css", bootstrapURI); //bootstrap
		} catch (InterruptedException e) {
			log.info(e.getMessage());
		}
	}
}
