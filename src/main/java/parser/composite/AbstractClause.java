package parser.composite;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class AbstractClause {
    protected List<String> parameters;

    protected AbstractClause() {
        parameters = new ArrayList<>();
    }

    public abstract void prettyParameter(String parameter);
}
