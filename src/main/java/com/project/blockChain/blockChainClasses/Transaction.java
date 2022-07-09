package com.project.blockChain.blockChainClasses;

import java.util.Objects;

public class Transaction {
    private String fromId;
    private String toId;
    private int amount;
    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public int getAmount(){
        return amount;
    }

    public Transaction(String fromId, String toId, int amount) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != getClass()) return false;
        Transaction rhs = (Transaction) other;
        if (!Objects.equals(this.fromId, rhs.fromId) ||
                !Objects.equals(this.toId, rhs.toId) ||
                !Objects.equals(this.amount, rhs.amount))
            return false;
        return true;
    }
}
