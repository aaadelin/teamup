package com.team.TeamUp.utils;

import lombok.extern.slf4j.Slf4j;

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

/**
 * Utility class to compress images
 */
@Slf4j
public class ImageCompressor {

    /**
     * Method to compress the image with a default value at compress quality of 0.2f
     * @param tempPath temporary path at which the file can be found
     * @param path final path to which to save the compressed image
     */
    public static void compressAndSave(String tempPath, String path){
        compressAndSave(tempPath, path, 0.2f);
    }

    /**
     * Method to compress the image
     * @param tempPath temporary path at which the file can be found
     * @param path final path to which to save the compressed image
     * @param compressQuality float, the compression quality of the proto, lower -> more compressed
     */
    public static void compressAndSave(String tempPath, String path, float compressQuality){
        log.info(String.format("Entering compressing image with temp path: %s and fimal path %s", tempPath, path ));
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
                param.setCompressionQuality(compressQuality);
            }

            writer.write(null, new IIOImage(image, null, null), param);
            out.close();
            imageOutputStream.close();
            writer.dispose();
            boolean deleted = file.delete();
            log.info("Image successfully compressed");
        } catch (IOException e) {
            log.info(String.format("Error occured %s ", e.getMessage()));
            e.printStackTrace();
        }
    }
}
