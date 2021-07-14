/*
 * MIT License
 *
 * Copyright (c) 2021 dkim19375
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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