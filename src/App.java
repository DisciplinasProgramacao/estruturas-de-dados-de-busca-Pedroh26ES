import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class App {

    /**
     * Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto
     */
    static String nomeArquivoDados;

    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Quantidade de produtos cadastrados atualmente na lista */
    static int quantosProdutos = 0;

    static ABB<String, Produto> produtosCadastradosPorNome;

    static ABB<Integer, Produto> produtosCadastradosPorId;

    // Tarefa 2: Declaração das estruturas para fornecedores
    static AVL<Integer, Fornecedor> fornecedoresPorDocumento;

    static TabelaHash<Integer, Lista<Fornecedor>> produtosFornecedores;

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {

        T valor;

        System.out.println(mensagem);
        try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    /**
     * Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Carregar produtos por nome/descrição");
        System.out.println("3 - Carregar produtos por id");
        System.out.println("4 - Procurar produto, por nome");
        System.out.println("5 - Procurar produto, por id");
        System.out.println("6 - Carregar fornecedores");
        System.out.println("7 - Relatório de fornecedor");
        System.out.println("8 - Fornecedores de um produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        try {
            return Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Lê os dados de um arquivo-texto e retorna uma árvore de produtos.
     */
    static <K> ABB<K, Produto> lerProdutos(String nomeArquivoDados, Function<Produto, K> extratorDeChave) {

        Scanner arquivo = null;
        int numProdutos;
        String linha;
        Produto produto;
        ABB<K, Produto> produtosCadastrados;
        K chave;

        try {
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

            numProdutos = Integer.parseInt(arquivo.nextLine());
            produtosCadastrados = new ABB<K, Produto>();

            for (int i = 0; i < numProdutos; i++) {
                linha = arquivo.nextLine();
                produto = Produto.criarDoTexto(linha);
                chave = extratorDeChave.apply(produto);
                produtosCadastrados.inserir(chave, produto);
            }
            quantosProdutos = numProdutos;
            System.out.println("Produtos carregados com sucesso: " + quantosProdutos);

        } catch (IOException excecaoArquivo) {
            System.out.println("Erro ao ler produtos: " + excecaoArquivo.getMessage());
            produtosCadastrados = null;
        } finally {
            if (arquivo != null)
                arquivo.close();
        }

        return produtosCadastrados;
    }

    // Tarefa 3: Implementação robusta de lerFornecedores
    static <K> AVL<K, Fornecedor> lerFornecedores(String nomeArquivoDados, Function<Fornecedor, K> extratorDeChave) {

        Scanner arquivo = null;
        int numFornecedores;
        String linha;
        Fornecedor fornecedor;
        AVL<K, Fornecedor> fornecedoresCadastrados;
        K chave;
        Random random = new Random();

        // Inicializa a tabela hash de produtos-fornecedores se ainda não foi inicializada
        if (produtosFornecedores == null) {
            produtosFornecedores = new TabelaHash<>(Math.max(10, quantosProdutos * 2));
        }

        try {
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

            numFornecedores = Integer.parseInt(arquivo.nextLine());
            fornecedoresCadastrados = new AVL<K, Fornecedor>();
            
            System.out.println("Carregando " + numFornecedores + " fornecedores...");

            for (int i = 0; i < numFornecedores; i++) {
                linha = arquivo.nextLine().trim();
                fornecedor = new Fornecedor(linha);

                // Seleciona aleatoriamente até 6 produtos para o fornecedor
                // Requer que os produtos já tenham sido carregados pelo ID (Opção 3) para funcionar corretamente
                if (produtosCadastradosPorId != null && !produtosCadastradosPorId.vazia() && quantosProdutos > 0) {
                    int quantidadeProdutos = random.nextInt(6) + 1; // 1 a 6 produtos

                    for (int j = 0; j < quantidadeProdutos; j++) {
                        // Sorteia um ID no intervalo [10000, 10000 + quantosProdutos)
                        int idSorteado = 10_000 + random.nextInt(quantosProdutos);

                        try {
                            Produto produto = produtosCadastradosPorId.pesquisar(idSorteado);
                            
                            // Adiciona na árvore interna do fornecedor
                            fornecedor.adicionarProduto(produto);

                            // Adiciona o fornecedor à lista de fornecedores do produto na Tabela Hash
                            Lista<Fornecedor> fornecedoresDoProduto = produtosFornecedores.pesquisar(idSorteado);
                            if (fornecedoresDoProduto == null) {
                                fornecedoresDoProduto = new Lista<>();
                            }
                            fornecedoresDoProduto.inserir(fornecedor);
                            produtosFornecedores.inserir(idSorteado, fornecedoresDoProduto);

                        } catch (NoSuchElementException e) {
                            // Produto sorteado não encontrado (ignora e tenta o próximo)
                        }
                    }
                }

                chave = extratorDeChave.apply(fornecedor);
                fornecedoresCadastrados.inserir(chave, fornecedor);
            }

            System.out.println("Fornecedores carregados com sucesso!");

        } catch (IOException | NumberFormatException excecaoArquivo) {
            System.out.println("Erro ao ler arquivo de fornecedores: " + excecaoArquivo.getMessage());
            fornecedoresCadastrados = new AVL<>();
        } finally {
            if (arquivo != null) {
                arquivo.close();
            }
        }

        return fornecedoresCadastrados;
    }

    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K procurado) {

        Produto produto;

        cabecalho();
        System.out.println("Localizando um produto...");

        try {
            produto = produtosCadastrados.pesquisar(procurado);
        } catch (NoSuchElementException excecao) {
            produto = null;
        }

        if (produto != null) {
             System.out.println("Número de comparações realizadas: " + produtosCadastrados.getComparacoes());
             System.out.println("Tempo de processamento da pesquisa: " + produtosCadastrados.getTempo() + " ms");
        }

        return produto;
    }

    static Produto localizarProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
            System.out.println("Nenhum produto cadastrado. Carregue os produtos primeiro (opção 3).");
            return null;
        }
        Integer id = lerOpcao("Digite o ID do produto:", Integer.class);
        if (id == null)
            return null;
        return localizarProduto(produtosCadastrados, id);
    }

    static Produto localizarProdutoNome(ABB<String, Produto> produtosCadastrados) {
        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
            System.out.println("Nenhum produto cadastrado. Carregue os produtos primeiro (opção 2).");
            return null;
        }
        System.out.println("Digite o nome do produto:");
        String nome = teclado.nextLine();
        return localizarProduto(produtosCadastrados, nome);
    }

    // Tarefa 4: Relatório de fornecedor
    static void relatorioDeFornecedor() {
        if (fornecedoresPorDocumento == null || fornecedoresPorDocumento.vazia()) {
            System.out.println("Nenhum fornecedor cadastrado. Carregue os fornecedores primeiro (opção 6).");
            return;
        }

        Integer documento = lerOpcao("Digite o documento do fornecedor:", Integer.class);
        if (documento == null) {
            System.out.println("Documento inválido.");
            return;
        }

        try {
            Fornecedor fornecedor = fornecedoresPorDocumento.pesquisar(documento);
            cabecalho();
            System.out.println("\n=== RELATÓRIO DE FORNECEDOR ===\n");
            System.out.println(fornecedor.toString());
        } catch (NoSuchElementException e) {
            System.out.println("Fornecedor não encontrado.");
        }
    }

    // Tarefa 4: Fornecedores de um produto
    static void fornecedoresDoProduto() {
        if (produtosCadastradosPorId == null || produtosCadastradosPorId.vazia()) {
            System.out.println("Nenhum produto cadastrado. Carregue os produtos primeiro.");
            return;
        }
        
        // Verifica se a tabela hash foi inicializada
        if (produtosFornecedores == null) {
             System.out.println("Associações de fornecedores não carregadas. Execute a opção 6 primeiro.");
             return;
        }

        Integer idProduto = lerOpcao("Digite o ID do produto:", Integer.class);
        if (idProduto == null) {
            System.out.println("ID inválido.");
            return;
        }

        try {
            Produto produto = produtosCadastradosPorId.pesquisar(idProduto);
            Lista<Fornecedor> fornecedores = produtosFornecedores.pesquisar(idProduto);

            String nomeArquivo = "fornecedores_produto_" + idProduto + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
                writer.println("=== FORNECEDORES DO PRODUTO ===");
                writer.println("\nPRODUTO:");
                writer.println(produto.toString());
                writer.println("\n=== FORNECEDORES ===\n");

                if (fornecedores == null || fornecedores.vazia()) {
                    writer.println("Nenhum fornecedor cadastrado para este produto.");
                    System.out.println("Nenhum fornecedor encontrado para este produto.");
                } else {
                    writer.println(fornecedores.toString());
                    System.out.println("Relatório gerado com sucesso no arquivo: " + nomeArquivo);
                }

            } catch (IOException e) {
                System.out.println("Erro ao gerar arquivo de relatório: " + e.getMessage());
            }

        } catch (NoSuchElementException e) {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void mostrarProduto(Produto produto) {

        cabecalho();
        String mensagem = "Produto não localizado ou dados inválidos!";

        if (produto != null) {
            mensagem = String.format("Dados do produto:\n%s", produto);
        }

        System.out.println(mensagem);
    }

    static <K> void listarTodosOsProdutos(ABB<K, Produto> produtosCadastrados) {

        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");

        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
            System.out.println("Nenhum produto cadastrado nesta categoria de busca.");
        } else {
            System.out.println(produtosCadastrados.toString());
        }
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";

        int opcao = -1;

        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> {
                    // CORREÇÃO: Verifica qual árvore está disponível para exibição
                    if (produtosCadastradosPorNome != null && !produtosCadastradosPorNome.vazia()) {
                        listarTodosOsProdutos(produtosCadastradosPorNome);
                    } else if (produtosCadastradosPorId != null && !produtosCadastradosPorId.vazia()) {
                         listarTodosOsProdutos(produtosCadastradosPorId);
                    } else {
                         System.out.println("Nenhum produto cadastrado. Carregue os produtos primeiro (opção 2 ou 3).");
                    }
                }
                case 2 -> produtosCadastradosPorNome = lerProdutos(nomeArquivoDados, (p -> p.descricao));
                case 3 -> produtosCadastradosPorId = lerProdutos(nomeArquivoDados, (p -> p.idProduto));
                case 4 -> mostrarProduto(localizarProdutoNome(produtosCadastradosPorNome));
                case 5 -> mostrarProduto(localizarProdutoID(produtosCadastradosPorId));
                case 6 -> fornecedoresPorDocumento = lerFornecedores("fornecedores.txt", (f -> f.getDocumento()));
                case 7 -> relatorioDeFornecedor();
                case 8 -> fornecedoresDoProduto();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
            if (opcao != 0) pausa();
        } while (opcao != 0);

        teclado.close();
    }
}