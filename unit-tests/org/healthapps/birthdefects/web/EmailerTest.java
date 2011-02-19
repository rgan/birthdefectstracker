package org.healthapps.birthdefects.web;

import junit.framework.TestCase;
import org.healthapps.birthdefects.model.ErrorMessages;

import java.util.regex.Pattern;

public class EmailerTest extends TestCase {

    public void testShouldReturnEmptyIfAllIsValid() {
        final ErrorMessages errorMessages =
                new Emailer().validate("hello.?!,<script>", "subject");
        assertEquals(0, errorMessages.size());
    }
    
    public void testShouldReturnErrorIfSubjectIsInvalid() {
        final ErrorMessages errorMessages =
                new Emailer().validate("This is ", "<<subject");
        assertEquals(1, errorMessages.size());
    }

}
