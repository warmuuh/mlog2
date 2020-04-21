package mlog.plugin.k8s;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KubectlCommand {

  private final String command;
  private final Map<String, List<String>> options;

  private final String executable = "/usr/local/bin/kubectl";

  Process execute() throws IOException {
    return execute(Optional.empty());
  }

  Process execute(Optional<String> exclusionFilter) throws IOException {
    //for now only support 1 argvalue
    String args = options.entrySet().stream()
        .filter(e -> exclusionFilter.stream().noneMatch(e.getKey()::equals))
        .map(e -> (e.getKey().length() == 1 ? "-" : "--") + e.getKey() + " " + e.getValue().stream().findFirst().orElse(""))
        .collect(Collectors.joining(" "));
    String commandLine = executable + " " + command + " " + args;
    log.info("Executing: {}", commandLine);
    return Runtime.getRuntime().exec(commandLine);
  }

}
