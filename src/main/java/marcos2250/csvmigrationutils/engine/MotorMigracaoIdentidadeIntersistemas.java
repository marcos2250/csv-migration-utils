package marcos2250.csvmigrationutils.engine;

import java.util.Set;

import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDTO;
import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaService;
import marcos2250.csvmigrationutils.domain.StatusProcessamentoMigracao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotorMigracaoIdentidadeIntersistemas extends MotorMigracao<IdentidadeProcessoMigradaDTO> {

    @Autowired
    private IdentidadeProcessoMigradaService identidadeProcessoMigradaService;

    @Autowired
    private ProcessadorEncapsuladorIdentidadeIntersistemas processador;

    @Override
    public String getFaseMigracao() {
        return "FASE 0: DADOS INICIAIS";
    }

    @Override
    public int getQuantidadeTotalRegistros() {
        return identidadeProcessoMigradaService.buscarQuantidadeIdentidades();
    }

    @Override
    public Set<IdentidadeProcessoMigradaDTO> getRegistros(int offset, int tamanhoPagina) {
        return identidadeProcessoMigradaService.getIdentidades(offset, tamanhoPagina);
    }

    @Override
    public StatusProcessamentoMigracao acao(IdentidadeProcessoMigradaDTO registro) {
        return processador.criarIdentidadeIntersistemas(registro);
    }

}
