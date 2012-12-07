
package com.badlogic.gdx;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.utils.JsonWriter;

/** Provides utility methods to work with the {@link HttpRequest} content and parameters. */
public class HttpParametersUtils {

	public static String defaultEncoding = "UTF-8";
	public static String nameValueSeparator = "=";
	public static String parameterSeparator = "&";

	/** Useful method to convert a map of key,value pairs to a String to be used as part of a GET or POST content.
	 * @param parameters A Map<String, String> with the parameters to encode.
	 * @return The String with the parameters encoded. */
	public static String convertHttpParameters (Map<String, String> parameters) {
		Set<String> keySet = parameters.keySet();
		StringBuffer convertedParameters = new StringBuffer();
		for (String name : keySet) {
			convertedParameters.append(encode(name, defaultEncoding));
			convertedParameters.append(nameValueSeparator);
			convertedParameters.append(encode(parameters.get(name), defaultEncoding));
			convertedParameters.append(parameterSeparator);
		}
		if (convertedParameters.length() > 0) convertedParameters.deleteCharAt(convertedParameters.length() - 1);
		return convertedParameters.toString();
	}

	private static String encode (String content, String encoding) {
		try {
			return URLEncoder.encode(content, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/** Takes a Map and converts it into json, which can be sent over http using header Content-Type = application/json
	 * @param parameters Map<String, ?> to output in json format
	 * @return String representing the parameters with json */
	public static String convertJsonHttpParameters (Map<String, ?> parameters) {
		StringWriter jsonText = new StringWriter();
		JsonWriter writer = new JsonWriter(jsonText);

		try {
			createJson(parameters, "", writer);
		} catch (IOException e) {
			return "";
		}
		return jsonText.toString();

	}

	/** Run this to fill writer with JSON based on the content */
	private static void createJson (Object content, String name, JsonWriter writer) throws IOException {
		if (content instanceof Map) {
			if (name == "")
				writer.object();
			else
				writer.object(name);
			Set<String> keySet = ((Map<String, ?>)content).keySet();
			for (String key : keySet) {
				createJson(((Map)content).get(key), key, writer);
			}
			writer.pop();
		} else if (content instanceof Object[]) {
			if (name == "")
				writer.array();
			else
				writer.array(name);
			for (Object key : (Object[])content) {
				createJson(key, "", writer);
			}
			writer.pop();
		} else {
			if (name == "")
				writer.value(content);
			else
				writer.set(name, content);
		}
	}
}
