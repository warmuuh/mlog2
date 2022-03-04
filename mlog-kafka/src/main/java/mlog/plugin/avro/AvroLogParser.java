package mlog.plugin.avro;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.logging.LogParser;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;

@RequiredArgsConstructor
public class AvroLogParser implements LogParser {

  private final String channelName;
  private final Schema schema;
  private final int offset;

  @Override
  @SneakyThrows
  public Message parse(byte[] logline) {
    GenericDatumReader reader = new GenericDatumReader(schema);
    ByteBuffer buffer = ByteBuffer.wrap(logline);
    int start = offset;
    int length = buffer.limit() - offset;
    GenericRecord record = (GenericRecord) reader.read(null, DecoderFactory.get().binaryDecoder(buffer.array(), start, length, null));

    Map<String, String> fieldValues = record.getSchema().getFields().stream()
        .collect(Collectors.toMap(
            f -> f.name(),
            f -> {
              Object value = record.get(f.name());
              return value != null ? value.toString() : "";
            }
        ));
    return new Message(channelName, fieldValues);
  }

  @Override
  public Message parse(byte[] logline, Message prevMessage) {
    return parse(logline);
  }
}
