import java.util.NoSuchElementException;

public class TabelaHash<K, V> implements IMapeamento<K, V> {
    
    private Lista<Entrada<K, V>>[] tabela;
    private int tamanho;
    private int capacidade;
    private long comparacoes;
    private long inicio;
    private long termino;
    
    @SuppressWarnings("unchecked")
    public TabelaHash(int capacidade) {
        this.capacidade = capacidade;
        this.tamanho = 0;
        this.tabela = new Lista[capacidade];
        
        for (int i = 0; i < capacidade; i++) {
            tabela[i] = new Lista<>();
        }
    }
    
    public TabelaHash() {
        this(101); // Capacidade padrão
    }
    
    private int hash(K chave) {
        return Math.abs(chave.hashCode() % capacidade);
    }
    
    @Override
    public int inserir(K chave, V item) {
        int indice = hash(chave);
        Lista<Entrada<K, V>> lista = tabela[indice];
        
        // Verifica se a chave já existe
        Celula<Entrada<K, V>> atual = lista.vazia() ? null : obterPrimeiraCelula(lista);
        while (atual != null) {
            if (atual.getItem().getChave().equals(chave)) {
                atual.getItem().setValor(item);
                return tamanho;
            }
            atual = atual.getProximo();
        }
        
        // Insere nova entrada
        lista.inserir(new Entrada<>(chave, item));
        tamanho++;
        return tamanho;
    }
    
    @Override
    public V pesquisar(K chave) {
        comparacoes = 0;
        inicio = System.nanoTime();
        
        int indice = hash(chave);
        Lista<Entrada<K, V>> lista = tabela[indice];
        
        if (lista.vazia()) {
            termino = System.nanoTime();
            return null;
        }
        
        Celula<Entrada<K, V>> atual = obterPrimeiraCelula(lista);
        while (atual != null) {
            comparacoes++;
            if (atual.getItem().getChave().equals(chave)) {
                termino = System.nanoTime();
                return atual.getItem().getValor();
            }
            atual = atual.getProximo();
        }
        
        termino = System.nanoTime();
        return null;
    }
    
    @Override
    public V remover(K chave) {
        int indice = hash(chave);
        Lista<Entrada<K, V>> lista = tabela[indice];
        
        if (lista.vazia()) {
            throw new NoSuchElementException("Chave não encontrada!");
        }
        
        Celula<Entrada<K, V>> anterior = null;
        Celula<Entrada<K, V>> atual = obterPrimeiraCelula(lista);
        
        while (atual != null) {
            if (atual.getItem().getChave().equals(chave)) {
                V valor = atual.getItem().getValor();
                
                if (anterior == null) {
                    lista.remover();
                } else {
                    anterior.setProximo(atual.getProximo());
                }
                
                tamanho--;
                return valor;
            }
            anterior = atual;
            atual = atual.getProximo();
        }
        
        throw new NoSuchElementException("Chave não encontrada!");
    }
    
    @Override
    public int tamanho() {
        return tamanho;
    }
    
    @Override
    public String percorrer() {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < capacidade; i++) {
            if (!tabela[i].vazia()) {
                Celula<Entrada<K, V>> atual = obterPrimeiraCelula(tabela[i]);
                while (atual != null) {
                    sb.append(atual.getItem().toString()).append("\n");
                    atual = atual.getProximo();
                }
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public long getComparacoes() {
        return comparacoes;
    }
    
    @Override
    public double getTempo() {
        return (termino - inicio) / 1_000_000.0;
    }
    
    @Override
    public String toString() {
        return percorrer();
    }
    
    // Método auxiliar para acessar a primeira célula de uma lista
    private Celula<Entrada<K, V>> obterPrimeiraCelula(Lista<Entrada<K, V>> lista) {
        // Como Lista tem uma célula cabeça, pegamos a próxima
        try {
            // Usa reflexão para acessar o campo 'primeiro'
            java.lang.reflect.Field campo = Lista.class.getDeclaredField("primeiro");
            campo.setAccessible(true);
            Celula<Entrada<K, V>> primeiro = (Celula<Entrada<K, V>>) campo.get(lista);
            return primeiro.getProximo();
        } catch (Exception e) {
            return null;
        }
    }
}
