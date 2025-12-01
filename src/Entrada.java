public class Entrada<K, V> {
    
    private K chave;
    private V valor;
    
    public Entrada(K chave, V valor) {
        this.chave = chave;
        this.valor = valor;
    }
    
    public K getChave() {
        return chave;
    }
    
    public void setChave(K chave) {
        this.chave = chave;
    }
    
    public V getValor() {
        return valor;
    }
    
    public void setValor(V valor) {
        this.valor = valor;
    }
    
    @Override
    public String toString() {
        return chave + " -> " + valor;
    }
}