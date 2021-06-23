package bolomagic.in.AdaptorAndParse;

public class withdrawalMethodsParse {
    private final String methodID;
    private final String methodImageURL;
    private final String methodName;
    private final String methodValue;

    public withdrawalMethodsParse(String methodID, String methodImageURL, String methodName, String methodValue) {
        this.methodID = methodID;
        this.methodImageURL = methodImageURL;
        this.methodName = methodName;
        this.methodValue = methodValue;
    }

    public String getMethodID() {
        return methodID;
    }

    public String getMethodImageURL() {
        return methodImageURL;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodValue() {
        return methodValue;
    }
}