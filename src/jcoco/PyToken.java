/**
 * PyScanner.java
 * 
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 * 
 * License:
 * Please read the LICENSE file in this distribution for details regarding
 * the licensing of this code. This code is freely available for educational
 * use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * 
 * Description:
 * The PyToken class defines the tokens that are read in from the scanner and
 * returned to the parser when CoCo reads and parses a casm file. 
 */

package jcoco;

public class PyToken {
    /**
     * Defines the types of tokens accepted by the JCoCo virtual machine. 
     */
    public enum TokenType {
        PYIDENTIFIERTOKEN,
        PYINTEGERTOKEN,
        PYFLOATTOKEN,
        PYSTRINGTOKEN,
        PYKEYWORDTOKEN,
        PYCOLONTOKEN,
        PYCOMMATOKEN,
        PYSLASHTOKEN,
        PYLEFTPARENTOKEN,
        PYRIGHTPARENTOKEN,
        PYEOFTOKEN,
        PYBADTOKEN
    }

    private String lexeme;
    private TokenType type;
    private int line;
    private int col;
    
    /**
     * The constructor method for PyToken. It defines the token read from the 
     * scanner and is passed to the parser. 
     * 
     * @param type  the type of Token 
     * @param lex   the lexeme for this token
     * @param line  the line the token was read from
     * @param col   the column the token was read from
     */
    public PyToken(TokenType type, String lex, int line, int col) {
        this.lexeme = lex;
        this.type = type;
        this.line = line;
        this.col = col;
    }
    
    /**
     * Returns the type of the token, in integer form. See TOKENS enum for 
     * reference. 
     * @return int
     */
    public TokenType getType() {
        return this.type;
    }
    
    /**
     * Returns the lexeme (word) of the token.
     * @return String
     */
    public String getLex() {
        return this.lexeme;
    }
    
    /**
     * Returns the column number on which the token was found. 
     * @return int
     */
    public int getCol() {
        return this.col;
    }
    
    /**
     * Returns the line number on which the token was found.
     * @return int
     */
    public int getLine() {
        return this.line;
    }
}
