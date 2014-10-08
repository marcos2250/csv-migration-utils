package marcos2250.csvmigrationutils.carga;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.StopWatch;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

public class CarregadorDados {

    private static final float MULTIPLICADOR_MILISEGUNDO_SEGUNDO = 1000.0f;

    private static final Logger LOGGER = LoggerFactory.getLogger(CarregadorDados.class);

    /**
     * Usar tabela de Controle de Cargas? Deve ser FALSE em producao!
     */
    private Boolean usarTabelaDeControleDeCargas = Boolean.FALSE;

    @Autowired
    private SessionFactory sessionFactory;

    private List<ICarga> cargas;

    public void carregar() {

        LOGGER.info("Iniciando carga de dados...");

        Session sessao = getSessao(sessionFactory);

        // Criacao da tabela de controle, se necessario
        if (usarTabelaDeControleDeCargas) {
            criarTabelaControleCarga(sessao);
        }

        // Processamento das cargas
        processarCarga(sessao);

        fecharSessao(sessionFactory);

    }

    private void criarTabelaControleCarga(Session sessao) {

        Transaction transaction = null;

        try {
            transaction = sessao.beginTransaction();

            String queryDescription = String.format("SELECT COUNT(*) FROM INFORMATION_SCHEMA.SYSTEM_TABLES "
                    + "WHERE TABLE_SCHEM='PUBLIC' AND TABLE_NAME='%s'", getNomeTabelaControle());

            boolean cargaJahFoiFeita = (!BigInteger.ZERO.equals(sessao.createSQLQuery(queryDescription).uniqueResult()));

            if (!cargaJahFoiFeita) {
                // carga-lo-ei agora
                LOGGER.info("Tabela de controle de carga inexiste. Criando a tabelinha.");

                sessao.createSQLQuery(queryParaGerarTabelaControle()).executeUpdate();

                transaction.commit();
            }

        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Nao foi possivel completar a criacao da tabela de controle de carga.", e);
        }
    }

    private void processarCarga(Session sessao) {
        for (ICarga carga : getCargas()) {
            processarCarga(sessao, carga);
        }
    }

    private void processarCarga(Session sessao, ICarga carga) {
        StatusCarga statusCarga = StatusCarga.SUCESSO;
        String descricaoErro = "Processado com sucesso!";
        String identificadorCarga = carga.getIdentificadorUnivoco();

        LOGGER.info("Iniciando carga de " + identificadorCarga);
        Transaction transaction = null;

        try {
            transaction = sessao.beginTransaction();

            if (usarTabelaDeControleDeCargas) {
                String query = String.format("SELECT %s FROM %s WHERE %s = '%s'", getNomeColunaStatusProcessamento(),
                        getNomeTabelaControle(), getNomeColunaCarga(), identificadorCarga);

                Integer statusProcessamentoCarga = (Integer) sessao.createSQLQuery(query).uniqueResult();
                if (statusProcessamentoCarga != null && StatusCarga.SUCESSO.getCodigo() == statusProcessamentoCarga) {
                    LOGGER.info("A carga " + identificadorCarga + " ja foi realizada!");
                    return;
                }
            }

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            carga.carregar(sessao);
            stopWatch.split();
            stopWatch.stop();
            LOGGER.info("O tempo de processamento da carga " + carga.getIdentificadorUnivoco() + " foi de "
                    + ((float) stopWatch.getSplitTime() / MULTIPLICADOR_MILISEGUNDO_SEGUNDO) + "s.");

            transaction.commit();

            // CHECKSTYLE:OFF
        } catch (Exception e) {
            LOGGER.error("ERRO na carga " + identificadorCarga, e);
            // CHECKSTYLE:ON
            if (transaction != null) {
                LOGGER.error("Executando operacao Rollback na carga " + identificadorCarga);
                transaction.rollback();
            }
            statusCarga = StatusCarga.FRACASSO;
            descricaoErro = "Lancou a seguinte excecao: " + e.toString();
        }

        if (usarTabelaDeControleDeCargas) {
            marcarResultadoDaCargaEspecifica(sessao, identificadorCarga, statusCarga, descricaoErro);
        }

        logarResultadoDaCargaEspecifica(identificadorCarga, statusCarga);
    }

    private void marcarResultadoDaCargaEspecifica(Session sessao, String identificadorCarga, StatusCarga statusCarga,
            String descricaoErro) {

        Transaction transaction = null;

        try {
            transaction = sessao.beginTransaction();

            String queryDelete = String.format("DELETE FROM %s WHERE %s = '%s'", getNomeTabelaControle(),
                    getNomeColunaCarga(), identificadorCarga);
            sessao.createSQLQuery(queryDelete).executeUpdate();

            String queryDescription = inserirNaTabelaControle(identificadorCarga, statusCarga, descricaoErro);
            sessao.createSQLQuery(queryDescription).executeUpdate();

            transaction.commit();
            // CHECKSTYLE:OFF
        } catch (Exception e) {
            // CHECKSTYLE:ON
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Problema ao inserir status da carga  " + identificadorCarga, e);
        }

    }

    private void logarResultadoDaCargaEspecifica(String identificadorCarga, StatusCarga statusCarga) {
        if (StatusCarga.SUCESSO.equals(statusCarga)) {
            LOGGER.info("Carga " + identificadorCarga + " feita com sucesso!");
        } else {
            LOGGER.error("Ocorreu erro de processamento na carga " + identificadorCarga);
        }
    }

    private String getNomeTabelaControle() {
        return "CONTROLE_DA_CARGA";
    }

    private String getNomeColunaCarga() {
        return "IDENTIFICADOR_CARGA";
    }

    private String getNomeColunaStatusProcessamento() {
        return "STATUS_PROCESSAMENTO";
    }

    private String getNomeColunaDescricaoErro() {
        return "DESCRICAO_ERRO_PROCESSAMENTO";
    }

    private String queryParaGerarTabelaControle() {
        return String.format("CREATE TABLE %s (%s VARCHAR(255), %s INTEGER, %s VARCHAR(30000))", //
                getNomeTabelaControle(), //
                getNomeColunaCarga(), //
                getNomeColunaStatusProcessamento(), //
                getNomeColunaDescricaoErro());
    }

    private String inserirNaTabelaControle(String className, StatusCarga status, String descricaoErro) {

        int statusProcessamento = status.getCodigo();

        return String.format("INSERT INTO %s (%s, %s, %s) VALUES ('%s', %d, '%s')", //
                getNomeTabelaControle(), //
                getNomeColunaCarga(), //
                getNomeColunaStatusProcessamento(), //
                getNomeColunaDescricaoErro(), //
                className, //
                statusProcessamento, //
                StringEscapeUtils.escapeSql(descricaoErro));
    }

    private Session getSessao(SessionFactory sessionFactory) {
        if (!SessionFactoryUtils.isDeferredCloseActive(sessionFactory)) {
            SessionFactoryUtils.initDeferredClose(sessionFactory);
        }
        return SessionFactoryUtils.getNewSession(sessionFactory);
    }

    private void fecharSessao(SessionFactory sessionFactory) {
        SessionFactoryUtils.processDeferredClose(sessionFactory);
    }

    // A lista de cargas para aplicacao local a ser executada pelo Spring e
    // definida no arquivo
    // applicationContext-web-desktop.xml, como propriedade do bean
    // cargaDeDados,
    // definido por esta classe (CarregadorDados).
    // Para carga do Fit, esta no arquivo applicationContext-web-fit.xml
    public List<ICarga> getCargas() {
        return cargas;
    }

    public void setCargas(List<ICarga> cargas) {
        this.cargas = cargas;
    }

    public void setUsarTabelaDeControleDeCargas(Boolean usarTabelaDeControleDeCargas) {
        this.usarTabelaDeControleDeCargas = usarTabelaDeControleDeCargas;
    }

    public Boolean getUsarTabelaDeControleDeCargas() {
        return usarTabelaDeControleDeCargas;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
