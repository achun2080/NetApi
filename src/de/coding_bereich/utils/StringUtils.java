package de.coding_bereich.utils;

public class StringUtils
{
	static public StringBuilder escapeXmlCdata(String text)
	{
		StringBuilder buf = new StringBuilder(text.length() + 32);

		buf.append("<![CDATA[");
		buf.append(text.replaceAll("]]>", "]]]]><![CDATA[>"));
		buf.append("]]>");

		return buf;
	}
}
