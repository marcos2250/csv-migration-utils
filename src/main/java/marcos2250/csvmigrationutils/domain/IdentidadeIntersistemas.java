package marcos2250.csvmigrationutils.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import marcos2250.csvmigrationutils.misc.ConstantesHibernate;

@Entity
@Table(name = "ISI_IDENTIDADE_INTERSISTEMAS")
@SequenceGenerator(name = "SEQ_ISI", sequenceName = "SEQ_ISI", allocationSize = ConstantesHibernate.ALLOCATION_SIZE)
public class IdentidadeIntersistemas implements IObjetoPersistente<Long>, EntidadeMigravel {

    private Long pk;

    // SYSTEM A
    private String pkOriginal;

    // SYSTEM B
    private IdentidadeProcesso identidadeProcesso;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ISI")
    @Column(name = "ISI_ID")
    public Long getPk() {
        return pk;
    }

    @Column(name = "ISI_CD_PK_ORIGINAL", length = 30, nullable = false)
    public String getPkOriginal() {
        return pkOriginal;
    }

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "ISI_ID_SISTEMA2_IDENTIDADE")
    public IdentidadeProcesso getIdentidadeProcesso() {
        return this.identidadeProcesso;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public void setPkOriginal(String pkOriginal) {
        this.pkOriginal = pkOriginal;
    }

    public void setIdentidadeProcesso(IdentidadeProcesso identidadeProcesso) {
        this.identidadeProcesso = identidadeProcesso;
    }

    @Override
    @Transient
    public String getIdentificador() {
        return getPkOriginal();
    }
}
