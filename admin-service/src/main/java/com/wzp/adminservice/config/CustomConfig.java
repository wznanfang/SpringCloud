package com.wzp.adminservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
@ConfigurationProperties(prefix = "custom-config")
public class CustomConfig {


    public static String fileSave;
    public static String excelSavePath;


    public String getFileSave() {
        return fileSave;
    }

    public void setFileSave(String fileSave) {
        CustomConfig.fileSave = fileSave;
    }

    public String getExcelSavePath() {
        return excelSavePath;
    }

    public void setExcelSavePath(String excelSavePath) {
        CustomConfig.excelSavePath = excelSavePath;
    }

}
