package org.healthapps.birthdefects.web;

import junit.framework.TestCase;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.model.BirthDefect;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonBirthDefectsPropertyEditorTest extends TestCase {

    public void testSetAsTextShouldConvertNamesToDefectIds() {
        final String defectName = "spina bifida";
        final Long defectId = 1L;
        Set<Long> ids = new HashSet<Long>();
        ids.add(defectId);
        BirthDefectsDao dao = mock(BirthDefectsDao.class);
        List<BirthDefect> defects = new ArrayList<BirthDefect>();
        defects.add(new BirthDefect(defectId, defectName, "400"));
        when(dao.idsFromNames(defectName)).thenReturn(ids);
        final PersonBirthDefectsPropertyEditor propertyEditor = new PersonBirthDefectsPropertyEditor(dao);
        propertyEditor.setAsText(defectName);
        assertEquals(ids, propertyEditor.getValue());
    }

    public void testSetAsTextShouldReturnEmptySetIfDefectNameIsNotFound() {
        final String defectName = "spina bifida";
        BirthDefectsDao dao = mock(BirthDefectsDao.class);
        final PersonBirthDefectsPropertyEditor propertyEditor = new PersonBirthDefectsPropertyEditor(dao);
        propertyEditor.setAsText(defectName);
        final Set<Long> defects = (Set<Long>) propertyEditor.getValue();
        assertTrue(defects.isEmpty());
    }
}
