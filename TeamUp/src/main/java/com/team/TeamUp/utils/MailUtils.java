package com.team.TeamUp.utils;

import com.team.TeamUp.domain.ResetRequest;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class MailUtils {

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void sendResetURL(ResetRequest resetRequest) {
        log.info("Entering method to send mail with parameter resetRequest {}", resetRequest);

        executorService.execute(()->{
            log.info("Sending mail from thread {}", Thread.currentThread().getName());
            String userName = resetRequest.getUser().getFirstName() + " " + resetRequest.getUser().getLastName();
            Email email = EmailBuilder.startingBlank()
                    .from("TeamUp Support", "teamup.open@gmail.com")
                    .to(userName, resetRequest.getUser().getMail())
                    .withSubject("Reset password")
                    .withHTMLText(getMessage(resetRequest))
                    .buildEmail();

            MailerBuilder
                    .withSMTPServer("smtp.gmail.com", 587, "teamup.open", "Q3JXc]w&")
                    .buildMailer()
                    .sendMail(email);
        });
    }

    private String getMessage (ResetRequest resetRequest) {
        try {
            File file = new ClassPathResource("static/mail-template.html").getFile();
            List<String> messageLines = Files.readAllLines(file.toPath());
            String message = String.join("\n", messageLines);
            message = message.replace("<token>", String.valueOf(resetRequest.getId()));
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
