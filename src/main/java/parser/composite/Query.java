package parser.composite;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Query extends AbstractClause {
    List<AbstractClause> clauses;

    public Query() {
        clauses = new ArrayList<>();
    }

    @Override
    public void prettyParameter(String parameter) {

    }

}
