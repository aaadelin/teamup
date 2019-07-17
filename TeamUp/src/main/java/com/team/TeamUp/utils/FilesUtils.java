package com.team.TeamUp.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FilesUtils {

    public static String readAllFile(String file) throws IOException {
        StringBuilder lines = new StringBuilder();
        InputStream in = new ClassPathResource(file).getInputStream();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
            String line;

            while((line =  bufferedReader.readLine()) != null){
                lines.append(line);
            }

            return lines.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
