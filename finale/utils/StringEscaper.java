package finale.utils;

import java.util.Locale;

public class StringEscaper {
	public static String escapeJavaScript(String str) {
		if (str == null) {
			return null;
		}

		StringBuffer writer = new StringBuffer(str.length() * 2);

		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);

			// handle unicode
			if (ch > 0xfff) {
				writer.append("\\u");
				writer.append(hex(ch));
			} else if (ch > 0xff) {
				writer.append("\\u0");
				writer.append(hex(ch));
			} else if (ch > 0x7f) {
				writer.append("\\u00");
				writer.append(hex(ch));
			} else if (ch < 32) {
				switch (ch) {
				case '\b':
					writer.append('\\');
					writer.append('b');
					break;
				case '\n':
					writer.append('\\');
					writer.append('n');
					break;
				case '\t':
					writer.append('\\');
					writer.append('t');
					break;
				case '\f':
					writer.append('\\');
					writer.append('f');
					break;
				case '\r':
					writer.append('\\');
					writer.append('r');
					break;
				default:
					if (ch > 0xf) {
						writer.append("\\u00");
						writer.append(hex(ch));
					} else {
						writer.append("\\u000");
						writer.append(hex(ch));
					}
					break;
				}
			} else {
				switch (ch) {
				case '\'':
					// If we wanted to escape for Java strings then we would
					// not need this next line.
					writer.append('\\');
					writer.append('\'');
					break;
				case '"':
					writer.append('\\');
					writer.append('"');
					break;
				case '\\':
					writer.append('\\');
					writer.append('\\');
					break;
				default:
					writer.append(ch);
					break;
				}
			}
		}

		return writer.toString();
	}

	private static String hex(char ch) {
		return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
	}
}
