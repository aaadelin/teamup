package com.team.teamup.utils;

import com.team.teamup.domain.ResetRequest;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.stereotype.Component;

import javax.activation.FileDataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class for sending mails
 */
@Component
@Slf4j
public class MailUtils {

    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Method to send a reset password mail
     * @param resetRequest containing the user to whom to send the request
     */
    public void sendResetURL(ResetRequest resetRequest) {
        log.debug("Entering method to send mail with parameter resetRequest {}", resetRequest);

        executorService.execute(()->{
            log.debug("Sending mail from thread {}", Thread.currentThread().getName());
            String userName = resetRequest.getUser().getFirstName() + " " + resetRequest.getUser().getLastName();
            Email email = null;
            email = EmailBuilder.startingBlank()
                    .from("TeamUp Support", "teamup.open@gmail.com")
                    .to(userName, resetRequest.getUser().getMail())
                    .withEmbeddedImage("logo", new FileDataSource(System.getProperty("user.home") + "/.TeamUpData/logo.png"))
//                        .with("bootstrap", new FileDataSource(new ClassPathResource("static/mail/bootstrap.min.css").getFile().getAbsolutePath()))
                    .withSubject("Reset password")
                    .withHTMLText(getMessage(resetRequest))
                    .buildEmail();


            MailerBuilder
//                    .withSMTPServer("email-smtp.us-west-2.amazonaws.com", 587, "AKIATGFDDRPH32FF5TXQ", "BCi5lHEVXN55zlmidCT9OqnnTEZC7bdKyWcnkmsG6cJ6")
                    .withSMTPServer("smtp.gmail.com", 587, "teamup.open", "Q3JXc]w&")
                    .buildMailer()
                    .sendMail(email);
            log.debug("Mail successfully sent from {}", Thread.currentThread().getName());
        });
    }

    /**
     * Method to read the message file
     * @param resetRequest request with the user to complete name and requestID
     * @return String containing the final message to be sent
     */
    private String getMessage (ResetRequest resetRequest) {
        try {
            File file = new File(System.getProperty("user.home") + "/.TeamUpData/mail-template.html");
            List<String> messageLines = Files.readAllLines(file.toPath());
            String message = String.join("\n", messageLines);
            message = message.replace("<token>", String.valueOf(resetRequest.getId()));
            return message;
        } catch (IOException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
