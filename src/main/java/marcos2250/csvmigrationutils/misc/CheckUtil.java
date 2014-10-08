package marcos2250.csvmigrationutils.misc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.Sets;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CheckUtil {

    public static <O> Collection<O> checkedCollection(Collection colecao, Class<O> classe) {
        return Collections.checkedCollection(colecao, classe);
    }

    public static <O> List<O> checkedList(List lista, Class<O> classe) {
        return Collections.checkedList(lista, classe);
    }

    public static <O> Set<O> checkedSet(Set set, Class<O> classe) {
        return Collections.checkedSet(set, classe);
    }

    public static <O> SortedSet<O> checkedSortedSet(SortedSet sortedSet, Class<O> classe) {
        return Collections.checkedSortedSet(sortedSet, classe);
    }

    public static <O> Set<O> toSet(Collection colecao) {
        return Sets.newHashSet(colecao);
    }

    public static <O> Set<O> toCheckedSet(List<?> lista, Class<O> classe) {
        return Sets.newHashSet(checkedList(lista, classe));
    }

    public static <O extends Comparable<O>> SortedSet<O> toSortedSet(Collection colecao) {
        return Sets.newTreeSet(colecao);
    }

    public static <O> O uncheckedCast(Object obj) {
        return (O) obj;
    }
}
