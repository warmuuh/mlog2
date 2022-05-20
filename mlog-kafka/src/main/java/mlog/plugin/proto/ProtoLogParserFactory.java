package mlog.plugin.proto;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protocjar.Protoc;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.val;
import mlog.ctrl.rt.logging.LogParser;
import mlog.plugin.LogParserFactory;
import mlog.plugin.proto.ProtocInvoker.ProtocInvocationException;
import org.apache.commons.io.IOUtils;

public class ProtoLogParserFactory implements LogParserFactory {

  @Override
  public String getLogFormatName() {
    return "Protobuf";
  }

  @Override
  @SneakyThrows
  public LogParser create(String channelName, String loggingFormatConfig, Map<String, String> additionalConfig) {
    String schemaFile = additionalConfig.get("schema");
    if (schemaFile == null) {
      throw new IllegalArgumentException("'schema' attribute is missing from protobuf logger additional config");
    }
    String eventType = additionalConfig.get("type");
    if (eventType == null) {
      throw new IllegalArgumentException("'type' attribute is missing from protobuf logger additional config");
    }

    DynamicSchema schema = loadSchema(schemaFile);
    return new ProtoLogParser(channelName, schema.getMessageDescriptor(eventType));
  }

  private DynamicSchema loadSchema(String schemaFile)
      throws ProtocInvocationException, FileNotFoundException, DescriptorValidationException {
    FileDescriptorSet set = ProtocInvoker.forStream(new FileInputStream(schemaFile)).invoke();
    DynamicSchema schema = new DynamicSchema(set);
    return schema;
  }

  @Override
  @SneakyThrows
  public List<String> getDefinedFields(String loggingFormatConfig, Map<String, String> additionalConfig) {
    String schemaFile = additionalConfig.get("schema");
    if (schemaFile == null) {
      throw new IllegalArgumentException("'schema' attribute is missing from protobuf logger additional config");
    }
    String eventType = additionalConfig.get("type");
    if (eventType == null) {
      throw new IllegalArgumentException("'type' attribute is missing from protobuf logger additional config");
    }

    List<String> specifiedFields = Arrays.stream(loggingFormatConfig.split(","))
        .map(String::trim)
        .collect(Collectors.toList());

    DynamicSchema schema = loadSchema(schemaFile);
    List<String> definedFields = schema.getMessageDescriptor(eventType).getFields().stream()
        .map(FieldDescriptor::getName)
        .collect(Collectors.toList());

    if (specifiedFields.isEmpty()) {
      return definedFields;
    }

    return definedFields.stream().filter(specifiedFields::contains).collect(Collectors.toList());
  }
}
