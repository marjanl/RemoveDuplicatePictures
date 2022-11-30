package com.keudr.removeduplicatepictures.tools;

import com.keudr.removeduplicatepictures.PicInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.util.Date;

import static org.apache.commons.imaging.Imaging.getMetadata;

public class ImageReader {

    MD5Hash md5Hash = new MD5Hash();
    DateHelper dateHelper =new DateHelper();

    public PicInfo getPicInfo(Path path) throws IOException, ImageReadException, ParseException {
        final ImageMetadata metadata = getMetadata(path.toFile());
        ImageMetadata.ImageMetadataItem stringDateTime = null;
        ImageMetadata.ImageMetadataItem dateTimeOriginal = null;
        if(metadata!=null && metadata.getItems().size() > 0) {
             stringDateTime = metadata.getItems().stream()
                    .filter(f -> f.toString().contains("DateTime"))
                    .findFirst()
                    .orElseGet(() -> null);

            dateTimeOriginal = metadata.getItems().stream()
                    .filter(f -> f.toString().contains("DateTimeOriginal"))
                    .findFirst()
                    .orElseGet(() -> null);
        }

        PicInfo picInfo = new PicInfo(path.toAbsolutePath().toString(), path.getFileName().toString(),
                md5Hash.getChecksum(path),
                new Date(((FileTime) Files.getAttribute(path, "creationTime")).toMillis()),
                dateHelper.convertToDate(stringDateTime == null ? null : stringDateTime.toString()),
                dateHelper.convertToDate(dateTimeOriginal == null ? null : dateTimeOriginal.toString())
                );



        return picInfo;
        //String fPath, String fName, String md5, Date dCreated, Date dTime, Date dtOriginal
    }

    public void readImageMetadata(File imgFile) throws IOException, ImageReadException {
        System.out.println("processing file "+imgFile.getAbsolutePath());
        final ImageMetadata metadata = getMetadata(imgFile);
        System.out.println(metadata);
        System.out.println("--------------------------------------------------------------------------------");
        metadata.getItems().stream().filter(f -> f.toString().contains("DateTime") || f.toString().contains("DateTimeOriginal")).findFirst();

        /** Get specific meta data information by drilling down the meta * */
        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE);

            // simple interface to GPS data
            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();
            if (null != exifMetadata) {
                final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                if (null != gpsInfo) {
                    final String gpsDescription = gpsInfo.toString();
                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();

                    System.out.println("    " + "GPS Description: " + gpsDescription);
                    System.out.println("    " + "GPS Longitude (Degrees East): " + longitude);
                    System.out.println("    " + "GPS Latitude (Degrees North): " + latitude);
                }
            }

            // more specific example of how to manually access GPS values
            final TiffField gpsLatitudeRefField = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
            final TiffField gpsLatitudeField = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
            final TiffField gpsLongitudeRefField = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
            final TiffField gpsLongitudeField = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
            if (gpsLatitudeRefField != null && gpsLatitudeField != null && gpsLongitudeRefField != null && gpsLongitudeField != null) {
                // all of these values are strings.
                final String gpsLatitudeRef = (String) gpsLatitudeRefField.getValue();
                final RationalNumber[] gpsLatitude = (RationalNumber[]) (gpsLatitudeField.getValue());
                final String gpsLongitudeRef = (String) gpsLongitudeRefField.getValue();
                final RationalNumber[] gpsLongitude = (RationalNumber[]) gpsLongitudeField.getValue();

                final RationalNumber gpsLatitudeDegrees = gpsLatitude[0];
                final RationalNumber gpsLatitudeMinutes = gpsLatitude[1];
                final RationalNumber gpsLatitudeSeconds = gpsLatitude[2];

                final RationalNumber gpsLongitudeDegrees = gpsLongitude[0];
                final RationalNumber gpsLongitudeMinutes = gpsLongitude[1];
                final RationalNumber gpsLongitudeSeconds = gpsLongitude[2];

                // This will format the gps info like so:
                //
                // gpsLatitude: 8 degrees, 40 minutes, 42.2 seconds S
                // gpsLongitude: 115 degrees, 26 minutes, 21.8 seconds E

                System.out.println("    " + "GPS Latitude: " + gpsLatitudeDegrees.toDisplayString() + " degrees, " + gpsLatitudeMinutes.toDisplayString() + " minutes, " + gpsLatitudeSeconds.toDisplayString() + " seconds " + gpsLatitudeRef);
                System.out.println("    " + "GPS Longitude: " + gpsLongitudeDegrees.toDisplayString() + " degrees, " + gpsLongitudeMinutes.toDisplayString() + " minutes, " + gpsLongitudeSeconds.toDisplayString() + " seconds " + gpsLongitudeRef);
            }
        }
    }


    private static void printTagValue(final JpegImageMetadata jpegMetadata, TagInfo tagInfo) {
        final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
        if (field == null) {
            System.out.println(tagInfo.name + ": " + "Not Found.");
        } else {
            System.out.println(tagInfo.name + ": " + field.getValueDescription());
        }
    }
}
