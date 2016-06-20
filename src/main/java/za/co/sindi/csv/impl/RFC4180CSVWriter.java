/**
 * 
 */
package za.co.sindi.csv.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import za.co.sindi.csv.CSVWriter;
import za.co.sindi.csv.Constants;

/**
 * @author Bienfait Sindi
 * @since 28 February 2013
 *
 */
public class RFC4180CSVWriter implements CSVWriter {

	private BufferedWriter writer;
	private char delimiter;
	private char quoteCharacter;
	
	//Internals
	private boolean closed = false;
	private int columnsWrittenCount = 0;
	
	/**
	 * @param writer
	 */
	public RFC4180CSVWriter(Writer writer) {
		this(writer, Constants.COMMA, Constants.DOUBLE_QUOTE);
	}

	/**
	 * @param writer
	 * @param delimiter
	 * @param quoteCharacter
	 */
	public RFC4180CSVWriter(Writer writer, char delimiter, char quoteCharacter) {
		super();
		if (writer == null) {
			throw new IllegalArgumentException("A writer is required.");
		}
		
		if (delimiter == quoteCharacter) {
			throw new IllegalArgumentException("The delimiter and quote character cannot be the same.");
		}
		
		if (writer instanceof BufferedWriter) {
			this.writer = (BufferedWriter) writer;
		} else {
			this.writer = new BufferedWriter(writer);
		}
		this.delimiter = delimiter;
		this.quoteCharacter = quoteCharacter;
	}

	/* (non-Javadoc)
	 * @see java.io.Flushable#flush()
	 */
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		writer.flush();
	}

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		// TODO Auto-generated method stub
		if (closed) return;
		if (writer != null) {
			flush();
			writer.close();
			closed = true;
		}
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.csv.CSVWriter#write(char[])
	 */
	public void write(char[] fieldCharacters) throws IOException {
		// TODO Auto-generated method stub
		ensureOpened();
		
		if (fieldCharacters == null) {
			return ;
		}
		
		boolean quoteNeeded = false;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < fieldCharacters.length; i++) {
			char c = fieldCharacters[i];
			
			if (!quoteNeeded) {
				quoteNeeded = (c == quoteCharacter || c == delimiter || (c == Constants.LF && (i > 0 && fieldCharacters[i - 1] == Constants.CR)));
			}
			
			if (c == quoteCharacter) {
				sb.append(quoteCharacter);
			}
			
			sb.append(c);
		}
		
		if (columnsWrittenCount > 0) {
			writer.write(delimiter);
		}
		
		if (quoteNeeded) {
			writer.write(quoteCharacter);
		}
		
		writer.write(sb.toString());
		
		if (quoteNeeded) {
			writer.write(quoteCharacter);
		}
		
		//Finally
		columnsWrittenCount++;
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.csv.CSVWriter#write(java.lang.String)
	 */
	public void write(String field) throws IOException {
		// TODO Auto-generated method stub
		if (field == null) {
			return ;
		}
		
		write(field.toCharArray());
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.csv.CSVWriter#write(java.lang.String[])
	 */
	public void write(String[] fields) throws IOException {
		// TODO Auto-generated method stub
		if (fields == null /* || fields.length == 0 */) {
			//Don't waste our time....
			return;
		}
		
		for (String field : fields) {
			write(field);
		}
	}
	
	/* (non-Javadoc)
	 * @see za.co.sindi.csv.CSVWriter#writeLine(java.lang.String[])
	 */
	public void writeLine(String[] fields) throws IOException {
		// TODO Auto-generated method stub
		int currentColumnsWrittenCount = columnsWrittenCount;
		write(fields);
		if (columnsWrittenCount != currentColumnsWrittenCount) {
			writeNewLine();
		}
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.csv.CSVWriter#writeNewLine()
	 */
	public void writeNewLine() throws IOException {
		// TODO Auto-generated method stub
		ensureOpened();
		writer.write(Constants.CRLF);
		columnsWrittenCount = 0;
	}
	
	private void ensureOpened() {
		if (closed) {
			throw new IllegalStateException("CSV reader is closed.");
		}
	}
	
//	public static void main(String[] args) {
//		try {
//			StringWriter sw = new StringWriter();
//			RFC4180CSVWriter writer = new RFC4180CSVWriter(sw);
//			writer.write("aaa");
//			writer.write("bbb");
//			writer.write("ccc");
//			writer.writeNewLine();
//			writer.write("ddd" + Constants.CR + Constants.LF);
//			writer.write("e\"ee");
//			writer.write("\"fff\"");
//			
//			System.out.println(sw.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
