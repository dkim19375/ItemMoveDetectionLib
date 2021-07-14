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

import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PickedItems {
    private InventoryLoc loc;
    private ItemStack item;
    private Integer slot;
    private InventoryView view;

    public PickedItems(InventoryLoc loc, ItemStack item, Integer slot, InventoryView view) {
        this.loc = loc;
        this.item = item;
        this.slot = slot;
        this.view = view;
    }

    public InventoryLoc getLoc() {
        return loc;
    }

    public ItemStack getItem() {
        return item;
    }

    public Integer getSlot() {
        return slot;
    }

    public InventoryView getView() {
        return view;
    }

    public void setLoc(InventoryLoc loc) {
        this.loc = loc;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public void setView(InventoryView view) {
        this.view = view;
    }
}