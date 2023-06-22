package com.example.yin.util;

import com.example.yin.model.domain.Collect;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SensitiveUtil {

    public static String filterString(String text) {

        //1、text为空不判断
        if(ObjectUtils.isEmpty(text)) return text;

        String dir = System.getProperty("user.dir") +File.separator+"music-server"+ File.separator + "sensitive_words.txt";
        File file = new File(dir);
        //2、过滤文件不在也不判断
        if (!file.exists()) return text;

        //3、将敏感词读入arraylist
        ArrayList<String> collect = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    file));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                collect.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4、遍历敏感词来判断
        for (String t : collect) {
            //5、替换
            if (!ObjectUtils.isEmpty(t) && text.indexOf(t) != -1) {
                text = text.replaceAll(t, "*");
            }
        }

        return text;
    }



}
