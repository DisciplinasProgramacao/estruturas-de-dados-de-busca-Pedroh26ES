import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class AVL<K, V> implements IMapeamento<K, V> {

    private No<K, V> raiz;
    private Comparator<K> comparador;
    private int tamanho;
    private long comparacoes;
    private long inicio;
    private long termino;

    private void init(Comparator<K> comparador) {
        raiz = null;
        tamanho = 0;
        this.comparador = comparador;
    }

    @SuppressWarnings("unchecked")
    public AVL() {
        init((Comparator<K>) Comparator.naturalOrder());
    }

    public AVL(Comparator<K> comparador) {
        init(comparador);
    }

    public Boolean vazia() {
        return (this.raiz == null);
    }

    @Override
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
            throw new NoSuchElementException("O item não foi localizado na árvore!");
        
        comparacao = comparador.compare(procurado, raizArvore.getChave());
        
        if (comparacao == 0)
            return raizArvore.getItem();
        else if (comparacao < 0)
            return pesquisar(raizArvore.getEsquerda(), procurado);
        else
            return pesquisar(raizArvore.getDireita(), procurado);
    }

    @Override
    public int inserir(K chave, V item) {
        raiz = inserir(raiz, chave, item);
        return tamanho;
    }

    private No<K, V> inserir(No<K, V> raizArvore, K chave, V item) {
        if (raizArvore == null) {
            tamanho++;
            return new No<>(chave, item);
        }

        int comparacao = comparador.compare(chave, raizArvore.getChave());

        if (comparacao < 0) {
            raizArvore.setEsquerda(inserir(raizArvore.getEsquerda(), chave, item));
        } else if (comparacao > 0) {
            raizArvore.setDireita(inserir(raizArvore.getDireita(), chave, item));
        } else {
            raizArvore.setItem(item);
            return raizArvore;
        }

        raizArvore.setAltura();
        return balancear(raizArvore);
    }

    private No<K, V> balancear(No<K, V> raizArvore) {
        int fatorBalanceamento = raizArvore.getFatorBalanceamento();

        // Rotação à direita
        if (fatorBalanceamento > 1) {
            if (raizArvore.getEsquerda().getFatorBalanceamento() < 0) {
                raizArvore.setEsquerda(rotacionarEsquerda(raizArvore.getEsquerda()));
            }
            return rotacionarDireita(raizArvore);
        }

        // Rotação à esquerda
        if (fatorBalanceamento < -1) {
            if (raizArvore.getDireita().getFatorBalanceamento() > 0) {
                raizArvore.setDireita(rotacionarDireita(raizArvore.getDireita()));
            }
            return rotacionarEsquerda(raizArvore);
        }

        return raizArvore;
    }

    private No<K, V> rotacionarDireita(No<K, V> no) {
        No<K, V> novaRaiz = no.getEsquerda();
        No<K, V> temp = novaRaiz.getDireita();

        novaRaiz.setDireita(no);
        no.setEsquerda(temp);

        no.setAltura();
        novaRaiz.setAltura();

        return novaRaiz;
    }

    private No<K, V> rotacionarEsquerda(No<K, V> no) {
        No<K, V> novaRaiz = no.getDireita();
        No<K, V> temp = novaRaiz.getEsquerda();

        novaRaiz.setEsquerda(no);
        no.setDireita(temp);

        no.setAltura();
        novaRaiz.setAltura();

        return novaRaiz;
    }

    @Override
    public V remover(K chave) {
        @SuppressWarnings("unchecked")
        V[] itemRemovido = (V[]) new Object[1];
        raiz = remover(raiz, chave, itemRemovido);
        return itemRemovido[0];
    }
    
    @SuppressWarnings("unchecked")
    private No<K, V> remover(No<K, V> raizArvore, K chave, V[] itemRemovido) {
        if (raizArvore == null)
            throw new NoSuchElementException("O item não foi localizado na árvore!");
    
        int comparacao = comparador.compare(chave, raizArvore.getChave());
    
        if (comparacao < 0) {
            raizArvore.setEsquerda(remover(raizArvore.getEsquerda(), chave, itemRemovido));
        } else if (comparacao > 0) {
            raizArvore.setDireita(remover(raizArvore.getDireita(), chave, itemRemovido));
        } else {
            itemRemovido[0] = raizArvore.getItem();
            tamanho--;
    
            if (raizArvore.getEsquerda() == null) {
                return raizArvore.getDireita();
            } else if (raizArvore.getDireita() == null) {
                return raizArvore.getEsquerda();
            } else {
                No<K, V> substituto = encontrarMenor(raizArvore.getDireita());
                raizArvore.setChave(substituto.getChave());
                raizArvore.setItem(substituto.getItem());
                @SuppressWarnings("unchecked")
                V[] temp = (V[]) new Object[1];
                raizArvore.setDireita(remover(raizArvore.getDireita(), substituto.getChave(), temp));
            }
        }
    
        raizArvore.setAltura();
        return balancear(raizArvore);
    }

    private No<K, V> encontrarMenor(No<K, V> raizArvore) {
        while (raizArvore.getEsquerda() != null) {
            raizArvore = raizArvore.getEsquerda();
        }
        return raizArvore;
    }

    @Override
    public String percorrer() {
        return caminhamentoEmOrdem();
    }

    public String caminhamentoEmOrdem() {
        return caminhamentoEmOrdem(raiz);
    }

    private String caminhamentoEmOrdem(No<K, V> raizArvore) {
        if (raizArvore == null)
            return "";

        String resultado = "";
        resultado += caminhamentoEmOrdem(raizArvore.getEsquerda());
        resultado += raizArvore.getItem().toString() + "\n";
        resultado += caminhamentoEmOrdem(raizArvore.getDireita());

        return resultado;
    }

    @Override
    public String toString() {
        return percorrer();
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