package validator;

import observer.Publisher;
import parser.composite.Query;

public interface Validator extends Publisher {
    boolean validate(Query query);
}
