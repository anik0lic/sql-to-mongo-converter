package validator.rules;

import lombok.Getter;
import lombok.Setter;
import parser.composite.Query;

@Getter
@Setter
public abstract class AbstractRule {
    private String name;
    private String message;

    public AbstractRule(String name) {
        this.name = name;
    }

    public AbstractRule(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public abstract boolean checkRule(Query query);
}
