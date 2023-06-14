package validator.rules;

import parser.composite.AbstractClause;
import parser.composite.Query;
import parser.implementation.Clause;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class OrderRule extends AbstractRule{
    List<String> keyWords = Arrays.asList("select", "from", "join", "full", "where", "group", "order");
    Stack<String> stack = new Stack<>();

    public OrderRule() {
        super("Order", "Clause order is not right");
    }
    @Override
    public boolean checkRule(Query query) {
        for(AbstractClause ac : query.getClauses()){
            if(ac instanceof Clause){
                Clause cl = (Clause) ac;
                if (stack.isEmpty() || keyWords.indexOf(cl.getType()) > keyWords.indexOf(stack.peek())) {
                    stack.push(cl.getType());
                } else {
                    stack = new Stack<>();
                    return false;
                }
            }
        }
        stack = new Stack<>();
        return true;
    }
}
