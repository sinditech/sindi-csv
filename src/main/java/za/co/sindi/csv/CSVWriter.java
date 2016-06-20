/**
 * 
 */
package za.co.sindi.csv;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * @author Bienfait Sindi
 * @since 27 February 2013
 *
 */
public interface CSVWriter extends Flushable, Closeable {

	public void write(char[] fieldCharacters) throws IOException;
	public void write(String field) throws IOException;
	public void write(String[] fields) throws IOException;
	public void writeLine(String[] fields) throws IOException;
	public void writeNewLine() throws IOException;
}
