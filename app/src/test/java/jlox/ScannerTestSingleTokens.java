package jlox;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

class ScannerTestSingleTokens {
    @Test
    @DisplayName("Token test LEFT_PAREN")
    void testTokenLeftParen() {
        assertThatScannerGivenSource("(")
                .producesFirstTokenOfType(TokenType.LEFT_PAREN)
                .withLexeme("(")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test RIGHT_PAREN")
    void testTokenRightParen() {
        assertThatScannerGivenSource(")")
                .producesFirstTokenOfType(TokenType.RIGHT_PAREN)
                .withLexeme(")")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test LEFT_BRACE")
    void testTokenLeftBrace() {
        assertThatScannerGivenSource("{")
                .producesFirstTokenOfType(TokenType.LEFT_BRACE)
                .withLexeme("{")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test RIGHT_BRACE")
    void testTokenRightBrace() {
        assertThatScannerGivenSource("}")
                .producesFirstTokenOfType(TokenType.RIGHT_BRACE)
                .withLexeme("}")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test COMMA")
    void testTokenComma() {
        assertThatScannerGivenSource(",")
                .producesFirstTokenOfType(TokenType.COMMA)
                .withLexeme(",")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test DOT")
    void testTokenDot() {
        assertThatScannerGivenSource(".")
                .producesFirstTokenOfType(TokenType.DOT)
                .withLexeme(".")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test MINUS")
    void testTokenMinus() {
        assertThatScannerGivenSource("-")
                .producesFirstTokenOfType(TokenType.MINUS)
                .withLexeme("-")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test PLUS")
    void testTokenPlus() {
        assertThatScannerGivenSource("+")
                .producesFirstTokenOfType(TokenType.PLUS)
                .withLexeme("+")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test SEMICOLON")
    void testTokenSemicolon() {
        assertThatScannerGivenSource(";")
                .producesFirstTokenOfType(TokenType.SEMICOLON)
                .withLexeme(";")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test STAR")
    void testTokenStar() {
        assertThatScannerGivenSource("*")
                .producesFirstTokenOfType(TokenType.STAR)
                .withLexeme("*")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test BANG")
    void testTokenBang() {
        assertThatScannerGivenSource("!")
                .producesFirstTokenOfType(TokenType.BANG)
                .withLexeme("!")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test EQUAL")
    void testTokenEqual() {
        assertThatScannerGivenSource("=")
                .producesFirstTokenOfType(TokenType.EQUAL)
                .withLexeme("=")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test EQUAL_EQUAL")
    void testTokenEqualEqual() {
        assertThatScannerGivenSource("==")
                .producesFirstTokenOfType(TokenType.EQUAL_EQUAL)
                .withLexeme("==")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test BANG_EQUAL")
    void testTokenBangEqual() {
        assertThatScannerGivenSource("!=")
                .producesFirstTokenOfType(TokenType.BANG_EQUAL)
                .withLexeme("!=")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test LESS_EQUAL")
    void testTokenLessEqual() {
        assertThatScannerGivenSource("<=")
                .producesFirstTokenOfType(TokenType.LESS_EQUAL)
                .withLexeme("<=")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test GREATER_EQUAL")
    void testTokenGreaterEqual() {
        assertThatScannerGivenSource(">=")
                .producesFirstTokenOfType(TokenType.GREATER_EQUAL)
                .withLexeme(">=")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test SLASH")
    void testTokenSlash() {
        assertThatScannerGivenSource("/")
                .producesFirstTokenOfType(TokenType.SLASH)
                .withLexeme("/")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Comment is ignored")
    void testCommentIsIgnored() {
        var source = "1234 // this is a test comment";
        assertThatScannerGivenSource(source)
                .producesFirstTokenOfType(TokenType.NUMBER)
                .withLexeme("1234")
                .withLiteral(1234d)
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Whitespace is ignored")
    void testWhitespaceIsIgnored() {
        var source = "\n\n    1234   \n";
        assertThatScannerGivenSource(source)
                .producesFirstTokenOfType(TokenType.NUMBER)
                .withLexeme("1234")
                .withLiteral(1234d)
                .atLine(3)
                .followedByEofAtLine(4);
    }

    @Test
    @DisplayName("Token test NUMBER: zero")
    void testTokenZeroAsNonDecimalNumber() {
        assertThatScannerGivenSource("0")
                .producesFirstTokenOfType(TokenType.NUMBER)
                .withLexeme("0")
                .withLiteral(0d)
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test NUMBER: zero with decimal digit")
    void testTokenZeroAsDecimalNumber() {
        assertThatScannerGivenSource("0.0")
                .producesFirstTokenOfType(TokenType.NUMBER)
                .withLexeme("0.0")
                .withLiteral(0d)
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test NUMBER: non decimal number")
    void testTokenSimpleNumber() {
        assertThatScannerGivenSource("1234")
                .producesFirstTokenOfType(TokenType.NUMBER)
                .withLexeme("1234")
                .withLiteral(1234d)
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test NUMBER: decimal number")
    void testTokenDecimalNumber() {
        assertThatScannerGivenSource("1234.997")
                .producesFirstTokenOfType(TokenType.NUMBER)
                .withLexeme("1234.997")
                .withLiteral(1234.997d)
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test STRING")
    void testTokenString() {
        assertThatScannerGivenSource("\"this is a string TEST\"")
                .producesFirstTokenOfType(TokenType.STRING)
                .withLexeme("\"this is a string TEST\"")
                .withLiteral("this is a string TEST")
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test AND")
    void testTokenAnd() {
        assertThatScannerGivenSource("and")
                .producesFirstTokenOfType(TokenType.AND)
                .withLexeme("and")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test CLASS")
    void testTokenClass() {
        assertThatScannerGivenSource("class")
                .producesFirstTokenOfType(TokenType.CLASS)
                .withLexeme("class")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test ELSE")
    void testTokenElse() {
        assertThatScannerGivenSource("else")
                .producesFirstTokenOfType(TokenType.ELSE)
                .withLexeme("else")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test FALSE")
    void testTokenFalse() {
        assertThatScannerGivenSource("false")
                .producesFirstTokenOfType(TokenType.FALSE)
                .withLexeme("false")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test FUN")
    void testTokenFun() {
        assertThatScannerGivenSource("fun")
                .producesFirstTokenOfType(TokenType.FUN)
                .withLexeme("fun")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test FOR")
    void testTokenFor() {
        assertThatScannerGivenSource("for")
                .producesFirstTokenOfType(TokenType.FOR)
                .withLexeme("for")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test IF")
    void testTokenIf() {
        assertThatScannerGivenSource("if")
                .producesFirstTokenOfType(TokenType.IF)
                .withLexeme("if")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test NIL")
    void testTokenNil() {
        assertThatScannerGivenSource("nil")
                .producesFirstTokenOfType(TokenType.NIL)
                .withLexeme("nil")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test or")
    void testTokenOr() {
        assertThatScannerGivenSource("or")
                .producesFirstTokenOfType(TokenType.OR)
                .withLexeme("or")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test PRINT")
    void testTokenPrint() {
        assertThatScannerGivenSource("print")
                .producesFirstTokenOfType(TokenType.PRINT)
                .withLexeme("print")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test RETURN")
    void testTokenReturn() {
        assertThatScannerGivenSource("return")
                .producesFirstTokenOfType(TokenType.RETURN)
                .withLexeme("return")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test SUPER")
    void testTokenSuper() {
        assertThatScannerGivenSource("super")
                .producesFirstTokenOfType(TokenType.SUPER)
                .withLexeme("super")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test THIS")
    void testTokenThis() {
        assertThatScannerGivenSource("this")
                .producesFirstTokenOfType(TokenType.THIS)
                .withLexeme("this")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test TRUE")
    void testTokenTrue() {
        assertThatScannerGivenSource("true")
                .producesFirstTokenOfType(TokenType.TRUE)
                .withLexeme("true")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test VAR")
    void testTokenVar() {
        assertThatScannerGivenSource("var")
                .producesFirstTokenOfType(TokenType.VAR)
                .withLexeme("var")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    @DisplayName("Token test WHILE")
    void testTokenWhile() {
        assertThatScannerGivenSource("while")
                .producesFirstTokenOfType(TokenType.WHILE)
                .withLexeme("while")
                .withNoLiteral()
                .atLine(1)
                .followedByEofOnTheSameLine();
    }

    @Test
    void randomTest() {
        var source =
                "class Test {\n" +
                        "    fun testFunction() {\n" +
                        "        var one = 1;\n" +
                        "        var zero = 0.0;\n" +
                        "\n" +
                        "        if (one == zero) {\n" +
                        "            print \"well...\";\n" +
                        "            return -1;\n" +
                        "        } else {\n" +
                        "            print \"ok\";\n" +
                        "            return 1;\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    fun loopTest() {\n" +
                        "        var counter = 0;\n" +
                        "        while (counter < 10) {\n" +
                        "            counter = counter + 1;\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";
        List<Token> tokens = new Scanner(source).scanTokens();
        for (Token token : tokens) {
            System.err.println(token);
        }
    }

    private static ScannerAsserter assertThatScannerGivenSource(String source) {
        return new ScannerAsserter(source);
    }

    static class ScannerAsserter {
        private final String scannerSource;

        public ScannerAsserter(String scannerSource) {
            this.scannerSource = Objects.requireNonNull(scannerSource, "Can't assert for null scanner source.");
        }

        public FirstTokenAndEofAsserter producesFirstTokenOfType(TokenType tokenType) {
            return new FirstTokenAndEofAsserter(new Scanner(scannerSource), tokenType);
        }
    }

    static class FirstTokenAndEofAsserter {
        private final Scanner primedScanner;
        private final TokenType tokenType;
        private String lexeme;
        private Object literal;
        private Integer tokenLine;

        public FirstTokenAndEofAsserter(Scanner primedScanner, TokenType tokenType) {
            this.primedScanner = primedScanner;
            this.tokenType = tokenType;
        }

        public FirstTokenAndEofAsserter withLexeme(String lexeme) {
            this.lexeme = lexeme;
            return this;
        }

        public FirstTokenAndEofAsserter withNoLiteral() {
            this.literal = null;
            return this;
        }

        public FirstTokenAndEofAsserter withLiteral(Object literal) {
            this.literal = literal;
            return this;
        }

        public FirstTokenAndEofAsserter atLine(int line) {
            this.tokenLine = line;
            return this;
        }

        public void followedByEofOnTheSameLine() {
            followedByEofAtLine(tokenLine);
        }

        public void followedByEofAtLine(Integer eofLine) {
            validateAssertSetup(eofLine);
            var expectedToken = new Token(tokenType, lexeme, literal, tokenLine);
            var expectedEof = new Token(TokenType.EOF, "", null, eofLine);

            var tokens = primedScanner.scanTokens();
            var tokenIter = tokens.iterator();
            var softly = new SoftAssertions();

            var produced = tokenIter.next();
            softly.assertThat(produced).isEqualTo(expectedToken);

            var eof = tokenIter.next();
            softly.assertThat(eof).isEqualTo(expectedEof);

            softly.assertAll();
        }

        private void validateAssertSetup(Integer eofLine) {
            Objects.requireNonNull(tokenType, "Can't assert a null token type");
            Objects.requireNonNull(tokenLine, "Can't assert a null token line");
            Objects.requireNonNull(lexeme, "Can't assert a null lexeme");
            Objects.requireNonNull(eofLine, "Can't assert a null EOF line // possibly use followedByEofOnTheSameLine()");
        }
    }
}