package marcos2250.csvmigrationutils.domain;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdentidadeProcessoMigradaService {

    @Autowired
    private IdentidadeProcessoMigradaRepository identidadeProcessoMigradaRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public int buscarQuantidadeIdentidades() {
        return identidadeProcessoMigradaRepository.buscarQuantidadeIdentidades();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Set<IdentidadeProcessoMigradaDTO> getIdentidades(int offset, int tamanhoPagina) {
        return identidadeProcessoMigradaRepository.buscarIdentidadesAindaNaoFinalizadas(offset, tamanhoPagina);
    }

}
