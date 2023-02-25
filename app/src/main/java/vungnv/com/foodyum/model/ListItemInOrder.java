package vungnv.com.foodyum.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListItemInOrder {
    public String idMerchant;
    public String quantity;
    public String name;
    public String price;
    public String notes;

    public ListItemInOrder() {
    }

    public ListItemInOrder(String idMerchant, String quantity, String name, String price, String notes) {
        this.idMerchant = idMerchant;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.notes = notes;
    }

    @NonNull
    @Override
    public String toString() {
        return "ListItemInOrder{" +
                "idMerchant='" + idMerchant + '\'' +
                ", quantity=" + quantity +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
    public List<ListItemInOrder> mergeListsByIdMerchant(List<ListItemInOrder> lists) {
        // Create a HashMap to group the items by idMerchant
        HashMap<String, List<ListItemInOrder>> itemsByMerchant = new HashMap<>();
        for (ListItemInOrder item : lists) {
            if (!itemsByMerchant.containsKey(item.idMerchant)) {
                itemsByMerchant.put(item.idMerchant, new ArrayList<>());
            }
            itemsByMerchant.get(item.idMerchant).add(item);
        }

        // Merge the lists for each idMerchant into a single list
        List<ListItemInOrder> mergedLists = new ArrayList<>();
        for (List<ListItemInOrder> items : itemsByMerchant.values()) {
            ListItemInOrder mergedItem = items.get(0);
            for (int i = 1; i < items.size(); i++) {
                ListItemInOrder item = items.get(i);
                mergedItem.quantity = mergedItem.quantity + "-" + item.quantity;
                mergedItem.price = mergedItem.price + "-" + item.price;
            }
            mergedLists.add(mergedItem);
        }

        return mergedLists;
    }
}
