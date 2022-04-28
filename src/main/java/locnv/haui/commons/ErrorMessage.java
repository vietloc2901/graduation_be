package locnv.haui.commons;

public enum ErrorMessage {
    PARSE_XML_ERROR("Error when parsing xml"),
    IO_AND_PARSE("IOException Or ParseException"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    IO_AND_REFLECTIVE_EXCEPTION("IOException OR ReflectiveException");
    private final String message;

    ErrorMessage(String errorMessage){
        this.message = errorMessage;
    }

    public String getErrorMessage(){
        return message;
    }
}
