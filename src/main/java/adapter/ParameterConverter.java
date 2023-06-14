package adapter;

import lombok.Getter;
import lombok.Setter;
import parser.composite.AbstractClause;
import parser.composite.Query;
import parser.implementation.Clause;
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
public class ParameterConverter {
    private Query query;
    private Map<String, String> parameters;
    private int subQueryFlag = 0;
    private String mongoType;

    //tip: 1 - jednostavan upit
    //tip: 2 - join upit
    //tip: 3 - podupit
    //tip: 4 - agregacija+group

    public ParameterConverter(Query query) {
        this.query = query;
        parameters = new HashMap<>(Map.of("where", "{}", "order", "{}", "group", "{}"));
        mongoType = "1";
    }

    public Map<String, String> convertParameters(){
        for(AbstractClause cl : query.getClauses()){
            if(cl instanceof Clause){
                Clause clause = (Clause) cl;

                switch (clause.getType()) {
                    case "select":
                        if (subQueryFlag == 1) {
                            parameters.put("select2", clause.getParameters().get(0));
                        }

                        StringBuilder select = new StringBuilder("{ _id: 0, ");
                        for (String p : clause.getParameters()) {
                            if (p.equals("*")) {
                                select.append("  ");
                                break;
                            }
                            if (p.contains(".")) {
                                String[] parts = p.split("\\.");
                                p = "\"1." + parts[1] + "\"";
                            }
                            select.append(p).append(" : 1, ");
                        }
                        select.deleteCharAt(select.length() - 2);
                        select.append("}");
                        parameters.put(clause.getType(), String.valueOf(select));

                        //agregacija
                        if (!(clause.getAggregation().isEmpty())) {
                            mongoType = "4";
                            StringBuilder aggregation = new StringBuilder("agr: ");
                            String key = null;
                            AggregateType at = null;
                            for (AggregateType k : clause.getAggregation().keySet()) {
                                at = k;
                                key = k.toString().toLowerCase();
                            }
                            aggregation.append("{ $").append(key).append(": \"$").append(clause.getAggregation().get(at)).append("\" }");
                            parameters.put("agr", aggregation.toString());
                        }
                        break;
                    case "from":
                        parameters.put(clause.getType(), clause.getParameters().get(0));
                        break;
                    case "where":
                        List<String> operatorString = new ArrayList<>();

                        for (Operators o : clause.getOperators()) {
                            StringBuilder where = new StringBuilder("{ ");

                            if (o.getType().equals(OperatorsType.EQUAL)) {
                                if (o.getSecond() == null) {
                                    where = new StringBuilder();
                                    where.append(o.getFirst());
                                } else {
                                    if (o.getFirst().contains(".")) {
                                        String[] parts = o.getFirst().split("\\.");
                                        o.setFirst("\"1." + parts[1] + "\"");
                                    }
                                    where.append(o.getFirst()).append(": ").append(o.getSecond()).append("}");
                                }
                            } else if (o.getType().equals(OperatorsType.LIKE)) {
                                where.append(o.getFirst()).append(": ");
                                String s = o.getSecond(); // /%A%/
                                s = s.replace("\"", "/");
                                if (s.charAt(1) == '%' && s.charAt(s.length() - 2) == '%') {
                                    s = s.replace("%", "");
                                } else if (s.charAt(1) == '%') { //  /%a/
                                    s = s.replace("%", ""); //  /a/
                                    s = s.replace("/", "");
                                    s = "/" + s + "$" + "/";
                                } else if (s.charAt(s.length() - 2) == '%') { //a%
                                    s = s.replace("%", "");
                                    s = s.replace("/", "");
                                    s = "/" + "^" + s + "/";
                                }

                                if (subQueryFlag == 1) {
                                    where = new StringBuilder(where.toString().replace("{ ", "{\"1."));
                                    where = new StringBuilder(where.toString().replace(":", "\":"));
                                }
                                where.append(s).append(" }");

                            } else if (o.getType().equals(OperatorsType.IN)) {
                                if (o.getSecond() == null) {
                                    where = new StringBuilder();
                                    where.append(o.getFirst());
                                } else {
                                    where.append(o.getFirst()).append(": {$").append(o.getType().toString().toLowerCase()).append(":[").append(o.getSecond()).append("]}}");
                                }
                            } else {
                                if (o.getSecond() == null) {
                                    where = new StringBuilder();
                                    where.append(o.getFirst());
                                } else {
                                    where.append(o.getFirst()).append(": {$").append(o.getType().toString().toLowerCase()).append(": ").append(o.getSecond()).append(" }");
                                }
                            }

                            operatorString.add(where.toString());
                        }

                        if (clause.getLogic().isEmpty()) {
                            parameters.put(clause.getType(), operatorString.get(0));
                        } else {
                            StringBuilder logic = new StringBuilder("{$");
                            logic.append(clause.getLogic().get(0).toString().toLowerCase()).append(": [").append(operatorString.get(0)).append(", ");
                            if (clause.getLogic().size() > 1) {
                                logic.append("{$").append(clause.getLogic().get(1).toString().toLowerCase()).append(": [")
                                        .append(operatorString.get(1)).append(", ").append(operatorString.get(2)).append("]}]}");
                            } else {
                                logic.append(operatorString.get(1)).append("]}");
                            }
                            parameters.put(clause.getType(), logic.toString());
                        }
                        break;
                    case "order":
                        StringBuilder order = new StringBuilder("{ ");

                        if (clause.getLogic().isEmpty()) {
                            for (String p : clause.getParameters()) {
                                order.append(p).append(": 1, ");
                            }
                        } else {
                            int i = 0;
                            for (String p : clause.getParameters()) {
                                order.append(p);
                                if (clause.getLogic().get(i).equals(LogicType.ASC)) {
                                    order.append(": 1, ");
                                    i++;
                                } else if (clause.getLogic().get(i).equals(LogicType.DESC)) {
                                    order.append(": -1, ");
                                    i++;
                                }
                            }
                        }

                        order.deleteCharAt(order.length() - 2);
                        order.append("}");
                        parameters.put(clause.getType(), order.toString());
                        break;
                    case "group":
                        if (parameters.containsKey("agr")) {
                            StringBuilder group = new StringBuilder("{ ");
                            StringBuilder selectGroup = new StringBuilder("{ _id:0, ");

                            group.append(parameters.get("agr")).append(", ");
                            group.append("_id: { ");

                            for (String s : cl.getParameters()) {
                                if(s.contains(".")) {
                                    String[] parts = s.split("\\.");
                                    group.append(parts[1]).append(":").append("\"$").append(parts[1]).append("\"").append(", ");
                                    selectGroup.append(parts[1]).append(": ").append("\"$_id.").append(parts[1]).append("\"").append(", ");
                                }
                                else{
                                    group.append(s).append(":").append("\"$").append(s).append("\"").append(", ");
                                    selectGroup.append(s).append(": ").append("\"$_id.").append(s).append("\"").append(", ");
                                }
                            }

                            selectGroup.append("agr: 1}");
                            parameters.remove("select");
                            parameters.put("select", selectGroup.toString());

                            group.deleteCharAt(group.length() - 2);
                            group.append("}}");
                            parameters.put("group", group.toString());
                        }
                        break;
                    case "join":
                        if(mongoType.equals("4"))
                            mongoType = "5";
                        else
                            mongoType = "2";

                        StringBuilder join = new StringBuilder("{");
                        join.append("from: \"").append(clause.getParameters().get(0)).append("\", ");
                        join.append("foreignField: \"");

                        if (clause.getParameters().contains("using")) {
                            join.append(clause.getParameters().get(2)).append("\", ");
                            join.append("localField: \"").append(clause.getParameters().get(2)).append("\", ");
                            join.append("as: \"1\"}");
                        } else {
                            //on
                        }

                        parameters.put("join", join.toString());
                        break;
                }
            }
        }

        return parameters;
    }
}
