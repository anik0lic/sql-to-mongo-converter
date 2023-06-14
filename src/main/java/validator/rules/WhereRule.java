package validator.rules;

import parser.composite.AbstractClause;
import parser.composite.Query;
import parser.implementation.Clause;

public class WhereRule extends AbstractRule{
    public WhereRule() {
        super("WhereRule");
    }

    @Override
    public boolean checkRule(Query query) {
        for(AbstractClause cl : query.getClauses()){
            if(cl instanceof Clause){
                Clause clause = (Clause) cl;
                if(clause.getType().equals("where")){
                    if(!(clause.getAggregation().isEmpty())){
                        setMessage("You can't have aggregate function in where clause.");
                        return false;
                    }
                    if(clause.getOperators().isEmpty()){
                        setMessage("Where clause doesn't have operators");
                        return false;
                    }
                    if(clause.getLogic().size() > 2){
                        setMessage("Where clause can't have more than 2 logic operators");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
