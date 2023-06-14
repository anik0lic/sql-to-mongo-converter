package adapter;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Mapper {
    private Map<String, String> queryMap;
    private Map<String, String> subQueryMap;
    private String mongoType;

    public Mapper(Map<String, String> query, Map<String, String> subQuery, String mongoType) {
        this.queryMap = query;
        this.subQueryMap = subQuery;
        this.mongoType = mongoType;
    }

    public Mapper(Map<String, String> query, String mongoType) {
        this.queryMap = query;
        this.mongoType = mongoType;
    }

    public List<String> getMongo(){
        List<String> parameters = new ArrayList<>();
        parameters.add(queryMap.get("from"));

        if(mongoType.equals("1")){
            parameters.add(queryMap.get("where"));
            parameters.add(queryMap.get("select"));
            parameters.add(queryMap.get("order"));
        }
        else if(mongoType.equals("2")){
            parameters.add("{$lookup: " + queryMap.get("join") + "}");
            parameters.add("{$unwind: \"$1\" }");
            parameters.add("{$match: " + queryMap.get("where") + "}");
            parameters.add("{$project: " + queryMap.get("select") + "}");

            if(!queryMap.get("order").equals("{}")){
                parameters.add("{$sort: " + queryMap.get("order") + "}");
            }
        }
        else if(mongoType.equals("3")){
            parameters.add("{$lookup: {from: \"" + subQueryMap.get("from") + "\", localField: \"" + queryMap.get("where") +
                    "\", foreignField: \"" + subQueryMap.get("select2") + "\", as: \"1\"}}");
            parameters.add("{$unwind: \"$1\" }");
            parameters.add("{$match: " + subQueryMap.get("where") + "}");

            if(queryMap.get("select").contains(",  }")){
                parameters.add("{$project: " + queryMap.get("select").replace(",", " ") + "}");
            }
            else
                parameters.add("{$project: " + queryMap.get("select").replace(", ", ", \"1.").replace(" :", "\":") + "}");

            if(!queryMap.get("order").equals("{}")){
                parameters.add("{$sort: " + queryMap.get("order") + "}");
            }
        }
        else if(mongoType.equals("4")){
            if(queryMap.get("group").equals("{}")) {
                parameters.add("{$group: {" + queryMap.get("agr") + ", _id: null}}");
                parameters.add("{$project: " + queryMap.get("select").replace("}", "") + ", agr: 1}}");
            }
            else {
                parameters.add("{$group: " + queryMap.get("group") + "}");
                parameters.add("{$project: " + queryMap.get("select") + "}");
            }

            parameters.add("{$match: " + queryMap.get("where") + "}");

            if(!queryMap.get("order").equals("{}")){
                parameters.add("{$sort: " + queryMap.get("order") + "}");
            }
        }
        else if(mongoType.equals("5")){
            parameters.add("{$lookup: " + queryMap.get("join") + "}");
            parameters.add("{$unwind: \"$1\" }");
            parameters.add("{$match: " + queryMap.get("where") + "}");
            parameters.add("{$group: " + queryMap.get("group").replace(":\"$", ":\"$1.") + "}");
            parameters.add("{$project: " + queryMap.get("select") + "}");

            if(!queryMap.get("order").equals("{}")){
                parameters.add("{$sort: " + queryMap.get("order") + "}");
            }
        }

        return parameters;
    }
}
