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

import org.jetbrains.annotations.NotNull;

/**
 * An enum for different types of item transferring
 */
public enum TransferType {
    /**
     * When a player puts an item from a separate inventory such as a chest into their own inventory
     */
    PUT_OTHER_SELF(true, false, true, false, false),
    /**
     * When a player puts an item from their own inventory and puts it in a separate inventory such as a chest
     */
    PUT_SELF_OTHER(false, true, true, false, false),
    /**
     * When a player puts an item from their own inventory and puts it their crafting menu
     */
    PUT_SELF_CRAFTING(false, false, true, false, true),
    /**
     * When a player puts an item from their crafting menu and puts it their own inventory
     */
    PUT_CRAFTING_SELF(false, false, true, true, false),
    /**
     * When a player drops an item from a separate inventory such as a chest
     */
    DROP_OTHER(true, false, false, false, false),
    /**
     * When a player drops an item from their own inventory
     */
    DROP_SELF(false, false, false, false, false),
    /**
     * When a player puts an item from their crafting menu and puts it a separate inventory
     */
    DROP_CRAFTING(false, false, false, true, false);

    private final boolean fromOther;
    private final boolean toOther;
    private final boolean put;
    private final boolean fromCrafting;
    private final boolean toCrafting;

    TransferType(boolean fromOther, boolean toOther, boolean put, boolean fromCrafting, boolean toCrafting) {
        this.fromOther = fromOther;
        this.toOther = toOther;
        this.put = put;
        this.fromCrafting = fromCrafting;
        this.toCrafting = toCrafting;
    }

    /**
     * @return True if the item is from a separate inventory such as a chest
     */
    public boolean isFromOther() {
        return fromOther;
    }
    /**
     * @return True if the item is to a separate inventory such as a chest
     */
    public boolean isToOther() {
        return toOther;
    }

    /**
     * @return True if the item is put down in a different inventory, False if the item was dropped
     */
    public boolean isPut() {
        return put;
    }

    /**
     * @return True if the item is from a crafting menu, False if the item was not from a crafting menu
     */
    public boolean isFromCrafting() {
        return fromCrafting;
    }

    /**
     * @return True if the item is from a crafting menu, False if the item was not from a crafting menu
     */
    public boolean isToCrafting() {
        return toCrafting;
    }

    /**
     * @return True if the item is from their own inventory, False if the item was not from their own inventory
     */
    public boolean isFromSelf() {
        return !fromCrafting && !fromOther;
    }

    /**
     * @return True if the item is from their own inventory, False if the item was not from their own inventory
     */
    public boolean isToSelf() {
        return !toCrafting && !toOther;
    }

    @NotNull
    public TransferType applyCrafting(boolean crafting) {
        if (!crafting) {
            return this;
        }
        switch (this) {
            case PUT_OTHER_SELF:
            case PUT_CRAFTING_SELF: {
                return PUT_CRAFTING_SELF;
            }
            case PUT_SELF_OTHER:
            case PUT_SELF_CRAFTING: {
                return PUT_SELF_CRAFTING;
            }
            case DROP_OTHER:
            case DROP_CRAFTING: {
                return DROP_CRAFTING;
            }
            case DROP_SELF: {
                return DROP_SELF;
            }
            default: {
                throw new IllegalStateException("Went through all types in switch!");
            }
        }
    }
}