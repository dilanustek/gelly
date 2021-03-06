package capsensor;

/**
 * Created by dilanustek on 7/22/16.
 */
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    private static final char DEFAULT_SEPARATOR = ',';

    public static void writeLine(Writer w, List<String> values) throws IOException {
        if (values != null)
            writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }

        if (sb != null) {
            sb.append("\n");
            w.append(sb.toString());
        }


    }

    public static void writeCsv(Integer gridAddress, Float capValue)
            throws IOException {

        List<String> list = new ArrayList<>();
        list.add(gridAddress.toString());
        list.add(capValue.toString());

        CSVUtil.writeLine(PortUtil.writer, list);
        PortUtil.writer.flush();
    }

}