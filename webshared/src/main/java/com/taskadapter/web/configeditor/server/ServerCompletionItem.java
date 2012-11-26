package com.taskadapter.web.configeditor.server;

/**
 * Server completion item.
 * 
 */
final class ServerCompletionItem implements Comparable<ServerCompletionItem>{
    final String url;
    final String login;

    ServerCompletionItem(String url, String login) {
        this.url = url;
        this.login = login;
    }

    @Override
    public int compareTo(ServerCompletionItem o) {
        final int ucor = this.url.compareTo(o.url);
        if (ucor != 0) {
            return ucor;
        }
        if (this.login == o.login) {
            return 0;
        }
        if (this.login == null && o.login != null) {
            return -1;
        }
        if (o.login == null && this.login != null) {
            return 1;
        }
        return this.login.compareTo(o.login);
    }

}
