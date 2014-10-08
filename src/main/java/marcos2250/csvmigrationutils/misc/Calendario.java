package marcos2250.csvmigrationutils.misc;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class Calendario {

    public LocalDateTime getDataHoraAtual() {
        return LocalDateTime.now();
    }

    public LocalDate getDataAtual() {
        return getDataHoraAtual().toLocalDate();
    }

    public int getAnoCorrente() {
        return getDataHoraAtual().getYear();
    }

}
