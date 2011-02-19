package org.healthapps.birthdefects.model;

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class IndustryCategoryTest extends TestCase {

    public void testShouldInitializeMapFromFile() throws FileNotFoundException {
        IndustryCategory.initialize(new FileInputStream("war/WEB-INF/NAIC.csv"));
        assertEquals(1811, IndustryCategory.categoryCount());
    }

    public void testShouldReturnCategoriesContainingText() throws FileNotFoundException {
        final List<IndustryCategory> result = IndustryCategory.findCategoriesContaining(new FileInputStream("war/WEB-INF/NAIC.csv"),
                "farming");
        assertEquals(41, result.size());
    }

    public void testShouldGetTitleByCode() throws FileNotFoundException {
        IndustryCategory.initialize(new FileInputStream("war/WEB-INF/NAIC.csv"));
        assertEquals("\"Agriculture, Forestry, Fishing and Hunting\"", IndustryCategory.getTitleByCode("11"));
    }
}
