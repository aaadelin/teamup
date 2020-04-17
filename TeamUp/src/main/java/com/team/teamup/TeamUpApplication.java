package com.team.teamup;

import com.team.teamup.utils.ImageCompressor;
import com.team.teamup.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class TeamUpApplication {

	@Autowired
	private UserUtils userUtils;

	public static void main(String[] args) {
		SpringApplication.run(TeamUpApplication.class, args);
		System.out.println("Server Started");
		// TODO:
		// in case of failing: change database
		// remove logo from email
		// check download links
	}

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			userUtils.createAdminIfNoneExistent();
			createFiles();
		};
	}

	public static void createFiles(){
		//create directory and download Avatar
		String home = System.getProperty("user.home");
		File file = new File(home + "/.TeamUpData");
		if (file.exists()) {
//			String[] list = file.list() == null? new String[0] : file.list();
//			for (String fileName : list) {
//				File inner = new File(file.getPath(), fileName);
//				inner.delete();
//			}
//			file.delete();
		}else {
			file.mkdirs();
		}
		ImageCompressor imageCompressor = new ImageCompressor();
//		String avatarURI = "https://getdrawings.com/free-icon/profile-icon-58.png";
		String avatarURI = "https://ucbcb4e03d32256f6b9a385fd6e9.previews.dropboxusercontent.com/p/thumb/AAyxjvrtwuGOXeeKNWpQus-n0ULh3InqNO0IlHVKMrpu62bA-s16i_87nFDrYfd1gDeEZUFzLrf7wwaxKK_ljvcllb25QTNiyNRhYFM4YtfK3giHvHeYe9ngMwAEi9XY2sR118OcdFbBPPiJAvHMcx_qaSjbAJHKCtRfifSELfAkBhe51uUompYEiROOFah78we2MlgTRoxt97kctM9U135egdGKZAmyJ-94FcLE20LVLfht1cI0nZ6PWeF6-8qEN_T_kFLtNTP6G0JDa51PMyN0oAKLEz7ChLSqYCgmzmrBUuGVzUwuIFIO8COjVKK2oHmFM-rGzRxZbpCj7EEV9Gxeej2-v_cEerxXjxS5adKeGSf-5Y6ZCHeCq7cVq2gM5p82EMcAWzNWH3iMpAj9LJ26/p.png?fv_content=true&size_mode=5";
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
			e.printStackTrace();
		}
	}
}
