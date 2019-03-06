/**
 * PyScanner.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 * 
 * License:
 * Please read the LICENSE file in this distribution for details regarding
 * the licensing of this code. This code is freely available for educational
 * use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * 
 * Description:
 * The PyScanner class provides a lexical analyzer for the PyParser class. 
 * When getToken is called on the scanner, the next token of the input file
 * is returned. The CoCo assembly language is LL(1). This means that we can 
 * parse it with a recursive descent parser by looking ahead by one symbol. 
 * The putBackToken method will put the last token back so it can be
 * retrieved again later with getToken. Calling putBackToken multiple times
 * has not accumulative effect. Only the last token read can be put back.
 */

package jcoco;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import jcoco.PyToken.TokenType;

public class PyScanner {
    /**
     * These are the keywords for the scanner.
     */
    public enum KeyWords {
        BEGIN ("BEGIN"),
        END ("END"),
        CLASS ("Class");
        
        private String keyword;
        KeyWords(String keyword) {
            this.keyword = keyword;
        }
        
        public String getKeyword() {
            return this.keyword;
        }
    }

    /**
     * The constructor for the PyScaner class with class attributes.
     * 
     * @param in an InputStream taken from the CoCo file to be read
     */
    private PushbackInputStream in;
    private boolean needToken;
    private int colCount;
    private int lineCount;
    private PyToken lastToken;
 
    public PyScanner(PushbackInputStream in){
        this.in = in;
        this.needToken = true;
        this.colCount = -1;
        this.lineCount = 1;
        this.lastToken = null;
    }
    
    public boolean isLetter(char c) {
        return (Character.isLetter(c) || c == '_' || c == '@' || c == '<' || c == '>');
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
    
    public PyToken getToken() {
        int state = 0;
        boolean foundOne = false;
        char c;
        String lex = "";
        boolean error = false;
        int column = -1;
        int line = -1; 
        int k;
        int next;
        TokenType type = null;
        
        if (!this.needToken) {
            this.needToken = true;
            return this.lastToken;
        }
        
        try {
            next = this.in.read();
            if (next == -1) {
                type = TokenType.PYBADTOKEN;
                foundOne = true;
            }
            
            while (!foundOne) {
                this.colCount++;
                c = (char) next;
                switch (state) {
                    case 0:
                        lex = "";
                        column = this.colCount;
                        line = this.lineCount;
                        if (isLetter(c)) state = 1;
                        else if (Character.isDigit(c)) state = 2;
                        else if (c == '-') state = 11;
                        else if (c == ':') state = 3;
                        else if (c == ',') state = 4;
                        else if (c == '\'') state = 6;
                        else if (c == '"') state = 7;
                        else if (c == '/') state = 8;
                        else if (c == '(') state = 9;
                        else if (c == ')') state = 10;
                        else if (c == ';') state = 12;
                        else if (next == -1) {
                            foundOne = true;
                            type = TokenType.PYEOFTOKEN;
                        } else if (c == '\n') {
                            this.colCount = -1;
                            this.lineCount ++;
                        } else if (Character.isWhitespace(c)) {
                        } else {
                            if (!error) {
                                error = true;
                                System.err.println("Unrecognized Character "+c+
                                        " found at line "+line+" and column "+
                                        column+".");
                            }
                            type = TokenType.PYBADTOKEN;
                            lex = Character.toString(c); 
                            foundOne = true;
                        }
                        break;
                    case 1:
                        if (isLetter(c) || Character.isDigit(c)) state = 1;
                        else {
                            for (KeyWords word : KeyWords.values()) {
                                if ( lex.equals(word.getKeyword()) ) {
                                    foundOne = true;
                                    type = TokenType.PYKEYWORDTOKEN;
                                }
                            }
                            if (!foundOne) {
                                type = TokenType.PYIDENTIFIERTOKEN;
                                foundOne = true;
                            }
                        }
                        break;
                    case 2:
                        if (Character.isDigit(c)) state = 2;
                        else if (c == '.') state = 5;
                        else {
                            type = TokenType.PYINTEGERTOKEN;
                            foundOne = true;
                        }
                        break;
                    case 3: 
                        type = TokenType.PYCOLONTOKEN;
                        foundOne = true;
                        break;
                    case 4:
                        type = TokenType.PYCOMMATOKEN;
                        foundOne = true;
                        break;
                    case 5:
                        if (Character.isDigit(c)) state = 5;
                        else {
                            type = TokenType.PYFLOATTOKEN;
                            foundOne = true;
                        }
                        break;
                    case 6:                             
                        if (c == '\'') {
                            type = TokenType.PYSTRINGTOKEN;
                            lex += c;
                            // Remove wrapping quotes
                            lex = lex.substring(1, lex.length()-1);
                            next = this.in.read();
                            foundOne = true;

                            if (next == -1) {
                                type = TokenType.PYBADTOKEN;
                            }
                        } else {
                            if (next == -1) {
                                type = TokenType.PYBADTOKEN;
                            }
                        }
                        break;
                    case 7:
                        if (c == '"') {
                            type = TokenType.PYSTRINGTOKEN;

                            lex = lex + c;
                            //eliminate the quotes on each end.
                            lex = lex.substring(1, lex.length() - 1);
                            next = this.in.read();
                            foundOne = true;

                        } else {
                            if (next == -1) {
                                type = TokenType.PYBADTOKEN;
                                foundOne = true;
                            }
                        }
                        break;
                    case 8:
                        foundOne = true;
                        type = TokenType.PYSLASHTOKEN;
                        break;
                    case 9:
                        foundOne = true;
                        type = TokenType.PYLEFTPARENTOKEN;
                        break;
                    case 10:
                        foundOne = true;
                        type = TokenType.PYRIGHTPARENTOKEN;
                        break;
                    case 11: 
                        if (Character.isDigit(c)) state = 2;
                        else {
                            type = TokenType.PYBADTOKEN;
                            foundOne = true;                    
                        }
                        break;
                    case 12: 
                        if (c == '\n' || next == -1) {
                            this.colCount = -1;
                            this.lineCount++;
                            state = 0;
                            lex = "";
                        }
                        break;
                }
                if (!foundOne) {
                    lex += c;
                    next = this.in.read();
                    if (next == -1) {
                        type = TokenType.PYEOFTOKEN;
                        foundOne = true;
                    }
                }
            }
            this.in.unread((char)next);
            this.colCount--;
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
        PyToken t = new PyToken(type, lex, line, column);

        this.lastToken = t;
        return t;
    }
    
    public void putBackToken() {
        needToken = false;
    }
}