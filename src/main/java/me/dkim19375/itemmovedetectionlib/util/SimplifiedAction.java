package me.dkim19375.itemmovedetectionlib.util;

import org.bukkit.event.inventory.InventoryAction;

@SuppressWarnings("unused")
public enum SimplifiedAction {
    NOTHING,
    PICKUP,
    PLACE,
    SWAP_WITH_CURSOR,
    DROP_CURSOR,
    DROP_SLOT,
    MOVE_TO_OTHER_INVENTORY,
    HOTBAR_MOVE_AND_READD,
    HOTBAR_SWAP,
    CLONE_STACK,
    COLLECT_TO_CURSOR,
    UNKNOWN;

    public static SimplifiedAction fromAction(InventoryAction action) {
        switch (action) {
            case NOTHING:
                return NOTHING;
            case PICKUP_ALL:
            case PICKUP_ONE:
            case PICKUP_HALF:
            case PICKUP_SOME:
                return PICKUP;
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
                return PLACE;
            case SWAP_WITH_CURSOR:
                return SWAP_WITH_CURSOR;
            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
                return DROP_CURSOR;
            case DROP_ALL_SLOT:
            case DROP_ONE_SLOT:
                return DROP_SLOT;
            case MOVE_TO_OTHER_INVENTORY:
                return MOVE_TO_OTHER_INVENTORY;
            case HOTBAR_MOVE_AND_READD:
                return HOTBAR_MOVE_AND_READD;
            case HOTBAR_SWAP:
                return HOTBAR_SWAP;
            case CLONE_STACK:
                return CLONE_STACK;
            case COLLECT_TO_CURSOR:
                return COLLECT_TO_CURSOR;
            case UNKNOWN:
                return UNKNOWN;
            default:
                return null;
        }
    }
}
