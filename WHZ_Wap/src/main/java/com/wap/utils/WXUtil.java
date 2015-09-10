package com.wap.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 寰�淇″��浼�骞冲�板伐��风被
 * 
 * @author sailor
 * @comment
 * @corp xingyi
 * @date 2014-9-21
 */
public class WXUtil {
	
	
	/**
	 * ��版��娴�杈����
	 * 
	 * @param outputStream
	 * @param text
	 * @return
	 */
	public static boolean outputStreamWrite(OutputStream outputStream, String text) {
		try {
			outputStream.write(text.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
