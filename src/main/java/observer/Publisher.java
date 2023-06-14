package observer;

import java.io.IOException;

public interface Publisher {
    void addSubscriber(Subscriber subscriber);
    void removeSubscriber(Subscriber subscriber);
    void notify(Object notification);
}
