package mlog.plugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_={@Inject})
public class PluginManager {

	Map<Class, List> cachedInstances = new HashMap<Class, List>();



	public List<LoggerPlugin> loadLoggerPlugins() {
		return loadSpiInstances(LoggerPlugin.class);
	}

	public List<LogParserFactory> loadLogParserFactories() {
		return loadSpiInstances(LogParserFactory.class);
	}


	public void wireUp(Object o) {

	}
	
	public <T> List<T> loadSpiInstances(Class<T> type) {
		if (cachedInstances.containsKey(type)) {
			return cachedInstances.get(type);
		}
		
		ServiceLoader<T> loader = ServiceLoader.load(type);
		List<T> result = new LinkedList<T>();
		loader.forEach(result::add);
		result.forEach(this::wireUp);
		
		cachedInstances.put(type, result);
		return result;
	}

	
	public <T extends Orderable> List<T> loadOrderedSpiInstances(Class<T> type) {
		if (cachedInstances.containsKey(type))
			return cachedInstances.get(type);
		
		ServiceLoader<T> loader = ServiceLoader.load(type);
		List<T> result = new LinkedList<T>();
		loader.forEach(result::add);
		
		result.forEach(this::wireUp);
		
		cachedInstances.put(type, result);
		
		result.sort((a,b) -> a.getOrder() - b.getOrder());
		
		return result;
	}

}
