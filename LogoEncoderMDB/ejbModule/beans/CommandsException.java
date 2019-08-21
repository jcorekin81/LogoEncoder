package beans;

public class CommandsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4411184807192502149L;
	String message;

	//----------------------------------------------
	// Default constructor - initializes instance variable to unknown

	  public CommandsException()
	  {
	    super();             // call superclass constructor
	    message = "No apprpriate commands";
	  }
	  

	//-----------------------------------------------
	// Constructor receives some kind of message that is saved in an instance variable.

	  public CommandsException(String err)
	  {
	    super(err);     // call super class constructor
	    message = err;  // save message
	  }
	  

	//------------------------------------------------  
	// public method, callable by exception catcher. It returns the error message.

	  public String getError()
	  {
	    return message;
	  }
	
}
