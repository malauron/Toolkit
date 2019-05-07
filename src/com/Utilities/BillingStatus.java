package com.Utilities;

public enum BillingStatus {
    RECEIVED,
    PROCESSING,
    PROCESSED,
    CANCELLED;

    public String toString() {
        return this.name();
    }
}
