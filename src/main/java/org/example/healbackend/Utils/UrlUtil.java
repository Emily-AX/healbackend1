package org.example.healbackend.Utils;


import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


public class UrlUtil {
    static private final String basePath="/file/upload/";
    
    public static String getUrl(String newFilename){
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(basePath)
                .path(newFilename)
                .toUriString();
        System.out.println(fileUrl);
        return fileUrl;
    }
    

}
