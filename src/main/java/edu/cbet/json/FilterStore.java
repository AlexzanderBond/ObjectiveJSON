package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterStore {
    private final HashMap<Class<?>, TypedFilters> filterMap;

    public FilterStore() {
        this.filterMap = new HashMap<>();
    }

    public List<Filter> getFilters(Class<?> clazz) {
        TypedFilters filters = filterMap.get(clazz);

        return filters==null?List.of():List.copyOf((filters).getFilters());
    }

    public void addFilter(Class<?> clazz, Filter filter) {
        TypedFilters filters =  filterMap.get(clazz);

        if(filters == null) {
            filters = new TypedFilters();

            filterMap.put(clazz, filters);
        }

        filters.getFilters().add(filter);
    }

    private static class TypedFilters {
        private final List<Filter> filters;

        public TypedFilters() {
            this.filters = new ArrayList<>();
        }

        public List<Filter> getFilters() {
            return filters;
        }
    }
}
