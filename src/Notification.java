public class Notification {
    Transaction transaction;
    static final int initTTL = 6;
    int TTL;
    byte[] signature;

    public Notification(Transaction transaction){
        this.transaction = transaction;
        this.TTL = initTTL;
    }

    public Notification(Transaction transaction, int initTTL){
        this.transaction = transaction;
        this.TTL = initTTL;
    }

    public void setSignature(byte[] sign){
        signature = sign;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Notification(this.transaction, this.TTL);
    }
}
