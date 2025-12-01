import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ABB<K, V> implements IMapeamento<K, V>{

	private No<K, V> raiz; // referência à raiz da árvore.
	private Comparator<K> comparador; //comparador empregado para definir "menores" e "maiores".
	private int tamanho;
	private long comparacoes;
	private long inicio;
	private long termino;
	
	/**
	 * Método auxiliar para inicialização da árvore binária de busca.
	 * 
	 * Este método define a raiz da árvore como {@code null} e seu tamanho como 0.
	 * Utiliza o comparador fornecido para definir a organização dos elementos na árvore.
	 * @param comparador o comparador para organizar os elementos da árvore.
	 */
	private void init(Comparator<K> comparador) {
		raiz = null;
		tamanho = 0;
		this.comparador = comparador;
	}

	/**
	 * Construtor da classe.
	 * O comparador padrão de ordem natural será utilizado.
	 */ 
	@SuppressWarnings("unchecked")
	public ABB() {
	    init((Comparator<K>) Comparator.naturalOrder());
	}

	/**
	 * Construtor da classe.
	 * Esse construtor cria uma nova árvore binária de busca vazia.
	 *  
	 * @param comparador o comparador a ser utilizado para organizar os elementos da árvore.  
	 */
	public ABB(Comparator<K> comparador) {
	    init(comparador);
	}

    /**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária a partir de uma outra árvore binária de busca,
     * com os mesmos itens, mas usando uma nova chave.
     * @param original a árvore binária de busca original.
     * @param funcaoChave a função que irá extrair a nova chave de cada item para a nova árvore.
     */
    public ABB(ABB<?, V> original, Function<V, K> funcaoChave) {
        ABB<K, V> nova = new ABB<>();
        nova = copiarArvore(original.raiz, funcaoChave, nova);
        this.raiz = nova.raiz;
    }
    
    /**
     * Recursivamente, copia os elementos da árvore original para esta, num processo análogo ao caminhamento em ordem.
     * @param <T> Tipo da nova chave.
     * @param raizArvore raiz da árvore original que será copiada.
     * @param funcaoChave função extratora da nova chave para cada item da árvore.
     * @param novaArvore Nova árvore. Parâmetro usado para permitir o retorno da recursividade.
     * @return A nova árvore com os itens copiados e usando a chave indicada pela função extratora.
     */
    private <T> ABB<T, V> copiarArvore(No<?, V> raizArvore, Function<V, T> funcaoChave, ABB<T, V> novaArvore) {
    	
        if (raizArvore != null) {
    		novaArvore = copiarArvore(raizArvore.getEsquerda(), funcaoChave, novaArvore);
            V item = raizArvore.getItem();
            T chave = funcaoChave.apply(item);
    		novaArvore.inserir(chave, item);
    		novaArvore = copiarArvore(raizArvore.getDireita(), funcaoChave, novaArvore);
    	}
        return novaArvore;
    }
    
    /**
	 * Método booleano que indica se a árvore está vazia ou não.
	 * @return
	 * verdadeiro: se a raiz da árvore for null, o que significa que a árvore está vazia.
	 * falso: se a raiz da árvore não for null, o que significa que a árvore não está vazia.
	 */
	public Boolean vazia() {
	    return (this.raiz == null);
	}
    
    @Override
    /**
     * Método que encapsula a pesquisa recursiva de itens na árvore.
     * @param chave a chave do item que será pesquisado na árvore.
     * @return o valor associado à chave.
     */
	public V pesquisar(K chave) {
    	comparacoes = 0;
    	inicio = System.nanoTime();
    	V procurado = pesquisar(raiz, chave);
    	termino = System.nanoTime();
    	return procurado;
	}
    
    private V pesquisar(No<K, V> raizArvore, K procurado) {
    	
    	int comparacao;
    	
    	comparacoes++;
    	if (raizArvore == null)
    		/// Se a raiz da árvore ou sub-árvore for null, a árvore/sub-árvore está vazia e então o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	
    	comparacao = comparador.compare(procurado, raizArvore.getChave());
    	
    	if (comparacao == 0)
    		/// O item procurado foi encontrado.
    		return raizArvore.getItem();
    	else if (comparacao < 0)
    		/// Se o item procurado for menor do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore esquerda.    
    		return pesquisar(raizArvore.getEsquerda(), procurado);
    	else
    		/// Se o item procurado for maior do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore direita.
    		return pesquisar(raizArvore.getDireita(), procurado);
    }
    
    @Override
    /**
     * Método que encapsula a adição recursiva de itens à árvore, associando-o à chave fornecida.
     * @param chave a chave associada ao item que será inserido na árvore.
     * @param item o item que será inserido na árvore.
     * 
     * @return o tamanho atualizado da árvore após a execução da operação de inserção.
     */
    public int inserir(K chave, V item) {
    	raiz = inserir(raiz, chave, item);
        return tamanho;
    }
    
    /**
     * Método recursivo auxiliar para inserção de itens na árvore.
     * @param raizArvore raiz da árvore ou subárvore atual.
     * @param chave chave do item a ser inserido.
     * @param item item a ser inserido.
     * @return referência à raiz da árvore/subárvore após a inserção.
     */
    private No<K, V> inserir(No<K, V> raizArvore, K chave, V item) {
        
        if (raizArvore == null) {
            // Árvore vazia: cria um novo nó e incrementa o tamanho
            tamanho++;
            return new No<>(chave, item);
        }
        
        int comparacao = comparador.compare(chave, raizArvore.getChave());
        
        if (comparacao < 0) {
            // Chave menor: insere na subárvore esquerda
            raizArvore.setEsquerda(inserir(raizArvore.getEsquerda(), chave, item));
        } else if (comparacao > 0) {
            // Chave maior: insere na subárvore direita
            raizArvore.setDireita(inserir(raizArvore.getDireita(), chave, item));
        } else {
            // Chave já existe: atualiza o item
            raizArvore.setItem(item);
        }
        
        return raizArvore;
    }

    @Override 
    public String toString(){
    	return percorrer();
    }

    @Override
    public String percorrer() {
    	return caminhamentoEmOrdem();
    }

    public String caminhamentoEmOrdem() {
    	return caminhamentoEmOrdem(raiz);
    }
    
    /**
     * Método recursivo auxiliar para realizar o caminhamento em ordem.
     * @param raizArvore raiz da árvore ou subárvore atual.
     * @return String com os itens da árvore em ordem.
     */
    private String caminhamentoEmOrdem(No<K, V> raizArvore) {
        
        if (raizArvore == null) {
            return "";
        }
        
        String resultado = "";
        
        // Percorre a subárvore esquerda
        resultado += caminhamentoEmOrdem(raizArvore.getEsquerda());
        
        // Visita a raiz
        resultado += raizArvore.getItem().toString() + "\n";
        
        // Percorre a subárvore direita
        resultado += caminhamentoEmOrdem(raizArvore.getDireita());
        
        return resultado;
    }

    @Override
    /**
     * Método que encapsula a remoção recursiva de um item da árvore.
     * @param chave a chave do item que deverá ser localizado e removido da árvore.
     * @return o valor associado ao item removido.
     */
    public V remover(K chave) {
    	@SuppressWarnings("unchecked")
        V[] itemRemovido = (V[]) new Object[1];
        raiz = remover(raiz, chave, itemRemovido);
        return itemRemovido[0];
    }
    
    /**
     * Método recursivo auxiliar para remoção de itens da árvore.
     * @param raizArvore raiz da árvore ou subárvore atual.
     * @param chave chave do item a ser removido.
     * @param itemRemovido array auxiliar para retornar o item removido.
     * @return referência à raiz da árvore/subárvore após a remoção.
     */
    private No<K, V> remover(No<K, V> raizArvore, K chave, V[] itemRemovido) {
        
        if (raizArvore == null) {
            throw new NoSuchElementException("O item não foi localizado na árvore!");
        }
        
        int comparacao = comparador.compare(chave, raizArvore.getChave());
        
        if (comparacao < 0) {
            // Chave menor: remove da subárvore esquerda
            raizArvore.setEsquerda(remover(raizArvore.getEsquerda(), chave, itemRemovido));
        } else if (comparacao > 0) {
            // Chave maior: remove da subárvore direita
            raizArvore.setDireita(remover(raizArvore.getDireita(), chave, itemRemovido));
        } else {
            // Item encontrado: processa a remoção
            itemRemovido[0] = raizArvore.getItem();
            tamanho--;
            
            // Caso 1: Nó sem filhos ou com apenas um filho
            if (raizArvore.getEsquerda() == null) {
                return raizArvore.getDireita();
            } else if (raizArvore.getDireita() == null) {
                return raizArvore.getEsquerda();
            }
            
            // Caso 2: Nó com dois filhos
            // Encontra o menor nó da subárvore direita (sucessor)
            No<K, V> sucessor = encontrarMenor(raizArvore.getDireita());
            
            // Substitui os dados do nó atual pelos dados do sucessor
            raizArvore.setChave(sucessor.getChave());
            raizArvore.setItem(sucessor.getItem());
            
            // Remove o sucessor da subárvore direita
            @SuppressWarnings("unchecked")
            V[] temp = (V[]) new Object[1];
            raizArvore.setDireita(remover(raizArvore.getDireita(), sucessor.getChave(), temp));
        }
        
        return raizArvore;
    }
    
    /**
     * Encontra o menor nó de uma subárvore (nó mais à esquerda).
     * @param raizArvore raiz da subárvore.
     * @return o menor nó da subárvore.
     */
    private No<K, V> encontrarMenor(No<K, V> raizArvore) {
        while (raizArvore.getEsquerda() != null) {
            raizArvore = raizArvore.getEsquerda();
        }
        return raizArvore;
    }

	@Override
	public int tamanho() {
		return tamanho;
	}
	
	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000.0;
	}
}