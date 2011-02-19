package org.healthapps.birthdefects.web;

import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.healthapps.birthdefects.model.*;
import org.junit.Test;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BirthDefectControllerTest extends BirthDefectControllerBaseTestCase {

    @Test
    public void testShouldSavePersonIfValidationSucceeds() throws IOException {
        Person person = new PersonBuilder().withCreatedByUser(createdByUser).build();
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.savePerson(person.getId(), person.getName(), person.getDateOfBirth(), person.getLat(), person.getLon(), person.getBirthDefectIds(), response);
        verify(response).setContentType("text/plain");
        assertEquals("Person saved", responseOutput.toString().trim());
        verify(birthDefectsDao).store(person);
    }

    @Test
    public void testSavePersonShouldReturnErrorIfValidationFails() throws IOException {
        Person person = new PersonBuilder().withName("<invalid").build();
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.savePerson(person.getId(), person.getName(), person.getDateOfBirth(), person.getLat(), person.getLon(), person.getBirthDefectIds(), response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.BAD_REQUEST);
        final String errorMsg = responseOutput.toString().trim();
        assertTrue(errorMsg.contains("invalidCharacters"));
    }

    @Test
    public void testShouldSaveBirthDefectIfValidationSucceeds() throws IOException {
        BirthDefect birthDefect = new BirthDefectBuilder().build();
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.addBirthDefect(birthDefect.getName(), birthDefect.getCode(), response);
        verify(response).setContentType("text/plain");
        assertEquals("Birth Defect saved", responseOutput.toString().trim());
        verify(birthDefectsDao).store(birthDefect);
    }

    public void testShouldSaveEnvHazardIfValidationsSucceed() throws IOException {
        EnvHazard envHazard = new EnvHazardBuilder().build();
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.saveEnvHazard(envHazard.getName(), envHazard.getDescription(), envHazard.getVerticesAsString(),
                envHazard.getExtent(), envHazard.getNAICTitle(), response);
        verify(response).setContentType("text/plain");
        assertEquals("Environment Hazard saved", responseOutput.toString().trim());
        verify(birthDefectsDao).store(envHazard);
    }

    @Test
    public void testShouldErrorSavingEnvHazardIfValidationsFail() throws IOException {
        EnvHazard envHazard = new EnvHazardBuilder().withName("<invalid").build();
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.saveEnvHazard(envHazard.getName(), envHazard.getDescription(), envHazard.getVerticesAsString(),
                envHazard.getExtent(), envHazard.getNAICTitle(), response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.BAD_REQUEST);
        final String errorMsg = responseOutput.toString().trim();
        assertTrue(errorMsg.contains("invalidCharacters"));
        verify(birthDefectsDao, never()).store(envHazard);
    }

    @Test
    public void testSaveBirthDefectShouldReturnErrorIfValidationFails() throws IOException {
        BirthDefect birthDefect = new BirthDefectBuilder().withName("<invalid").build();
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.addBirthDefect(birthDefect.getName(), birthDefect.getCode(), response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.BAD_REQUEST);
        final String errorMsg = responseOutput.toString().trim();
        assertTrue(errorMsg.contains("invalidCharacters"));
        verify(birthDefectsDao, never()).store(birthDefect);
    }

    @Test
    public void testShouldReturnAllBirthDefects() throws IOException {
        List<BirthDefect> defects = new ArrayList<BirthDefect>();
        defects.add(new BirthDefect("spina bifida", "400"));
        when(birthDefectsDao.allBirthDefects()).thenReturn(defects);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.getBirthDefects(response);
        verify(response).setContentType("text/plain");
        assertEquals("[{\"code\":\"400\",\"id\":0,\"name\":\"spina bifida\"}]",
                responseOutput.toString().trim());
    }

    @Test
    public void testDeletePersonShouldReturn404ResponseForInvalidId() throws IOException {
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.deletePerson(-1L, response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.NOT_FOUND);
    }

    @Test
    public void testDeletePersonShouldReturnValidResponseForValidId() throws IOException, ParseException {
        final Person person = new PersonBuilder().withId(1L).withCreatedByUser(createdByUser).build();
        when(birthDefectsDao.findPersonById(eq(1L), eq(createdByUser))).thenReturn(person);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.deletePerson(1L, response);
        verify(response).setContentType("text/plain");
        assertEquals("Person deleted",
                responseOutput.toString().trim());
    }

    @Test
    public void testGetPersonShouldReturn404ResponseForInvalidId() throws IOException {
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.getPerson(-1L, response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.NOT_FOUND);
    }

    @Test
    public void testGetPersonShouldReturnValidResponseForValidId() throws IOException, ParseException {
        final BirthDefect birthDefect = new BirthDefectBuilder().withId(1L).build();
        final Date dob = new SimpleDateFormat("yyyy-mm-dd").parse("2001-01-01");
        final Person person = new PersonBuilder().withId(1L).withDOB(dob).withCreatedByUser(createdByUser).build();
        when(birthDefectsDao.findPersonById(eq(1L), eq(createdByUser))).thenReturn(person);
        when(birthDefectsDao.getBirthDefectById(eq(1L))).thenReturn(birthDefect);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.getPerson(1L, response);
        verify(response).setContentType("text/plain");
        verify(response).setStatus(BaseController.OK);
        assertEquals("{\"bbox\":[{\"lat\":11,\"lon\":12},{\"lat\":11,\"lon\":11},{\"lat\":10,\"lon\":11},{\"lat\":10,\"lon\":12},{\"lat\":11,\"lon\":12}],\"birthDefects\":[\"spina bifida\"],\"createdBy\":\"test\",\"createdById\":-1,\"dateOfBirth\":\"2001-01-01\",\"id\":1,\"lat\":\"10.1\",\"lon\":\"11.1\",\"name\":\"aperson\"}",
                responseOutput.toString().trim());
    }

    @Test
    public void testShouldReturnTenByTenPersonSummaryData() throws IOException {
        List<SpatialTensSummary> summaries = Lists.newArrayList(new SpatialTensSummary(7817, 1L));
        when(birthDefectsDao.mapSummaries(eq(SpatialTensSummary.class.getName()))).thenReturn(summaries);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.getMapTenByTenSummaries(response);
        verify(birthDefectsDao).mapSummaries(SpatialTensSummary.class.getName());
        assertEquals("[{\"count\":1,\"tensCode\":7817,\"unitsCode\":-1,\"vertices\":[{\"lat\":90,\"lon\":-170},{\"lat\":90,\"lon\":-180},{\"lat\":80,\"lon\":-180},{\"lat\":80,\"lon\":-170},{\"lat\":90,\"lon\":-170}]}]",
                responseOutput.toString().trim());
    }

    @Test
    public void testShouldReturnOneByOnePersonSummaryData() throws IOException {
        List<SpatialSummary> summaries = Lists.newArrayList(new SpatialSummary(new CSquareCode(7817, 477, 455)));
        when(birthDefectsDao.mapSummaries(eq(SpatialSummary.class.getName()))).thenReturn(summaries);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.getMapOneByOneSummaries(response);
        verify(birthDefectsDao).mapSummaries(SpatialSummary.class.getName());
        assertEquals("[{\"count\":1,\"tensCode\":7817,\"tenthsCode\":455,\"unitsCode\":477,\"vertices\":[{\"lat\":88,\"lon\":-177},{\"lat\":88,\"lon\":-178},{\"lat\":87,\"lon\":-178},{\"lat\":87,\"lon\":-177},{\"lat\":88,\"lon\":-177}]}]",
                responseOutput.toString().trim());
    }

    @Test
    public void testShouldReturnDefectSummaryByCode() throws IOException {
        long differentUserId = 2L;
        User differentUser = new UserBuilder().withId(differentUserId).build();
        List<Person> persons = Lists.newArrayList(new PersonBuilder().withCreatedByUser(differentUser).build());
        when(birthDefectsDao.getDefectsSummaryByCode(eq(new CSquareCode(7817)), eq(100))).thenReturn(persons);
        when(birthDefectsDao.getBirthDefectById(eq(1L))).thenReturn(new BirthDefect("defect1", "11"));
        when(birthDefectsDao.findUserById(differentUserId)).thenReturn(differentUser);
        controller = new BirthDefectController(birthDefectsDao, messageSource);
        controller.getDefectsSummary(7817, -1, response);
        verify(birthDefectsDao).getDefectsSummaryByCode(new CSquareCode(7817), 100);
        JSONArray jsonArray = JSONArray.fromObject(responseOutput.toString().trim());
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        assertEquals("", jsonObject.get("lat"));
        assertEquals("", jsonObject.get("lon"));
        assertEquals("", jsonObject.get("name"));
        assertEquals("defect1", ((JSONArray)jsonObject.get("birthDefects")).get(0));
        assertNotNull(jsonObject.get("bbox"));
    }

}
