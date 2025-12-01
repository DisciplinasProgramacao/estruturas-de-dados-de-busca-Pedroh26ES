import java.util.NoSuchElementException;

public class Fornecedor {
    
    private static int ultimoDocumento = 10_000;
    
    private int documento;
    private String nome;
    private ABB<Integer, Produto> produtosVendidos;

    
    /**
     * Construtor da classe Fornecedor.
     * Cria um novo fornecedor a partir do nome informado.
     * O nome deve conter pelo menos duas palavras.
     * @param nome Nome do fornecedor (deve conter pelo menos duas palavras)
     * @throws IllegalArgumentException se o nome não contiver pelo menos duas palavras
     */
    public Fornecedor(String nome) {
        if (nome == null || nome.trim().split("\\s+").length < 2) {
            throw new IllegalArgumentException("O nome do fornecedor deve conter pelo menos duas palavras.");
        }
        this.nome = nome;
        this.documento = ultimoDocumento++;
        this.produtosVendidos = new ABB<>();
    }
    
    /**
     * Adiciona um produto à lista de produtos vendidos pelo fornecedor.
     * @param produto O produto a ser adicionado
     * @throws IllegalArgumentException se o produto for nulo
     */
    public void adicionarProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Não é possível adicionar um produto nulo.");
        }
        produtosVendidos.inserir(produto.hashCode(), produto);
    }
    
    /**
     * Retorna o documento identificador do fornecedor.
     * @return documento do fornecedor
     */
    public int getDocumento() {
        return documento;
    }
    
    /**
     * Retorna o nome do fornecedor.
     * @return nome do fornecedor
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Retorna a árvore de produtos vendidos pelo fornecedor.
     * @return árvore de produtos
     */
    public ABB<Integer, Produto> getProdutosVendidos() {
        return produtosVendidos;
    }
    
    /**
     * Retorna uma representação textual do fornecedor.
     * @return String com nome, documento e produtos vendidos
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FORNECEDOR: ").append(nome).append("\n");
        sb.append("DOCUMENTO: ").append(documento).append("\n");
        sb.append("PRODUTOS VENDIDOS:\n");
        sb.append(produtosVendidos.toString());
        return sb.toString();
    }
    
    /**
     * Retorna o código hash do fornecedor (documento).
     * @return código hash baseado no documento
     */
    @Override
    public int hashCode() {
        return documento;
    }
    
    /**
     * Verifica igualdade entre fornecedores com base no documento.
     * @param obj Outro objeto para comparação
     * @return true se os documentos forem iguais, false caso contrário
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fornecedor outro = (Fornecedor) obj;
        return documento == outro.documento;
    }
}