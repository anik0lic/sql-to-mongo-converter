package validator.rules;

import parser.composite.AbstractClause;
import parser.composite.Query;
import parser.implementation.Clause;

public class GroupByRule extends AbstractRule{

    public GroupByRule() {
        super("GroupByRule", "Everything that is selected and isn't in aggregate function must be in group by");
    }

    @Override
    public boolean checkRule(Query query) {
        Clause select = null;
        Clause groupBy = null;
        for(AbstractClause cl : query.getClauses()){
            if(cl instanceof Clause){
                Clause clause = (Clause) cl;
                if(clause.getType().equals("select")) {
                    select = clause;
                    if(select.getAggregation().isEmpty()){
                        return true;
                    }
                    else{
                        if(select.getParameters().isEmpty())
                            return true;
                    }
                }
                else if(clause.getType().equals("group"))
                    groupBy = clause;
            }
        }
        if(groupBy == null)
            return false;

        return select.getParameters().equals(groupBy.getParameters());
    }
}
