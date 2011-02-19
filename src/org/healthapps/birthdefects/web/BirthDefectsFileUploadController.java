package org.healthapps.birthdefects.web;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
public class BirthDefectsFileUploadController extends BaseController {

    private BirthDefectsDao dao;
    private MessageSource messageSource;

    @Autowired
    public BirthDefectsFileUploadController(BirthDefectsDao dao, MessageSource messageSource) {
        this.dao = dao;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/uploadBirthDefectData.do", method = RequestMethod.POST)
    public void upload(HttpServletRequest request,
                       HttpServletResponse response) throws IOException, FileUploadException {
        if (!isLoggedIn()) {
            setupNotLoggedInResponse(response);
            return;
        }
        ServletFileUpload upload = new ServletFileUpload();
        final User user = getLoggedInUser();
        FileItemIterator iterator = upload.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            InputStream stream = item.openStream();

            if (!item.isFormField()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                int noLines = 0;
                while ((line = reader.readLine()) != null) {

                    Person person;
                    try {
                        person = personFromCSV(line, user);
                    } catch (Exception e) {
                        writeToResponse(response, BAD_REQUEST, "Error reading file at line:" + line);
                        return;
                    }
                    ErrorMessages errors = person.validate(dao);
                    if (errors.isEmpty()) {
                        dao.store(person);
                    } else {
                        writeToResponse(response, BAD_REQUEST, "Error validating person data at line:" + line);
                        return;
                    }
                    noLines++;
                }
                writeToResponse(response, OK, "Persons saved: " + noLines);
            }
        }
    }

    private Person personFromCSV(String line, User user) throws ParseException {
        String[] csv = line.split(",");
        Date dob = getDateFormat().parse(csv[1]);
        Double lat = Double.parseDouble(csv[2]);
        Double lon = Double.parseDouble(csv[3]);
        Set<Long> defectIds = parseDefects(csv[4]);
        return new Person(csv[0], dob, new GeoLocation(lat, lon), defectIds, user);
    }

    private Set<Long> parseDefects(String text) {
        final String[] values = text.split(";");
        Set<Long> defectIds = new HashSet<Long>();
        for (String v : values) {
            for (BirthDefect defect : dao.allBirthDefects()) {
                if (defect.getName().equals(v)) {
                    defectIds.add(defect.getId());
                }
            }
        }
        return defectIds;
    }
}
