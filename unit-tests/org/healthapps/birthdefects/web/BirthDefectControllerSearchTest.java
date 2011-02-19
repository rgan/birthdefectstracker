package org.healthapps.birthdefects.web;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.healthapps.birthdefects.model.*;
import org.healthapps.birthdefects.utils.TestUtils;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class BirthDefectControllerSearchTest extends BirthDefectControllerBaseTestCase {

    public void testSearchShouldReturnDetailedResultsForDataCreatedByCurrentUser() throws ParseException, IOException {
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        final Person person = new PersonBuilder().withId(1L).withDOB(dob).withCreatedByUser(createdByUser).build();
        final BirthDefect birthDefect = new BirthDefectBuilder().withId(1L).build();
        when(birthDefectsDao.getBirthDefectById(eq(1L))).thenReturn(birthDefect);
        Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01");
        Date toDate = new SimpleDateFormat("yyyy-MM-dd").parse("2009-01-01");
        List<Person> persons = new ArrayList<Person>();
        persons.add(person);
        when(birthDefectsDao.search(eq(fromDate), eq(toDate), eq(1L), anyInt())).thenReturn(persons);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.search(fromDate, toDate, TestUtils.defectsWithOneItem(1L), "", response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.OK);
        String output = responseOutput.toString().trim();
        JSONArray jsonArray = JSONArray.fromObject(output);
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        assertEquals("10.1", jsonObject.get("lat"));
        assertEquals("11.1", jsonObject.get("lon"));
        assertEquals("aperson", jsonObject.get("name"));
        assertEquals(-1, jsonObject.get("createdById"));
    }

    public void testSearchShouldNotReturnNameOrLatLngForDataCreatedByAnotherUser() throws ParseException, IOException {
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        User differentUser = new UserBuilder().withId(2L).build();
        final Person person = new PersonBuilder().withId(1L).withDOB(dob).withCreatedByUser(differentUser).build();
        final BirthDefect birthDefect = new BirthDefectBuilder().withId(1L).build();
        when(birthDefectsDao.getBirthDefectById(eq(1L))).thenReturn(birthDefect);
        Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01");
        Date toDate = new SimpleDateFormat("yyyy-MM-dd").parse("2009-01-01");
        List<Person> persons = new ArrayList<Person>();
        persons.add(person);
        when(birthDefectsDao.findUserById(2L)).thenReturn(differentUser);
        when(birthDefectsDao.search(eq(fromDate), eq(toDate), eq(1L), anyInt())).thenReturn(persons);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.search(fromDate, toDate, TestUtils.defectsWithOneItem(1L), "", response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.OK);
        JSONArray jsonArray = JSONArray.fromObject(responseOutput.toString().trim());
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        assertEquals("", jsonObject.get("lat"));
        assertEquals("", jsonObject.get("lon"));
        assertEquals("", jsonObject.get("name"));
    }

    public void testSpatialSearchShouldReturnResults() throws ParseException, IOException {
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        final Person person = new PersonBuilder().withId(1L).withLocation(new GeoLocation(10.1, 20.1)).withDOB(dob)
                .withCreatedByUser(createdByUser).build();
        final BirthDefect birthDefect = new BirthDefectBuilder().withId(1L).build();
        when(birthDefectsDao.getBirthDefectById(eq(1L))).thenReturn(birthDefect);
        Collection persons = new ArrayList<Person>();
        persons.add(person);
        SpatialExtent extent = new SpatialExtent(new GeoLocation(11.0, 20.0), new GeoLocation(9.0, 10.0));
        when(birthDefectsDao.search(eq(extent), anyInt())).thenReturn(persons);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.spatialSearch(extent.getNE().getLat(), extent.getNE().getLon(),
                extent.getSW().getLat(), extent.getSW().getLon(),
                response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.OK);
        JSONArray jsonArray = JSONArray.fromObject(responseOutput.toString().trim());
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        assertEquals("10.1", jsonObject.get("lat"));
        assertEquals("20.1", jsonObject.get("lon"));
        assertEquals("aperson", jsonObject.get("name"));
        assertEquals(-1, jsonObject.get("createdById"));
    }

}
