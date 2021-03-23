package com.taskadapter.connector.trello;

import com.julienvey.trello.domain.Board;
import com.taskadapter.http.HttpCaller;

import java.io.IOException;
import java.util.List;

public class TrelloClient {
    private final String credentials;
    private final String baseUrl;

    public TrelloClient(String key, String token) {
        credentials = "key=" + key + "&token=" + token;
        baseUrl = "https://api.trello.com/1";
    }

    Board createBoard(String boardName) throws IOException {
        // TODO 14 name with spaces??
        var createBoardUrl = baseUrl + "/boards?name=" + boardName + "&" + credentials;
        var board = HttpCaller.post(createBoardUrl, Board.class);
        return board;
    }

    Board closeBoard(String boardId) throws IOException {
        var url = baseUrl + "/boards/" + boardId + "?closed=true&" + credentials;
        var board = HttpCaller.put(url, Board.class);
        return board;
    }

    public List<Board> getBoards(String memberIdOrLogin) {
        var url = baseUrl + "/members/" + memberIdOrLogin + "/boards?" + credentials;
        try {
            return HttpCaller.get(url, List.class);
        } catch (IOException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }
}
