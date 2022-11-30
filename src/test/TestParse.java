import com.keudr.removeduplicatepictures.tools.DateHelper;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestParse {

    @Test
    public void testDateTime() throws ParseException {
    /*
    * //        DateTime: '2018:01:26 18:14:41'
  //      DateTimeOriginal: '2018:01:26 18:14:41'
  * convert thse to date
    * */

        DateHelper dateHelper = new DateHelper();
        Date date = dateHelper.convertToDate("DateTime: '2018:01:26 18:14:41'");
        assertEquals(2018, date.getYear()+1900);
        assertEquals(0, date.getMonth());
        assertEquals(26, date.getDate());
        assertEquals(18, date.getHours());
        assertEquals(14, date.getMinutes());
        assertEquals(41, date.getSeconds());
    }

    @Test
    public void testDateTimeOriginal() throws ParseException {
        DateHelper dateHelper = new DateHelper();
        Date date = dateHelper.convertToDate("DateTimeOriginal: '2018:01:26 18:14:41'");
        assertEquals(2018, date.getYear()+1900);
        assertEquals(0, date.getMonth());
        assertEquals(26, date.getDate());
        assertEquals(18, date.getHours());
        assertEquals(14, date.getMinutes());
        assertEquals(41, date.getSeconds());
    }

    @Test
    public void testNull() throws ParseException {
        DateHelper dateHelper = new DateHelper();
        Date date = dateHelper.convertToDate(null);
        assertNull(date);
    }
}
