package marcos2250.csvmigrationutils.carga;

public enum StatusCarga {

    SUCESSO(1, "Sucesso no processamento"), //
    FRACASSO(0, "Fracasso no processamento");

    private int codigo;
    private String descricao;

    private StatusCarga(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;

    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

}
