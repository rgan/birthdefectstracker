package org.healthapps.birthdefects.web;

import junit.framework.TestCase;
import org.healthapps.birthdefects.model.SpatialExtent;

public class SpatialExtentPropertyEditorTest extends TestCase {

    public void testShouldSetExtentGivenValidInput() {
        SpatialExtentPropertyEditor editor = new SpatialExtentPropertyEditor();
        editor.setAsText("20.1, -10.2, 11.9, 1.5");
        SpatialExtent extent = (SpatialExtent) editor.getValue();
        assertEquals(20.1, extent.getNE().getLat());
        assertEquals(-10.2, extent.getNE().getLon());
        assertEquals(11.9, extent.getSW().getLat());
        assertEquals(1.5, extent.getSW().getLon());
    }

    public void testShouldThrowExceptionGivenInvalidInput() {
        try {
            SpatialExtentPropertyEditor editor = new SpatialExtentPropertyEditor();
            editor.setAsText("invalid");
            fail();
        } catch (IllegalArgumentException ex) {

        }

    }
}
