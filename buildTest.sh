#/bin/sh
java -Xmx500M -cp "$CLASSPATH" org.antlr.Tool -fo ./src/ -debug ./src/TigerLanguage.g
javac -d ./bin/ ./src/*.java
java -cp "$CLASSPATH:./bin/" TigerLanguageTestAST "./testAST/"$1
