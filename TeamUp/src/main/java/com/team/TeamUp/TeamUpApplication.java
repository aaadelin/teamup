package com.team.TeamUp;

import com.team.TeamUp.utils.ImageCompressor;
import com.team.TeamUp.utils.UserUtils;
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
			String[] list = file.list() == null? new String[0] : file.list();
			for (String fileName : list) {
				File inner = new File(file.getPath(), fileName);
				inner.delete();
			}
			file.delete();
		}
		file.mkdirs();
		ImageCompressor imageCompressor = new ImageCompressor();
		String avatarURI = "https://ucd4b90e0bbd0e4199a637eba259.previews.dropboxusercontent.com/p/thumb/AAzBj-C4uXAxM433Vm2ZNwZ7BT6EOpo3PD4zIJm6wnHb1myWxaC90tgO3f0NobzDE6Fnj1imN3p3BWEyqlL6ToAIsNlKRNnawS3hbnZlpzQg60DiBb57hRxjJG9EHA_ORc-AFqeji9UtPztwnTPkR7waxErPfTUtB8ivRsXZOXWR9D2isT_-91Sim2W_aG5RhNSlLm0hc4bWsLig2i3-FKNy3R2bzt2sjpVpQskg1hBy00-zkVDLqV5z2BvwnrC40NoLKs1-rC86gV5YR-uHFdrYm16R4YE5kAWOcfbo-rNE03NgsOaX-yyKlKFkj74OskeBP_ycx3PsgF79jNGoC059BwnauN_9Q9ZpR0LyGoafioJDNrvEntSo5Q9q2GW5MlLz9TAE_A0YNfLJZOZ7-5XR/p.png?fv_content=true&size_mode=5";
		String logoURI = "https://uc9e24869d551fd1ed09f1ec9530.previews.dropboxusercontent.com/p/thumb/AAz6XC8KN4FTVhWQRBeHBHv7kPXLQACEY520Pp-VH4WpEDme0FYIRrWfDTZhQkBEGzwSrdOzlmsSdv0mKPjyp-mpNJV6t2LowUXchEyA3e8gSuHeRTyi5jxDaZ1Blk1j5JOvYYZwUHb9qMA37RBFSb8q1PQKWmvt2t67yeYBGi3GKkNekvlTz_h6pZ6MC0Rd0c3a99jmAKHCUqAFA4pPb0ExATef5GlvouTdDT0zkf_BqPd7JLrGoaeKskoZJKIy5fykOs6QCNa_Fqj6eHuIX-o4IoAarfD3y4DLw11_h0vTgTxLoQstTkIimJ72g-TErHAg7lYuToE0DHnTvKEOuXA5V5yQnauHVFO8k4HVSLNRC4xaqGceCRIfyrZYSyLZFMSLy-4k_eAISseuT10JV0C4/p.png?fv_content=true&size_mode=5";
		String mailURI = "https://uc01ca1371effafdc2656df747ac.dl.dropboxusercontent.com/cd/0/get/A1s0casMK_M-4BekqR2cw_15V6ooF0nBnA1SDUTekJ-t_qGwHsNbJTL2TNKPukqPiPIhGSU73hQfmP4yU4G6nsW5Z1ytPsswqgsPWthyl1NfbcpwCRuoW95NanN-hStPfmc/file?_download_id=88535250186043580965114225078500557621567396595475240970911060681&_notify_domain=www.dropbox.com&dl=1";
		String bootstrapURI = "https://uc9713b927f7e1b9c7fead178dff.dl.dropboxusercontent.com/cd/0/get/A1tdqF80S2cXG1L8tUgNGo2QExU7-xyFhjfVvESZzvEvSK8EftDTMXFjBaUGVyTBjm_jBsfYi0Poif1g6zh7dyoL7Yl2OWa5IwnIA9HKlv0Cwy3esD--ym9g6TeS24bya7E/file?_download_id=29337331885310498012872057261391577771793038110347170712334640275&_notify_domain=www.dropbox.com&dl=1";

		imageCompressor.downloadImage(file.getAbsolutePath() + "/avatar.png", avatarURI); //avatar
		imageCompressor.downloadImage(file.getAbsolutePath() + "/logo.png", logoURI); //logo
		imageCompressor.downloadImage(file.getAbsolutePath() + "/mail-template.html", mailURI); //mail
		imageCompressor.downloadImage(file.getAbsolutePath() + "/bootstrap.min.css", bootstrapURI); //bootstrap
	}
}
