package marcos2250.csvmigrationutils.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "IPR_IDENTIDADE_PROCESSO")
public class IdentidadeProcesso implements IObjetoPersistente<Long> {

    private Long pk;

    @Override
    @Id
    @GeneratedValue
    @Column(name = "IPR_PK")
    public Long getPk() {
        return this.pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

}
