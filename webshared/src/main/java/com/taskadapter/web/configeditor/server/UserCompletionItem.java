package com.taskadapter.web.configeditor.server;

public final class UserCompletionItem implements Comparable<UserCompletionItem> {
    public final String login;
    public final String password;
    public final boolean isPrimary;

    public UserCompletionItem(String login, String password, boolean isPrimary) {
        this.login = login;
        this.password = password;
        this.isPrimary = isPrimary;
    }

    @Override
    public int compareTo(UserCompletionItem o) {
        if (isPrimary && !o.isPrimary) {
            return -1;
        }
        if (!isPrimary && o.isPrimary) {
            return 1;
        }

        final int lcp = login.compareTo(o.login);
        if (lcp != 0) {
            return lcp;
        }
        if (this.login == o.login) {
            return 0;
        }
        if (this.login == null) {
            return -1;
        }
        if (o.login == null) {
            return 1;
        }
        return this.login.compareTo(o.login);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isPrimary ? 1231 : 1237);
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserCompletionItem other = (UserCompletionItem) obj;
        if (isPrimary != other.isPrimary)
            return false;
        if (login == null) {
            if (other.login != null)
                return false;
        } else if (!login.equals(other.login))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        return true;
    }
}
