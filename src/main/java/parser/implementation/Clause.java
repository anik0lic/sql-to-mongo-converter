package parser.implementation;

import lombok.Getter;
import lombok.Setter;
import parser.composite.AbstractClause;
import parser.implementation.additional.Operators;
import parser.implementation.enums.AggregateType;
import parser.implementation.enums.LogicType;
import parser.implementation.enums.OperatorsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Clause extends AbstractClause {
    private String type;
    private Map<AggregateType, String> aggregation;
    private List<Operators> operators;
    private List<LogicType> logic;
    private String tmp;
    private int flag = 0;

    public Clause(String type) {
        super();
        this.type = type;

        aggregation = new HashMap<>();
        operators = new ArrayList<>();
        logic = new ArrayList<>();
    }

    @Override
    public void prettyParameter(String p) {
        if(p.contains(",")) {
            p = p.replace(",", "");
        }
        if(p.contains("max(")){
            p = p.replace("max(", "");
            p = p.replace(")", "");
            aggregation.put(AggregateType.MAX, p);
            return;
        }
        else if(p.contains("min(")){
            p = p.replace("min(", "");
            p = p.replace(")", "");
            aggregation.put(AggregateType.MIN, p);
            return;
        }
        else if(p.contains("sum(")){
            p = p.replace("sum(", "");
            p = p.replace(")", "");
            aggregation.put(AggregateType.SUM, p);
            return;
        }
        else if(p.contains("count(")){
            p = p.replace("count(", "");
            p = p.replace(")", "");
            aggregation.put(AggregateType.COUNT, p);
            return;
        }
        else if(p.contains("avg(")){
            p = p.replace("avg(", "");
            p = p.replace(")", "");
            aggregation.put(AggregateType.AVG, p);
            return;
        }
        else if(p.contains("asc")){
            logic.add(LogicType.ASC);
            return;
        }
        else if(p.contains("desc")){
            logic.add(LogicType.DESC);
            return;
        }
        if(p.contains(")")) {
            p = p.replace(")", "");
        }
        if(p.contains("(")) {
            p = p.replace("(", "");
        }
        if(flag == 1){
            if(p.contains("\'")){
                p = p.replace("\'", "\"");
                tmp = tmp + " " + p;
                flag = 0;
                getParameters().add(tmp);
            } else{
                tmp = tmp + " " + p;
            }
            return;
        }
        if(p.contains("\'")) {      // 'united states of america'
            if(p.charAt(0) == '\'' && p.charAt(p.length()-1) == '\''){
                p = p.replace("\'", "\"");
                getParameters().add(p);
            }
            else{
                if(flag == 0) {
                    p = p.replace("\'", "\"");
                    tmp = p;
                    flag = 1;
                }
            }
            return;
        }

        getParameters().add(p);
    }

    public void prettierParameter(){
        int i = 1;

        while(i < getParameters().size()){
            Operators op = null;
            if(getParameters().get(i).equals("=")){
                if(isSubQuery(i+1)){
                    op = new Operators(OperatorsType.EQUAL, getParameters().get(i-1), null);
                }
                else{
                    op = new Operators(OperatorsType.EQUAL, getParameters().get(i-1), getParameters().get(i+1));
                }
                operators.add(op);
            }
            else if(getParameters().get(i).equals("<")){
                if(isSubQuery(i+1)){
                    op = new Operators(OperatorsType.LT, getParameters().get(i-1), null);
                }
                else{
                    op = new Operators(OperatorsType.LT, getParameters().get(i-1), getParameters().get(i+1));
                }
                operators.add(op);
            }
            else if(getParameters().get(i).equals(">")){
                if(isSubQuery(i+1)){
                    op = new Operators(OperatorsType.GT, getParameters().get(i-1), null);
                }
                else{
                    op = new Operators(OperatorsType.GT, getParameters().get(i-1), getParameters().get(i+1));
                }
                operators.add(op);
            }
            else if(getParameters().get(i).equals("<=")){
                if(isSubQuery(i+1)){
                    op = new Operators(OperatorsType.LTE, getParameters().get(i-1), null);
                }
                else{
                    op = new Operators(OperatorsType.LTE, getParameters().get(i-1), getParameters().get(i+1));
                }
                operators.add(op);
            }
            else if(getParameters().get(i).equals(">=")){
                if(isSubQuery(i+1)){
                    op = new Operators(OperatorsType.GTE, getParameters().get(i-1), null);
                }
                else{
                    op = new Operators(OperatorsType.GTE, getParameters().get(i-1), getParameters().get(i+1));
                }
                operators.add(op);
            }
            else if(getParameters().get(i).equals("like")){
                op = new Operators(OperatorsType.LIKE, getParameters().get(i - 1), getParameters().get(i + 1));
                operators.add(op);
            }
            else if(getParameters().get(i).equals("in")){
                if(isSubQuery(i+1)){
                    op = new Operators(OperatorsType.IN, getParameters().get(i-1), null);
                }
                else{
                    StringBuilder sb = new StringBuilder();
                    //in (20, 30, 40)
                    // in 20 30 40
                    // 20,30,40
                    op = new Operators(OperatorsType.IN, getParameters().get(i-1), null);
                    i++;
                    while(i < getParameters().size()){
                        if(getParameters().get(i).equals("and") || getParameters().get(i).equals("or")) {
                            i--;
                            break;
                        }
                        sb.append(getParameters().get(i));
                        sb.append(",");
                        i++;
                    }
                    sb.deleteCharAt(sb.length()-1);
                    op.setSecond(sb.toString());
                }
                operators.add(op);
            }
            else if(getParameters().get(i).equals("and")){
                logic.add(LogicType.AND);
            }
            else if(getParameters().get(i).equals("or")){
                logic.add(LogicType.OR);
            }
            i++;
        }
    }

    public boolean isSubQuery(int i) {
        return i == getParameters().size();
    }
}
