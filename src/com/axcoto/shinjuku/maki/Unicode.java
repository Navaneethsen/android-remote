package com.axcoto.shinjuku.maki;

public class Unicode {
	public static String convert(String input) {
		String s = input;

		s = s.replaceAll("[èéẹẻẽêềếệểễ]", "e");
		s = s.replaceAll("[ùúụủũưừứựửữ]", "u");
		s = s.replaceAll("[ìíịỉĩ]", "i");
		s = s.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
		s = s.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");

		s = s.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E");
		s = s.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U");
		s = s.replaceAll("[ÌÍỊỈĨ]", "I");
		s = s.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A");
		s = s.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O");
		return s;
	}
}
