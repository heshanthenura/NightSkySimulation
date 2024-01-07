module com.heshanthenura.nightsim {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.heshanthenura.nightsim to javafx.fxml;
    exports com.heshanthenura.nightsim;
}