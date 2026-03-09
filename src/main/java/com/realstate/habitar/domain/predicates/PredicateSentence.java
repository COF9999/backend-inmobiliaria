package com.realstate.habitar.domain.predicates;

import java.util.Objects;
import java.util.function.Predicate;

public interface PredicateSentence<T> {

    boolean test(T value);

    default PredicateSentence<T> and(PredicateSentence<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    default PredicateSentence<T> negate() {
        return (t) -> !test(t);
    }


    default PredicateSentence<T> or(PredicateSentence<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

    static <T> PredicateSentence<T> isEqual(Object targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : (a)-> targetRef.equals(targetRef);
    }

    static <T extends Integer> Predicate<T> isLess(T targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : object -> targetRef.compareTo(object) < 0;
    }
}
