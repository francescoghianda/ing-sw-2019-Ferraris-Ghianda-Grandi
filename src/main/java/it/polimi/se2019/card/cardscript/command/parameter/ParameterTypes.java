package it.polimi.se2019.card.cardscript.command.parameter;

public class ParameterTypes
{
    public static final PlayerParameter PLAYER = new PlayerParameter();
    public static final BlockParameter BLOCK = new BlockParameter();
    public static final PlayerOrBlockParameter PLAYER_OR_BLOCK = new PlayerOrBlockParameter();
    public static final DigitParameter DIGIT = new DigitParameter();
    public static final BooleanParameter BOOLEAN = new BooleanParameter();
    public static final LogicExpressionParameter LOGIC_EXPRESSION = new LogicExpressionParameter();
    public static final GenericParameter GENERIC = new GenericParameter();
    public static final StringParameter STRING = new StringParameter();


    public static EnumParameter ENUM(String... values)
    {
        return new EnumParameter(values);
    }

}
