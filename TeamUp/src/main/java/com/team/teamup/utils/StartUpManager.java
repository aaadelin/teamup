package com.team.teamup.utils;

import com.team.teamup.domain.TaskStatus;
import com.team.teamup.persistence.TaskStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Slf4j
public class StartUpManager {

    private TaskStatusRepository taskStatusRepository;
    private UserUtils userUtils;

    @Autowired
    public StartUpManager(UserUtils userUtils, TaskStatusRepository taskStatusRepository) {
        this.userUtils = userUtils;
        this.taskStatusRepository = taskStatusRepository;
    }

    public void createFiles(){
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

    public void createAdmin() {
        userUtils.createAdminIfNoneExistent();
    }

    public void createDefaultTaskStatuses(){
        List<TaskStatus> taskStatuses = taskStatusRepository.findAll();
        if (taskStatuses.size() != 0){
            return;
        }
        List<String> statuses = List.of("OPEN", "IN PROGRESS", "UNDER REVIEW", "DONE");

        for(int i=0; i<statuses.size();  i++){
            taskStatusRepository.save(new TaskStatus(statuses.get(i), i));
        }

        log.info("Default statuses are created");
    }
}
