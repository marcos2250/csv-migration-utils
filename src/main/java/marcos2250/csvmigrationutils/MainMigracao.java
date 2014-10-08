package marcos2250.csvmigrationutils;

import marcos2250.csvmigrationutils.engine.MotorMigracaoIdentidadeIntersistemas;

import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main entry point for application.
 */
public class MainMigracao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainMigracao.class);

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(getContexto());

        try {
            LOGGER.info("Here we go...");

            context.getBean(MotorMigracaoIdentidadeIntersistemas.class).processar();

            LOGGER.info("Batch finalizado em "
                    + ((System.currentTimeMillis() - context.getStartupDate()) / DateTimeConstants.MILLIS_PER_SECOND)
                    + " segundos");
        } finally {
            context.close();
        }
    }

    public static String getContexto() {
        // LOCAL
        return "classpath:/applicationContext-migracao-batch-local-hsqldb.xml";
    }
}
