package me.stella.core.tasks.scanners.io;

public class SearchQuery {

    private final SearchType type;
    private final Query<?> data;

    public SearchQuery(SearchType type, Query<?> data) {
        this.type = type;
        this.data = data;
    }

    public final SearchType getType() {
        return this.type;
    }

    public final Query<?> getData() {
        return this.data;
    }

    public enum SearchType {
        ITEM, ITEM_LOCKED, ITEM_MMOITEMS, ITEM_ENCHANT
    }

    public static class Query<T> {

        private final T data;

        public Query(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }

    }

}
