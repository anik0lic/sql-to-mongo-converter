package parser;

import parser.composite.Query;

public interface Parser {
    Query parsing(String query);
}
