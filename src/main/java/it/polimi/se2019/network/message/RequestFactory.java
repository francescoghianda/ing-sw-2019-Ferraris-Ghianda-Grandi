package it.polimi.se2019.network.message;

public class RequestFactory
{

    private RequestFactory()
    {

    }

    public static ActionRequest newActionRequest(String message, ActionFunction function)
    {
        return new ActionRequest(message, function);
    }

    public static CancellableActionRequest newCancellableActionRequest(String message, CancellableActionFunction function)
    {
        return new CancellableActionRequest(message, function);
    }
}
