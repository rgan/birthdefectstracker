package org.healthapps.birthdefects.web;

import com.google.common.collect.Lists;
import static net.sf.json.JSONArray.fromObject;
import net.sf.json.JSONObject;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.model.*;
import org.healthapps.birthdefects.utils.BDCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class BirthDefectController extends BaseController {

    private BirthDefectsDao birthDefectDao;
    private MessageSource messageSource;
    public static final int MAX_RETURNED = 100;
    private static final int MAX_RETURNED_LUCENE_SEARCH = 10;
    private static final String ALL_DEFECT_TYPES_KEY = "allDefectTypes";

    @Autowired
    public BirthDefectController(BirthDefectsDao dao, MessageSource messageSource) {
        this.birthDefectDao = dao;
        this.messageSource = messageSource;
    }

    @InitBinder
    public void setupCustomEditors(WebDataBinder binder) {
        SimpleDateFormat dateFormat = getDateFormat();
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(Set.class, new PersonBirthDefectsPropertyEditor(birthDefectDao));
        binder.registerCustomEditor(SpatialExtent.class, new SpatialExtentPropertyEditor());
    }

    @RequestMapping(value = "/savePerson.do", method = RequestMethod.POST)
    public void savePerson(@RequestParam("id") Long id,
                           @RequestParam("name") String name,
                           @RequestParam("dateOfBirth") Date dateOfBirth,
                           @RequestParam("lat") Double lat,
                           @RequestParam("lon") Double lon,
                           @RequestParam("defects") Set<Long> defectIds,
                           HttpServletResponse response) throws IOException {
        if (!isLoggedIn()) {
            setupNotLoggedInResponse(response);
            return;
        }
        final User user = getLoggedInUser();
        Person person = new Person(id, name, dateOfBirth, new GeoLocation(lat, lon), defectIds, user);
        ErrorMessages errors = person.validate(birthDefectDao);
        if (errors.isEmpty()) {
            try {
                birthDefectDao.store(person);
                writeToResponse(response, OK, "Person saved");
            } catch (JDOObjectNotFoundException ex) {
                writeToResponse(response, NOT_FOUND, "No person found for id");
            }
        } else {
            writeToResponse(response, BAD_REQUEST, errors.getMessages(messageSource));
        }
    }

    @RequestMapping(value = "/saveEnvHazard.do", method = RequestMethod.POST)
    public void saveEnvHazard(@RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam("vertices") String vertices,
                              @RequestParam("boundingBox") SpatialExtent extent,
                              @RequestParam("naicCode") String naicCode, HttpServletResponse response
    ) throws IOException {
        EnvHazard hazard = new EnvHazard(name, description, vertices, extent, naicCode);
        ErrorMessages errors = hazard.validate(birthDefectDao);
        if (errors.isEmpty()) {
            birthDefectDao.store(hazard);
            writeToResponse(response, OK, "Environment Hazard saved");
        } else {
            writeToResponse(response, BAD_REQUEST, errors.getMessages(messageSource));
        }
    }

    @RequestMapping(value = "/addBirthDefect.do", method = RequestMethod.POST)
    public void addBirthDefect(@RequestParam("name") String name, @RequestParam("code") String code,
                               HttpServletResponse response) throws IOException {
        if (!isLoggedIn()) {
            setupNotLoggedInResponse(response);
            return;
        }
        BirthDefect birthDefect = new BirthDefect(name, code);
        ErrorMessages errors = birthDefect.validate(birthDefectDao);
        if (errors.isEmpty()) {
            birthDefectDao.store(birthDefect);
            new BDCache().remove(ALL_DEFECT_TYPES_KEY);
            writeToResponse(response, OK, "Birth Defect saved");
        } else {
            writeToResponse(response, BAD_REQUEST, errors.getMessages(messageSource));
        }
    }

    @RequestMapping(value = "/searchDownload.do", method = RequestMethod.GET)
    public void searchDownload(@RequestParam("fromDate") Date fromDate,
                               @RequestParam("toDate") Date toDate,
                               @RequestParam("defects") Set<Long> defectIds,
                               String onlyCurrentUser, HttpServletResponse response) throws IOException {
        if (!isLoggedIn()) {
            setupNotLoggedInResponse(response);
            return;
        }
        Collection adaptedObjects = getSearchResults(fromDate, toDate, defectIds, onlyCurrentUser);

        sendCSV(response, adaptedObjects);
    }

    private void sendCSV(HttpServletResponse response, Collection adaptedObjects) throws IOException {
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=searchresults.csv");
        writeCsv(response.getWriter(), adaptedObjects);
    }

    private void writeCsv(PrintWriter writer, Collection<CSVLine> adaptedObjects) {
        writer.println(PersonUIAdaptor.csvHeader());
        for(CSVLine adaptedPerson : adaptedObjects) {
            writer.println(adaptedPerson.toCsv());
            writer.flush();
        }
    }

    @RequestMapping(value = "/search.do", method = RequestMethod.GET)
    public void search(@RequestParam("fromDate") Date fromDate,
                       @RequestParam("toDate") Date toDate,
                       @RequestParam("defects") Set<Long> defectIds,
                       String onlyCurrentUser, HttpServletResponse response) throws IOException {
        if (!isLoggedIn()) {
            setupNotLoggedInResponse(response);
            return;
        }
        Collection adaptedObjects = getSearchResults(fromDate, toDate, defectIds, onlyCurrentUser);
        writeToResponse(response, OK, fromObject(adaptedObjects).toString());
    }

    private Collection getSearchResults(Date fromDate, Date toDate, Set<Long> defectIds, String onlyCurrentUser) {
        final User user = getLoggedInUser();
        Long defectId = null;
        if (!defectIds.isEmpty()) {
            defectId = defectIds.iterator().next();
        }
        Collection<Person> persons;
        if ("true".equalsIgnoreCase(onlyCurrentUser)) {
            persons = birthDefectDao.search(fromDate, toDate, defectId, user, MAX_RETURNED);
        } else {
            persons = birthDefectDao.search(fromDate, toDate, defectId, MAX_RETURNED);
        }
        Collection adaptedObjects = adaptToUIAsNeeded(persons, user);
        return adaptedObjects;
    }

    @RequestMapping(value = "/searchHazards.do", method = RequestMethod.GET)
    public void searchHazards(@RequestParam("text") String text,
                              HttpServletResponse response) throws IOException {
        final Collection<EnvHazard> hazards = birthDefectDao.searchHazards(text, MAX_RETURNED_LUCENE_SEARCH);
        writeToResponse(response, OK, fromObject(hazards).toString());
    }

    private Collection adaptToUIAsNeeded(Collection<? extends Locatable> locatables, User user) {
        Collection adaptedObjects = new HashSet();
        for (Locatable locatable : locatables) {
            if (locatable instanceof Person) {
                adaptedObjects.add(new PersonUIAdaptor((Person) locatable, birthDefectDao, user));
            } else {
                adaptedObjects.add(locatable);
            }
        }
        return adaptedObjects;
    }

    @RequestMapping(value = "/spatialSearchDownload.do", method = RequestMethod.GET)
    public void spatialSearchDownload(@RequestParam("nelat") Double neLat,
                              @RequestParam("nelng") Double neLng,
                              @RequestParam("swlat") Double swLat,
                              @RequestParam("swlng") Double swLng,
                              HttpServletResponse response) throws IOException {

        final User user = getLoggedInUser();
        final Collection<? extends Locatable> locatables =
                birthDefectDao.search(new SpatialExtent(new GeoLocation(neLat, neLng), new GeoLocation(swLat, swLng)), MAX_RETURNED);
        Collection result = adaptToUIAsNeeded(locatables, user);
        sendCSV(response, result);
    }

    @RequestMapping(value = "/spatialSearch.do", method = RequestMethod.GET)
    public void spatialSearch(@RequestParam("nelat") Double neLat,
                              @RequestParam("nelng") Double neLng,
                              @RequestParam("swlat") Double swLat,
                              @RequestParam("swlng") Double swLng,
                              HttpServletResponse response) throws IOException {

        final User user = getLoggedInUser();
        final Collection<? extends Locatable> locatables =
                birthDefectDao.search(new SpatialExtent(new GeoLocation(neLat, neLng), new GeoLocation(swLat, swLng)), MAX_RETURNED);
        Collection result = adaptToUIAsNeeded(locatables, user);
        writeToResponse(response, OK, fromObject(result).toString());
    }

    @RequestMapping(value = "/allBirthDefects.do", method = RequestMethod.GET)
    public void getBirthDefects(HttpServletResponse response) throws IOException {
        final Collection<BirthDefect> birthDefects = getAllBirthDefectTypes();
        writeToResponse(response, OK, fromObject(birthDefects).toString());
    }

    private Collection<BirthDefect> getAllBirthDefectTypes() {
        final BDCache bdCache = new BDCache();
        final Collection<BirthDefect> birthDefects = (Collection<BirthDefect>) bdCache.get(ALL_DEFECT_TYPES_KEY);
        if (birthDefects != null) {
            return birthDefects;
        }
        bdCache.put(ALL_DEFECT_TYPES_KEY, birthDefects);
        return birthDefectDao.allBirthDefects();
    }

    @RequestMapping(value = "/personTenByTenSummaries.do", method = RequestMethod.GET)
    public void getMapTenByTenSummaries(HttpServletResponse response) throws IOException {
        final Collection summaries = birthDefectDao.mapSummaries(SpatialTensSummary.class.getName());
        writeToResponse(response, OK, fromObject(summaries).toString());
    }

    @RequestMapping(value = "/personOneByOneSummaries.do", method = RequestMethod.GET)
    public void getMapOneByOneSummaries(HttpServletResponse response) throws IOException {
        final Collection summaries = birthDefectDao.mapSummaries(SpatialSummary.class.getName());
        writeToResponse(response, OK, fromObject(summaries).toString());
    }

    @RequestMapping(value = "/defectsSummary.do", method = RequestMethod.GET)
    public void getDefectsSummary(@RequestParam("tensCode") Integer tensCode,
                                  @RequestParam("unitsCode") Integer unitsCode,
                                  HttpServletResponse response) throws IOException {
        CSquareCode csCode = unitsCode > 0 ? new CSquareCode(tensCode.intValue(), unitsCode.intValue()) : new CSquareCode(tensCode.intValue());
        Collection<Person> persons = birthDefectDao.getDefectsSummaryByCode(csCode,
                MAX_RETURNED);
        Collection results = adaptToUIAsNeeded(persons, getLoggedInUser());
        writeToResponse(response, OK, fromObject(results).toString());
    }

    @RequestMapping(value = "/person.do", method = RequestMethod.GET)
    public void getPerson(@RequestParam("id") Long id,
                          HttpServletResponse response) throws IOException {
        if (!isLoggedIn()) {
            setupNotLoggedInResponse(response);
            return;
        }
        final User user = getLoggedInUser();
        final Person person = birthDefectDao.findPersonById(id, user);
        if (person != null) {
            writeToResponse(response, OK, JSONObject.fromObject(new PersonUIAdaptor(person, birthDefectDao, user)).toString());
        } else {
            writeToResponse(response, NOT_FOUND, "No such person");
        }
    }

    @RequestMapping(value = "/hazard.do", method = RequestMethod.GET)
    public void getHazard(@RequestParam("id") Long id,
                          HttpServletResponse response) throws IOException {
        final EnvHazard hazard = birthDefectDao.findEnvHazardById(id);
        if (hazard != null) {
            writeToResponse(response, OK, JSONObject.fromObject(hazard).toString());
        } else {
            writeToResponse(response, NOT_FOUND, "No hazard found");
        }
    }

    @RequestMapping(value = "/deletePerson.do", method = RequestMethod.POST)
    public void deletePerson(@RequestParam("id") Long id,
                             HttpServletResponse response) throws IOException {
        if (!isLoggedIn()) {
            setupNotLoggedInResponse(response);
            return;
        }
        final User user = getLoggedInUser();
        final Person person = birthDefectDao.findPersonById(id, user);
        if (person == null) {
            writeToResponse(response, NOT_FOUND, "Person not found");
        } else {
            birthDefectDao.delete(person);
            writeToResponse(response, OK, "Person deleted");
        }
    }

    @RequestMapping(value = "/deleteHazard.do", method = RequestMethod.POST)
    public void deleteHazard(@RequestParam("id") Long id,
                             HttpServletResponse response) throws IOException {
        final EnvHazard hazard = birthDefectDao.findEnvHazardById(id);
        if (hazard == null) {
            writeToResponse(response, NOT_FOUND, "Hazard not found");
        } else {
            birthDefectDao.delete(hazard);
            writeToResponse(response, OK, "Hazard deleted");
        }
    }

    @RequestMapping(value = "/searchNAIC.do", method = RequestMethod.GET)
    public void searchNAIC(@RequestParam("q") String query,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        final WebApplicationContext webAppContext = (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        final InputStream naicInputStream = webAppContext.getServletContext().getResourceAsStream("/WEB-INF/NAIC.csv");
        try {
            final List<IndustryCategory> matchingCategories =
                    IndustryCategory.findCategoriesContaining(naicInputStream, query);
            StringBuffer result = new StringBuffer();
            for (IndustryCategory category : matchingCategories) {
                result.append(category.getTitle() + "|" + category.getCode() + "\n");
            }
            writeToResponse(response, OK, result.toString());
        } finally {
            naicInputStream.close();
        }
    }
}
