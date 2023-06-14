package validator.rules;

import parser.composite.AbstractClause;
import parser.composite.Query;
import parser.implementation.Clause;

public class MandatoryClausesRule extends AbstractRule {

    public MandatoryClausesRule() {
        super("MandatoryClauses", "You need select and from clause");
    }

    @Override
    public boolean checkRule(Query query) {
        int selectCnt = 0;
        int fromCnt = 0;
        int subQueryFlag = 0;
        for(AbstractClause cl : query.getClauses()){
            if(cl instanceof Clause){
                Clause clause = (Clause)cl;
                if(clause.getType().equals("select"))
                    selectCnt++;
                else if(clause.getType().equals("from"))
                    fromCnt++;
            }
            else if(cl instanceof Query){
                Query subQuery = (Query)cl;
                subQueryFlag = 1;
                for(AbstractClause scl : subQuery.getClauses()){
                    if(scl instanceof Clause){
                        Clause clause = (Clause)scl;
                        if(clause.getType().equals("select"))
                            selectCnt++;
                        else if(clause.getType().equals("from"))
                            fromCnt++;
                    }
                }
            }
        }

        if(subQueryFlag == 0)
            return (selectCnt == 1 && fromCnt == 1);
        else if(subQueryFlag == 1)
            return (selectCnt == 2 && fromCnt == 2);

        return false;
    }
}
