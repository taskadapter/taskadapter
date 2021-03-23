package com.taskadapter.connector.trello;

import com.julienvey.trello.domain.TList;

import java.util.List;
import java.util.Optional;

public class ListCache {
    private final List<TList> lists;

    public ListCache(List<TList> lists) {
        this.lists = lists;
    }

    Optional<String> getListIdByName(String listName) {
        return lists.stream().filter(list -> list.getName().equals(listName))
                .findFirst()
                .map(TList::getId);
    }

    Optional<String> getListNameById(String listId) {
        return lists.stream().filter(list -> list.getId().equals(listId))
                .findFirst()
                .map(TList::getName);
    }
}
