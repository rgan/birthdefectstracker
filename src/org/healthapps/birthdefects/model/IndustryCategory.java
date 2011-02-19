package org.healthapps.birthdefects.model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndustryCategory {

    private String code;
    private String title;

    public String getTitle() {
        return title;
    }

    private static final Map<String, IndustryCategory> map = new HashMap<String, IndustryCategory>();

    public IndustryCategory(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public static List<IndustryCategory> findCategoriesContaining(InputStream naicFile, String text) {
        if (map.isEmpty()) {
            initialize(naicFile);
        }
        List<IndustryCategory> result = new ArrayList<IndustryCategory>();

        for (IndustryCategory category : map.values()) {
            if (text != null && category.getTitle().toLowerCase().contains(text.toLowerCase())) {
                result.add(category);
            }
        }
        return result;
    }


    public static void initialize(InputStream inputStream) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf(",") > 0) {
                    String code = line.substring(0, line.indexOf(","));
                    String title = line.substring(line.indexOf(",") + 1);
                    final IndustryCategory category = new IndustryCategory(code, title);
                    map.put(category.getCode(), category);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCode() {
        return code;
    }

    public static int categoryCount() {
        return map.size();
    }

    public static String getTitleByCode(String naicCode) {
        if (naicCode == null) {
            return "";
        }
        IndustryCategory category = map.get(naicCode);
        if (category == null) {
            return "";
        }
        return category.getTitle();
    }
}
