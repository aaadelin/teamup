package com.team.TeamUp.utils;

import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.UserEvent;
import com.team.TeamUp.domain.enums.UserEventType;
import com.team.TeamUp.persistance.UserEventRepository;
import com.team.TeamUp.persistance.UserRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserUtils {

    private final UserRepository userRepository;
    private UserEventRepository userEventRepository;

    @Autowired
    public UserUtils(UserRepository userRepository, UserEventRepository userEventRepository) {
        this.userRepository = userRepository;
        this.userEventRepository = userEventRepository;
    }

    public String decryptPassword(String password) {
        // TODO
        String key = "aesEncryptionKey";
//        String initVector = "encryptionIntVec";

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            Key key1 =  new SecretKeySpec(key.getBytes(), "AES");
//            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
            byte[] initVector = new byte[cipher.getBlockSize()];
            IvParameterSpec iv = new IvParameterSpec(initVector);

            cipher.init(Cipher.DECRYPT_MODE, key1, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(password));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public void createEvent(User creator, String description, UserEventType eventType){
        UserEvent userEvent = new UserEvent();
        userEvent.setCreator(creator);
        userEvent.setDescription(description);
        userEvent.setEventType(eventType);
        userEvent.setTime(LocalDateTime.now());

        userEvent = userEventRepository.save(userEvent);
        List<UserEvent> history = creator.getHistory();
        if(history == null){
            history = new ArrayList<>();
        }
        history.add(userEvent);
        creator.setHistory(history);
        userRepository.save(creator);
    }
}
