package mlog.utils;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

public class UrlUtils {

  public static Map<String, List<String>> splitQuery(String queryString) {
    return Arrays.stream(queryString.split("&"))
        .map(UrlUtils::splitQueryParameter)
        .collect(Collectors.groupingBy(
            SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
  }

  private static SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
    final int idx = it.indexOf("=");
    final String key = idx > 0 ? it.substring(0, idx) : it;
    final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
    return new SimpleImmutableEntry<>(key, value);
  }
}
