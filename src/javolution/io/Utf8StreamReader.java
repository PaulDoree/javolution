/*
 * Javolution - Java(TM) Solution for Real-Time and Embedded Systems
 * Copyright (C) 2004 - The Javolution Team (http://javolution.org/)
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package javolution.io;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * <p> This class represents an UTF-8 stream reader.</p>
 *
 * <p> This reader supports surrogate <code>char</code> pairs (representing
 *     characters in the range [U+10000 .. U+10FFFF]). It can also be used
 *     to read characters unicodes (31 bits) directly
 *     (ref. {@link #read()}).</p>
 *
 * <p> Each invocation of one of the <code>read()</code> methods may cause one
 *     or more bytes to be read from the underlying byte-input stream.
 *     To enable the efficient conversion of bytes to characters, more bytes may
 *     be read ahead from the underlying stream than are necessary to satisfy
 *     the current read operation.</p>
 *
 * <p> Unlike <code>java.io.InputStreamReader</code> this class does not
 *     allocate new buffers (e.g. <code>java.nio.HeapCharBuffer</code>) each
 *     time a {@link #read} is performed and its execution speed is therefore
 *     greatly improved (twice as fast).</p>
 *
 * <p> Instances of this class can be reused for different input streams
 *     and can be part of a higher level component (e.g. parser) in order
 *     to avoid dynamic buffer allocation when the input source changes.
 *     Also wrapping using a <code>java.io.BufferedReader</code> is unnescessary
 *     as instances of this class embed their own data buffers.</p>
 *
 * <p> Note: This reader is unsynchronized and does not test if the UTF-8
 *           encoding is well-formed (e.g. UTF-8 sequences longer than
 *           necessary to encode a character).</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, October 4, 2004
 * @see     Utf8StreamWriter
 */
public final class Utf8StreamReader extends Reader {

    /**
     * Holds the current input stream or <code>null</code> if closed.
     */
    private InputStream _inStream;

    /**
     * Holds the start index.
     */
    private int _start;

    /**
     * Holds the end index.
     */
    private int _end;

    /**
     * Holds the bytes buffer.
     */
    private final byte[] _bytes;

    /**
     * Default constructor.
     */
    public Utf8StreamReader() {
        this(2048);
    }

    /**
     * Creates a {@link Utf8StreamReader} of specified buffer size.
     *
     * @param  bufferSize the buffer size in bytes.
     */
    public Utf8StreamReader(int bufferSize) {
        _bytes = new byte[bufferSize];
    }

    /**
     * Sets the input stream to use for reading until this reader is closed.
     * For example:<pre>
     *     Reader reader = new Utf8StreamReader().setInputStream(inStream);
     * </pre> is equivalent but reads twice as fast as <pre>
     *     Reader reader = new java.io.InputStreamReader(inStream, "UTF-8");
     * </pre>
     *
     * @param  inStream the input stream.
     * @return this UTF-8 reader.
     * @see    #close
     */
    public Utf8StreamReader setInputStream(InputStream inStream) {
        _inStream = inStream;
        return this;
    }

    /**
     * Indicates if this stream is ready to be read.
     *
     * @return <code>true</code> if the next read() is guaranteed not to block
     *         for input; <code>false</code> otherwise.
     * @throws  IOException if an I/O error occurs.
     */
    public boolean ready() throws IOException {
        if (_inStream != null) {
            return ((_end - _start) > 0) || (_inStream.available() != 0);
        } else {
            throw new IOException("Stream closed");
        }
    }

    /**
     * Closes the stream. Once a stream has been closed, further read(),
     * ready(), mark(), or reset() invocations will throw an IOException.
     * Closing a previously-closed stream, however, has no effect.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        if (_inStream != null) {
            _inStream.close();
            _start = 0;
            _end = 0;
            _code = 0;
            _moreBytes = 0;
            _inStream = null;
        }
    }

    /**
     * Reads a single character.  This method will block until a character is
     * available, an I/O error occurs, or the end of the stream is reached.
     *
     * @return the 31-bits Unicode of the character read, or -1 if the end of
     *         the stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    public int read() throws IOException {
        byte b = _bytes[_start];
        return ((b >= 0) && (_start++ < _end)) ? b : read2();
    }
    // Reads one full character, blocks if necessary.
    private int read2() throws IOException {
        if (_start < _end) {
            byte b = _bytes[_start++];

            // Decodes UTF-8.
            if ((b >= 0) && (_moreBytes == 0)) {
                // 0xxxxxxx
                return b;
            } else if (((b & 0xc0) == 0x80) && (_moreBytes != 0)) {
                // 10xxxxxx (continuation byte)
                _code = (_code << 6) | (b & 0x3f); // Adds 6 bits to code.
                if (--_moreBytes == 0) {
                    return _code;
                } else {
                    return read2();
                }
            } else if (((b & 0xe0) == 0xc0) && (_moreBytes == 0)) {
                // 110xxxxx
                _code = b & 0x1f;
                _moreBytes = 1;
                return read2();
            } else if (((b & 0xf0) == 0xe0) && (_moreBytes == 0)) {
                // 1110xxxx
                _code = b & 0x0f;
                _moreBytes = 2;
                return read2();
            } else if (((b & 0xf8) == 0xf0) && (_moreBytes == 0)) {
                // 11110xxx
                _code = b & 0x07;
                _moreBytes = 3;
                return read2();
            } else if (((b & 0xfc) == 0xf8) && (_moreBytes == 0)) {
                // 111110xx
                _code = b & 0x03;
                _moreBytes = 4;
                return read2();
            } else if (((b & 0xfe) == 0xfc) && (_moreBytes == 0)) {
                // 1111110x
                _code = b & 0x01;
                _moreBytes = 5;
                return read2();
            } else {
                throw new CharConversionException("Invalid UTF-8 Encoding");
            }
        } else { // No more bytes in buffer.
            if (_inStream != null) {
                _start = 0;
                _end = _inStream.read(_bytes, 0, _bytes.length);
                if (_end > 0) {
                    return read2(); // Continues.
                } else { // Done.
                    if (_moreBytes == 0) {
                        return -1;
                    } else { // Incomplete sequence.
                        throw new CharConversionException(
                            "Unexpected end of stream");
                    }
                }
            } else {
                throw new IOException("Stream closed");
            }
        }
    }
    private int _code;
    private int _moreBytes;

    /**
     * Reads characters into a portion of an array.  This method will block
     * until some input is available, an I/O error occurs, or the end of the
     * stream is reached.
     *
     * <p> Note: Characters between U+10000 and U+10FFFF are represented
     *     by surrogate pairs (two <code>char</code>).</p>
     *
     * @param  cbuf the destination buffer.
     * @param  off the offset at which to start storing characters.
     * @param  len the maximum number of characters to read
     * @return the number of characters read, or -1 if the end of the
     *         stream has been reached
     * @throws IOException if an I/O error occurs.
     */
    public int read(char cbuf[], int off, int len) throws IOException {
        if (_inStream != null) {
            if (_start >= _end) { // Fills buffer.
                _start = 0;
                _end = _inStream.read(_bytes, 0, _bytes.length);
                if (_end <= 0) { // Done.
                    return _end;
                }
            }
            final int off_plus_len = off + len;
            for (int i=off; i < off_plus_len;) {
                // assert(_start < _end)
                byte b = _bytes[_start];
                if ((b >= 0) && (++_start < _end)) {
                    cbuf[i++] = (char) b; // Most common case.
                } else if (b < 0) {
                    if (i < off_plus_len - 1) { // Up to two 'char' can be read.
                        int code = read2();
                        if (code < 0x10000) {
                            cbuf[i++] = (char)code;
                        } else if (code <= 0x10ffff) { // Surrogates.
                            cbuf[i++] = (char)
                                (((code - 0x10000) >> 10) + 0xd800);
                            cbuf[i++] = (char)
                                (((code - 0x10000) & 0x3ff) + 0xdc00);
                        } else {
                            throw new CharConversionException(
                                "Cannot convert U+" +
                                Integer.toHexString(code) +
                                " to char (code greater than U+10FFFF)");
                        }
                        if (_start < _end) {
                            continue;
                        }
                    }
                    return i - off;
                } else { // End of buffer (_start >= _end).
                    cbuf[i++] = (char) b;
                    return i - off;
                }
            }
            return len;
        } else {
            throw new IOException("Stream closed");
        }
    }
}