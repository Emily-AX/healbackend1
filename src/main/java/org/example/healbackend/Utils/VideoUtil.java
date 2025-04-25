package org.example.healbackend.Utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoUtil {

    public static void generateCover(String videoPath, String coverPath) throws IOException {
        File coverFile = new File(coverPath);

        // 确保父目录存在
        File parentDir = coverFile.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("无法创建目录: " + parentDir.getAbsolutePath());
            }
        }
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath)) {
            grabber.start();

            // 获取第一帧（可调整时间戳）
            Frame frame = grabber.grabImage();

            // 将帧转换为BufferedImage
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage image = converter.getBufferedImage(frame);

            // 保存为JPEG
            ImageIO.write(image, "jpg", new File(coverPath));

            grabber.stop();
        } catch (Exception e) {
            throw new RuntimeException("封面生成失败: " + e.getMessage(), e);
        }
    }
}
