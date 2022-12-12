module com.keudr.removeduplicatepictures {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.xerial.sqlitejdbc;
    requires org.apache.commons.imaging;

    opens com.keudr.removeduplicatepictures to javafx.fxml;
    opens com.keudr.removeduplicatepictures.db to javafx.fxml, javafx.base;

    exports com.keudr.removeduplicatepictures;

}