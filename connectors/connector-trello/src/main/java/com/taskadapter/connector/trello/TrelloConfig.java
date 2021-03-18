package com.taskadapter.connector.trello;

import com.taskadapter.connector.definition.ConnectorConfig;

import java.util.HashMap;
import java.util.Map;

public class TrelloConfig extends ConnectorConfig {

    private static final Map<String, Integer> DEFAULT_PRIORITIES = new HashMap<>();

    static {
        DEFAULT_PRIORITIES.put("Low", 100);
        DEFAULT_PRIORITIES.put("Normal", 500);
        DEFAULT_PRIORITIES.put("High", 700);
        DEFAULT_PRIORITIES.put("Urgent", 800);
        DEFAULT_PRIORITIES.put("Immediate", 1000);
    }

    private String boardId;
    private String boardName;

    public TrelloConfig() {
        super(DEFAULT_PRIORITIES);
    }

    public String getBoardId() {
        return boardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }
}
