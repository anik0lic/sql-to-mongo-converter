package parser;

import lombok.Getter;
import lombok.Setter;
import parser.composite.AbstractClause;
import parser.composite.Query;
import parser.implementation.*;
import validator.ValidatorImplementation;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ParserImplementation implements Parser{
    private List<String> keyWords;
    private Query query;
    private Query subQuery = null;
    private String[] parts;
    private int subQueryFlag;
    private int opened;
    private int closed;

    public ParserImplementation() {
        keyWords = Arrays.asList("select", "from", "where", "group", "order", "join", "(select", "full");
    }

//select * from hr.employees join hr.departments using (department_id)  where department_name like 'Con%'

//select first_name, last_name from hr.employees where department_id =
//    (select department_id from hr.employees where last_name like 'King' and first_name like 'Steven')

//select first_name, last_name from hr.employees where salary = (select max(salary) from hr.employees)

    @Override
    public Query parsing(String string){
        parts = string.split(" ");
        query = new Query();

        int i = 0;
        subQueryFlag = 0;
        opened = 0;
        closed = 0;

        while(i < parts.length) {
            switch (parts[i].toLowerCase()) {
                case "select":
                case "from":
                case "where":
                case "join":
                case "full":
                case "group":
                case "order":
                    Clause clause = new Clause(parts[i].toLowerCase());
                    if(subQueryFlag == 0)
                        query.getClauses().add(clause);
                    else if(subQueryFlag == 1)
                        subQuery.getClauses().add(clause);

                    if(parts[i].equalsIgnoreCase("order") || parts[i].equalsIgnoreCase("group") || parts[i].equalsIgnoreCase("full"))
                        i++;

                    i++;
                    while (!keyWords.contains(parts[i].toLowerCase())) {
                        if(subQueryFlag == 1){
                            countParentheses(parts[i]);
                            if(opened == closed)
                                subQueryFlag = 0;
                        }
                        clause.prettyParameter(parts[i]);
                        i++;
                        if(i == parts.length)
                            break;
                    }
                    break;
                case "(select":
                    subQueryFlag = 1;
                    opened = 1;

                    subQuery = new Query();
                    query.getClauses().add(subQuery);

                    String s = parts[i].replace("(", "");
                    Clause select = new Clause(s.toLowerCase());
                    subQuery.getClauses().add(select);

                    i++;
                    while (!keyWords.contains(parts[i].toLowerCase())) {
                        countParentheses(parts[i].toLowerCase());
                        select.prettyParameter(parts[i].toLowerCase());
                        i++;
                    }
                    break;
            }
        }

        for(AbstractClause cl : query.getClauses()){
            if(cl instanceof Clause) {
                Clause clause = (Clause) cl;
                if(clause.getType().equals("where")) {
                    clause.prettierParameter();
                    break;
                }
            }
        }

        if(subQuery != null){
            for(AbstractClause cl : subQuery.getClauses()){
                if(cl instanceof Clause) {
                    Clause clause = (Clause) cl;
                    if(clause.getType().equals("where")) {
                        clause.prettierParameter();
                        break;
                    }
                }
            }
        }

        return query;
    }

    public void countParentheses(String s){
        if(s.contains("("))
            opened++;
        else if(s.contains(")"))
            closed++;
    }
}
