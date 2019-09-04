package com.team.TeamUp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageCompressor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCompressor.class);

    public static void compressAndSave(String tempPath, String path){
        LOGGER.info(String.format("Entering compressing image with temp path: %s and fimal path %s", tempPath, path ));
        try {
            File file = new File(tempPath);
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(path);
            OutputStream out = new FileOutputStream(outputFile);

            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(out);
            writer.setOutput(imageOutputStream);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if(param.canWriteCompressed()){
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.2f);
            }

            writer.write(null, new IIOImage(image, null, null), param);
            out.close();
            imageOutputStream.close();
            writer.dispose();
            boolean deleted = file.delete();
            LOGGER.info("Image successfully compressed");
        } catch (IOException e) {
            LOGGER.info(String.format("Error occured %s ", e.getMessage()));
            e.printStackTrace();
        }
    }
}
