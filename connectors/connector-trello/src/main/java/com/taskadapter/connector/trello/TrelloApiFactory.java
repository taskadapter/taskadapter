package com.taskadapter.connector.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;

public class TrelloApiFactory {
    static Trello createApi(String appKey, String token) {
        var a = new ApacheHttpClient();
        return new TrelloImpl(appKey, token, a);
    }
}
