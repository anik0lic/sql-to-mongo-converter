package validator.rules;

import parser.composite.AbstractClause;
import parser.composite.Query;
import parser.implementation.Clause;

public class JoinRule extends AbstractRule{

    public JoinRule() {
        super("JoinRule", "You need condition USING/ON in join clause.");
    }

    @Override
    public boolean checkRule(Query query) {
        for(AbstractClause cl : query.getClauses()){
            if(cl instanceof Clause){
                Clause clause = (Clause) cl;
                if(clause.getType().contains("join"))
                    return clause.getParameters().contains("on") || clause.getParameters().contains("using");
            }
        }
        return true;
    }
}
