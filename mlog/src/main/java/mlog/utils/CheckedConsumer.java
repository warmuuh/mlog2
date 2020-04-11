package mlog.utils;

@FunctionalInterface
public interface CheckedConsumer<T> {
	void apply(T t) throws Throwable;
}