package marcos2250.csvmigrationutils.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import marcos2250.csvmigrationutils.misc.ConstantesHibernate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "IPM_IDENTIDADE_PROCESSO_MIGRADA")
@SequenceGenerator(name = "SEQ_IPM", sequenceName = "SEQ_IPM", allocationSize = ConstantesHibernate.ALLOCATION_SIZE)
public class IdentidadeProcessoMigradaDTO implements EntidadeMigravel, IObjetoPersistente<Long> {

    private Long pk;

    // SYSTEM 1
    private String pkOriginal;
    private String comment;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_IPM")
    @Column(name = "IPM_ID")
    public Long getPk() {
        return this.pk;
    }

    @Column(name = "IPM_CD_PK_ORIGINAL", length = 30)
    public String getPkOriginal() {
        return pkOriginal;
    }

    @Column(name = "IPM_DS_COMMENT", length = 255)
    public String getComment() {
        return comment;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public void setPkOriginal(String pkOriginal) {
        this.pkOriginal = pkOriginal;
    }

    @Override
    @Transient
    public String getIdentificador() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        sb.append("Pk: " + pk);

        if (StringUtils.isNotEmpty(pkOriginal)) {
            sb.append(" Pk original: " + pkOriginal);
        }
        if (StringUtils.isNotEmpty(comment)) {
            sb.append(" Comment: " + comment);
        }

        sb.append("]");

        return sb.toString();
    }

    @Override
    public boolean equals(Object comparado) {

        if (!IdentidadeProcessoMigradaDTO.class.isInstance(comparado)) {
            return false;
        }

        IdentidadeProcessoMigradaDTO other = IdentidadeProcessoMigradaDTO.class.cast(comparado);

        EqualsBuilder eb = new EqualsBuilder();

        eb.append(this.pkOriginal, other.pkOriginal);

        return eb.isEquals();
    }

    @Override
    public int hashCode() {

        HashCodeBuilder hc = new HashCodeBuilder();

        hc.append(this.pkOriginal);

        return hc.toHashCode();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("[Pk Original = " + getIdentificador() + "; comment = " + comment + " ] ");

        return sb.toString();
    }

    public void setComment(String string) {
        this.comment = string;
    }

}
