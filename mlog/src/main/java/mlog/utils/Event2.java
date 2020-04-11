package mlog.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.SneakyThrows;

public class Event2<T1, T2> {

	
	List<BiConsumer<T1, T2>> listeners = new ArrayList<>();
	

	
	public void add(BiConsumer<T1, T2> listener) {
		listeners.add(listener);
	}

	public void remove(BiConsumer<T1, T2> listener) {
		listeners.remove(listener);
	}

	
	@SneakyThrows
	public void invoke(T1 payload1, T2 payload2) {
		for(var listener : listeners) {
			listener.accept(payload1, payload2);
		}
	}
	
}
