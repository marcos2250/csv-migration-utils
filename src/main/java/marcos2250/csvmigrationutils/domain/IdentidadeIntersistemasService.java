package marcos2250.csvmigrationutils.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Service
public class IdentidadeIntersistemasService {

    @Autowired
    private IdentidadeIntersistemasRepository identidadeIntersistemasRepository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public IdentidadeIntersistemas getIdentidadeIntersistemas(String codigoProcesso) {

        IdentidadeIntersistemas identidadeIntersistemas = identidadeIntersistemasRepository
                .getIdentidadeIntersistemas(Long.parseLong(codigoProcesso));

        return Preconditions.checkNotNull(identidadeIntersistemas,
                "Ainda nao existe uma IDENTIDADE INTERSISTEMAS cadastrada para o processo do sistema original com PK "
                        + codigoProcesso);

    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public IdentidadeProcesso getIdentidade(String codigoProcesso) {
        return getIdentidadeIntersistemas(codigoProcesso).getIdentidadeProcesso();
    }

}
