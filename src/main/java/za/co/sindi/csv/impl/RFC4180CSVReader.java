/**
 * 
 */
package za.co.sindi.csv.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import za.co.sindi.csv.CSVReader;
import za.co.sindi.csv.Constants;

/**
 * This class implements the <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a> Specification. It doesn't support comments.
 * 
 * @author Bienfait Sindi
 * @since 23 February 2013
 *
 */
public class RFC4180CSVReader implements CSVReader {

	private static enum QUOTE_STATE { UNQUOTED, QUOTED, DOUBLEQUOTED }
	private static final Logger LOGGER = Logger.getLogger(RFC4180CSVReader.class.getName());
	private BufferedReader reader;
	private char delimiter;
	private char quoteCharacter;
	
	//Internals
	private int rowIndex = 0;
	private int columnIndex = 0;
	private boolean closed = false;
	private String[] values = null;
	
	/**
	 * @param reader
	 */
	public RFC4180CSVReader(Reader reader) {
		this(reader, Constants.COMMA, Constants.DOUBLE_QUOTE);
	}
	
	/**
	 * @param reader
	 * @param delimiter
	 * @param quoteCharacter
	 */
	public RFC4180CSVReader(Reader reader, char delimiter, char quoteCharacter) {
		super();
		if (reader == null) {
			throw new IllegalArgumentException("A reader is required.");
		}
		
		if (reader instanceof BufferedReader) {
			this.reader = (BufferedReader)reader;
		} else {
			this.reader = new BufferedReader(reader);
		}
		
		if (delimiter == quoteCharacter) {
			throw new IllegalArgumentException("The delimiter and quote character cannot be the same.");
		}
		
		this.delimiter = delimiter;
		this.quoteCharacter = quoteCharacter;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		// TODO Auto-generated method stub
		ensureOpened();
		values = null;
		
		try {
			boolean isCr = false;
			QUOTE_STATE quote = QUOTE_STATE.UNQUOTED;
			List<String> valueList = new ArrayList<String>();
			
			String value = "";
			int c = -1;
			while ((c = reader.read()) != -1) {
				//First, increment column
				columnIndex++;
				if (quote == QUOTE_STATE.QUOTED) {
					if (c == quoteCharacter) {
						quote = QUOTE_STATE.DOUBLEQUOTED;
						continue;
					}
					
					value += (char) c;
				}
				
				if (quote == QUOTE_STATE.DOUBLEQUOTED) {
					if (c == quoteCharacter) {
						value += quoteCharacter;
						quote = QUOTE_STATE.QUOTED;
						continue;
					}
					
					quote = QUOTE_STATE.UNQUOTED;
				}
				
				if (quote == QUOTE_STATE.UNQUOTED) {
					if (c == quoteCharacter) {
						quote = QUOTE_STATE.QUOTED;
						continue;
					}
					
					if (c == delimiter || (isCr && c == Constants.LF)) {
//						if (!value.isEmpty()) {
							valueList.add(value);
							value = "";
//						}
						
						if (c == Constants.LF) {
							isCr = false;
							break;
						}
						
						continue;
					}
					
					if (c == Constants.CR) {
						isCr = true;
						continue;
					}
					
					value += (char)c;
				}
			}
			
			if (!value.isEmpty()) {
				valueList.add(value);
				value = "";
			}
			
			if (valueList != null && !valueList.isEmpty()) {
				values = valueList.toArray(new String[valueList.size()]);
				//clear
				valueList.clear();
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Error while reading character (last row " + rowIndex +", last column " + columnIndex + ").", e);
		}
		
		return (values != null && values.length > 0);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public String[] next() {
		// TODO Auto-generated method stub
		return values;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Method not supported.");
	}

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		// TODO Auto-generated method stub
		if (closed) return;
		if (reader != null) {
			reader.close();
			reader = null;
			closed = true;
		}
	}
	
	private void ensureOpened() {
		if (closed) {
			throw new IllegalStateException("CSV reader is closed.");
		}
	}
	
//	public static void main(String[] args) throws IOException {
////		String s = "\"e\"\"ee\"";
//		String s = "\"aaa\",\"b \r\nbb\",\"ccc\"\r\nzzz,yyy,xxx\r\n\"ddd\",\"e\"\"ee\",\"fff\"";
////		System.out.println(s);
//		CSVReader reader = new RFC4180CSVReader(new StringReader(s));
//		while (reader.hasNext()) {
//			String[] values = reader.next();
//			for (String value : values) {
//				System.out.print(value + " ");
//			}
//			System.out.println();
//		}
//		reader.close();
//	}
}
