package marcos2250.csvmigrationutils.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import marcos2250.csvmigrationutils.misc.ConstantesHibernate;

import com.google.common.base.Preconditions;

/**
 * Presta-se como interface de migracao entre os modulos.
 */
@Entity
@Table(name = "SMI_STATUS_MIGRACAO_IDENTIDADE")
@SequenceGenerator(name = "SEQ_SMI", sequenceName = "SEQ_SMI", allocationSize = ConstantesHibernate.ALLOCATION_SIZE)
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
public class StatusMigracaoIdentidade implements IEntidadeMigracaoMonitoravel, IObjetoPersistente<Long> {

    private static final long serialVersionUID = -5140502364715628366L;

    /**
     * Integrador bidirecional entre sistema 1 <-> sistema 2.
     */
    private IdentidadeIntersistemas identidadeIntersistemas;

    private String resumoUltimaAcao;

    private String stacktrace;

    private Long pk;

    StatusMigracaoIdentidade() {
        // cglib man
    }

    StatusMigracaoIdentidade(IdentidadeIntersistemas identidadeIntersistemas) {
        this.identidadeIntersistemas = Preconditions.checkNotNull(identidadeIntersistemas,
                "A identidade intersistemas nao pode ser nula quando da criacao do status migracao!");
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SMI")
    @Column(name = "SMI_ID")
    public Long getPk() {
        return this.pk;
    }

    @Override
    @OneToOne(optional = false, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "ISI_ID_INTERSYSTEMS_IDENTITY", nullable = false)
    public IdentidadeIntersistemas getIdentidadeIntersistemas() {
        return identidadeIntersistemas;
    }

    @Override
    @Column(name = "SMI_DS_SUMMARY", length = TAMANHO_MAXIMO_ERRO)
    public String getResumoUltimaAcao() {
        return resumoUltimaAcao;
    }

    @Override
    @Column(name = "SMI_DS_STACKTRACE", length = TAMANHO_MAXIMO_STACKTRACE)
    public String getStackTrace() {
        return stacktrace;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public void setStackTrace(String stackTrace) {
        this.stacktrace = stackTrace;
    }

    public void setResumoUltimaAcao(String erroCorrente) {
        this.resumoUltimaAcao = erroCorrente;
    }

    protected void setIdentidadeIntersistemas(IdentidadeIntersistemas identidadeIntersistemas) {
        this.identidadeIntersistemas = identidadeIntersistemas;
    }

}
