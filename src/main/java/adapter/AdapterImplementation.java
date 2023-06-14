package adapter;

import lombok.NoArgsConstructor;
import parser.composite.AbstractClause;
import parser.composite.Query;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class AdapterImplementation implements Adapter{
    private Query query;

    @Override
    public List<String> getMongoQuery(Query query) {
        this.query = query;
        return convertToMongo();
    }

    public List<String> convertToMongo(){
        ParameterConverter pc = new ParameterConverter(query);
        Map<String, String> queryMap = pc.convertParameters();

        Query subQuery = null;
        Map<String, String> subQueryMap;

        for(AbstractClause ac : query.getClauses()){
            if(ac instanceof Query){
                subQuery = (Query) ac;
            }
        }

        Mapper m;
        if(!(subQuery == null)){
            ParameterConverter pc2 = new ParameterConverter(subQuery);
            pc2.setSubQueryFlag(1);
            subQueryMap = pc2.convertParameters();
            m = new Mapper(queryMap, subQueryMap, "3");
        }
        else
            m = new Mapper(queryMap, pc.getMongoType());

        List<String> mongoList = m.getMongo();
        mongoList.add(m.getMongoType());

        return mongoList;
    }
}