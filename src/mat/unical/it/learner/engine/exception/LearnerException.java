package mat.unical.it.learner.engine.exception;

public class LearnerException extends Exception {

    private String message = "";

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public LearnerException(String m) {
	super();
	message = m;
    }

}
