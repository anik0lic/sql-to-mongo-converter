package parser.implementation.additional;

import lombok.Getter;
import lombok.Setter;
import parser.implementation.enums.OperatorsType;

@Getter
@Setter
public class Operators {
    private OperatorsType type;
    private String first;
    private String second;

    public Operators(OperatorsType type, String first, String second) {
        this.type = type;
        this.first = first;
        this.second = second;
    }
}
