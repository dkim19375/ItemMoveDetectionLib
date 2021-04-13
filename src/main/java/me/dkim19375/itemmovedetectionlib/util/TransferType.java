package me.dkim19375.itemmovedetectionlib.util;

/**
 * An enum for different types of item transfering
 */
public enum TransferType {
    /**
     * When a player puts an item from a separate inventory such as a chest into their own inventory
     */
    PUT_OTHER(true, true),
    /**
     * When a player puts an item from their own inventory and puts it in a separate inventory such as a chest
     */
    PUT_SELF(false, true),
    /**
     * When a player drops an item from a separate inventory such as a chest
     */
    DROP_OTHER(true, false),
    /**
     * When a player drops an item from their own inventory
     */
    DROP_SELF(false, false);

    /**
     * True if the item is from a separate inventory such as a chest
     * False if the item is from the player's own inventory
     */
    private final boolean other;
    /**
     * True if the item is put down in a different inventory
     * False if the item was dropped
     */
    private final boolean put;

    TransferType(boolean other, boolean put) {
        this.other = other;
        this.put = put;
    }

    public boolean isOther() {
        return other;
    }

    public boolean isPut() {
        return put;
    }
}
