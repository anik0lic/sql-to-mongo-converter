package validator;

import observer.Subscriber;
import parser.composite.Query;
import validator.rules.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidatorImplementation implements Validator {
    private List<AbstractRule> rules;
    private List<Subscriber> subscribers;

    public ValidatorImplementation() {
        subscribers = new ArrayList<>();
        rules = Arrays.asList(new MandatoryClausesRule(), new OrderRule(), new JoinRule(), new WhereRule(), new GroupByRule());
    }

    @Override
    public boolean validate(Query query){
        for(AbstractRule rule : rules) {
            if(!rule.checkRule(query)){
                notify(rule.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public void addSubscriber(Subscriber subscriber) {
        if(subscriber == null)
            return;
        if(this.subscribers ==null)
            this.subscribers = new ArrayList<>();
        if(this.subscribers.contains(subscriber))
            return;
        this.subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(Subscriber subscriber) {
        if(subscriber == null || this.subscribers == null || !this.subscribers.contains(subscriber))
            return;
        this.subscribers.remove(subscriber);
    }

    @Override
    public void notify(Object notification) {
        if(notification == null || this.subscribers == null || this.subscribers.isEmpty())
            return;

        for(Subscriber sub : subscribers){
            sub.update(notification);
        }
    }
}
