public class Lista<T> {
    
    private Celula<T> primeiro;
    private Celula<T> ultimo;
    private int tamanho;
    
    public Lista() {
        primeiro = new Celula<>();
        ultimo = primeiro;
        tamanho = 0;
    }
    
    public boolean vazia() {
        return (primeiro == ultimo);
    }
    
    public void inserir(T item) {
        Celula<T> nova = new Celula<>(item);
        ultimo.setProximo(nova);
        ultimo = nova;
        tamanho++;
    }
    
    public T remover() {
        if (vazia()) {
            throw new IllegalStateException("Lista vazia!");
        }
        
        Celula<T> removida = primeiro.getProximo();
        T item = removida.getItem();
        primeiro.setProximo(removida.getProximo());
        
        if (removida == ultimo) {
            ultimo = primeiro;
        }
        
        tamanho--;
        return item;
    }
    
    public int tamanho() {
        return tamanho;
    }
    
    @Override
    public String toString() {
        if (vazia()) {
            return "Lista vazia";
        }
        
        StringBuilder sb = new StringBuilder();
        Celula<T> atual = primeiro.getProximo();
        
        while (atual != null) {
            sb.append(atual.getItem().toString()).append("\n");
            atual = atual.getProximo();
        }
        
        return sb.toString();
    }
}