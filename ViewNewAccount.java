package guiNewAccount;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import passwordEvaluationTestbedMain.PasswordEvaluationTestingAutomation;
import userNameRecognizerTestbed.UserNameRecognizer;
/*******
* <p> Title: ViewNewAccount Class. </p>
*
* <p> Description: The ViewNewAccount Page is used to enable a potential user with an invitation
* code to establish an account after they have specified an invitation code on the standard login
* page. </p>
*
* <p> Copyright: Lynn Robert Carter © 2025 </p>
*
* @author Lynn Robert Carter
*
* @version 1.00		2025-08-19 Initial version
* 
*/
public class ViewNewAccount {
	
	/*-********************************************************************************************
	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// This is a simple GUI login Page, very similar to the FirstAdmin login page.  The only real
	// difference is in this case we also know an email address, since it was used to send the
	// invitation to the potential user.
	private static Label label_ApplicationTitle =
			new Label("Foundation Application Account Setup Page");
   protected static Label label_NewUserCreation = new Label(" User Account Creation.");
   protected static Label label_NewUserLine = new Label("Please enter a username and a password.");
   protected static TextField text_Username = new TextField();
   protected static PasswordField text_Password1 = new PasswordField();
   protected static PasswordField text_Password2 = new PasswordField();
   protected static Button button_UserSetup = new Button("User Setup");
   protected static TextField text_Invitation = new TextField();
	// Username validation labels
	protected static Label label_UsernameStartAlpha = new Label();
	protected static Label label_UsernameAllowedChars = new Label();
	protected static Label label_UsernameSpecialBetween = new Label();
	protected static Label label_UsernameLength = new Label();
	// Password validation labels
	protected static Label label_UpperCase = new Label();
	protected static Label label_LowerCase = new Label();
	protected static Label label_NumericDigit = new Label();
	protected static Label label_SpecialChar = new Label();
	protected static Label label_LongEnough = new Label();
	protected static Label label_ShortEnough = new Label();
	// This alert is used should the invitation code be invalid
   protected static Alert alertInvitationCodeIsInvalid = new Alert(AlertType.INFORMATION);
	// This alert is used should the user enter two passwords that do not match
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);
   protected static Button button_Quit = new Button("Quit");
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewNewAccount theView;		// Is instantiation of the class needed?
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	protected static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
 
   protected static String theInvitationCode;	// The invitation code links to an email address
   											// and a role for this user
   protected static String emailAddress;		// Established here for use by the controller
   protected static String theRole;			// Established here for use by the controller
	public static Scene theNewAccountScene = null;	// Access to the User Update page's GUI Widgets
	
	private void setupValidationLabel(Label label, double x, double y, String text) {
	    label.setFont(Font.font("Arial", 12));
	    label.setMinWidth(400);
	    label.setAlignment(Pos.BASELINE_LEFT);
	    label.setLayoutX(x);
	    label.setLayoutY(y);
	    label.setText(text);
	    label.setTextFill(Color.RED);
	}
	
	/*-********************************************************************************************
	Constructors
	
	*/
	/**********
	 * <p> Method: displayNewAccount(Stage ps, String ic) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the NewAccount page to be displayed.
	 *
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 *
	 * It then checks to see if the page has been setup.  If not, it instantiates the class,
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 *
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param ic specifies the user's invitation code for this GUI and it's methods
	 *
	 */
	public static void displayNewAccount(Stage ps, String ic) {
		// This is the only way some component of the system can cause a New User Account page to
		// appear.  The first time, the class is created and initialized.  Every subsequent call it
		// is reused with only the elements that differ being initialized.
		
		// Establish the references to the GUI and the current user
		theStage = ps;				// Save the reference to the Stage for the rest of this package
		theInvitationCode = ic;		// Establish the invitation code so it can be easily accessed
		
		if (theView == null) theView = new ViewNewAccount();
		
		text_Username.setText("");	// Clear the input fields so previously entered values do not
		text_Password1.setText("");	// appear for a new user
		text_Password2.setText("");
		
		// Reset validation labels
		resetUsernameValidation();
		resetPasswordValidation();
		
		// Fetch the role for this user
		theRole = theDatabase.getRoleGivenAnInvitationCode(theInvitationCode);
		
		if (theRole.length() == 0) {// If there is an issue with the invitation code, display a
			alertInvitationCodeIsInvalid.showAndWait();	// dialog box saying that are when it it
			return;					// acknowledged, return so the proper code can be entered
		}
		
		Label headingUsername = new Label("Username Requirements:");					//Display for users
		headingUsername.setStyle("-fx-font-weight: bold;");
		headingUsername.setTextFill(Color.BLACK);
		Label headingPassword = new Label("Password Requirements:");
		headingPassword.setStyle("-fx-font-weight: bold;");
		headingPassword.setTextFill(Color.BLACK);
		
		setupLabelUI(headingUsername, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 200);
		setupLabelUI(headingPassword, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 410);
		
		// Get the email address associated with the invitation code
		emailAddress = theDatabase.getEmailAddressUsingCode(theInvitationCode);
		
   	// Place all of the established GUI elements into the pane
   	theRootPane.getChildren().clear();
   	theRootPane.getChildren().addAll(label_NewUserCreation, label_NewUserLine, text_Username,
   			text_Password1, text_Password2, button_UserSetup, button_Quit,
				label_UsernameStartAlpha, headingUsername, headingPassword,label_UsernameAllowedChars, label_UsernameSpecialBetween,
				label_UsernameLength, label_UpperCase, label_LowerCase, label_NumericDigit,
				label_SpecialChar, label_LongEnough, label_ShortEnough);    	
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: New User Account Setup");	
       theStage.setScene(theNewAccountScene);
		theStage.show();
	}
	
	/**********
	 * <p> Constructor: ViewNewAccount() </p>
	 *
	 * <p> Description: This constructor is called just once, the first time a new account needs to
	 * be created.  It establishes all of the common GUI widgets for the page so they are only
	 * created once and reused when needed.
	 *
	 * The do
	 * 		
	 */
	private ViewNewAccount() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theNewAccountScene = new Scene(theRootPane, width, height);
		
		// Label the Panle with the name of the startup screen, centered at the top of the pane
		setupLabelUI(label_ApplicationTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
		
   	// Label to display the welcome message for the new user
   	setupLabelUI(label_NewUserCreation, "Arial", 32, width, Pos.CENTER, 0, 10);
	
   	// Label to display the  message for the first user
   	setupLabelUI(label_NewUserLine, "Arial", 24, width, Pos.CENTER, 0, 70);
		
		// Establish the text input operand asking for a username
		setupTextUI(text_Username, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 160, true);
		text_Username.setPromptText("Enter the Username");
		
		// Add listener for username validation
		text_Username.textProperty().addListener((observable, oldValue, newValue) -> {
			validateUsername(newValue);
		});
		
		// Setup username validation labels
		setupValidationLabel(label_UsernameStartAlpha, 50, 220, "Start with alphabet - Not yet satisfied");
		setupValidationLabel(label_UsernameAllowedChars, 50, 240, "Allowed: A-Z, a-z, 0-9, ., _, - - Not yet satisfied");
		setupValidationLabel(label_UsernameSpecialBetween, 50, 260, "Special chars only between alphanumeric - Not yet satisfied");
		setupValidationLabel(label_UsernameLength, 50, 280, "4-16 characters long - Not yet satisfied");
		
		// Establish the text input operand field for the password
		setupTextUI(text_Password1, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 330, true);
		text_Password1.setPromptText("Enter the Password");
		
		// Add listener for password validation
		text_Password1.textProperty().addListener((observable, oldValue, newValue) -> {
			validatePassword(newValue);
		});
		
		// Establish the text input operand field to confirm the password
		setupTextUI(text_Password2, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 370, true);
		text_Password2.setPromptText("Enter the Password Again");
		
		// Setup password validation labels
		setupValidationLabel(label_UpperCase, 50, 430, "At least one upper case letter - Not yet satisfied");
		setupValidationLabel(label_LowerCase, 50, 450, "At least one lower case letter - Not yet satisfied");
		setupValidationLabel(label_NumericDigit, 50, 470, "At least one numeric digit - Not yet satisfied");
		setupValidationLabel(label_SpecialChar, 50, 490, "At least one special character - Not yet satisfied");
		setupValidationLabel(label_LongEnough, 50, 510, "At least eight characters - Not yet satisfied");
		setupValidationLabel(label_ShortEnough, 50, 530, "Less than 32 characters - Not yet satisfied");
		
		// If the invitation code is wrong, this alert dialog will tell the user
		alertInvitationCodeIsInvalid.setTitle("Invalid Invitation Code");
		alertInvitationCodeIsInvalid.setHeaderText("The invitation code is not valid.");
		alertInvitationCodeIsInvalid.setContentText("Correct the code and try again.");
		// If the passwords do not match, this alert dialog will tell the user
		alertUsernamePasswordError.setTitle("Passwords Do Not Match");
		alertUsernamePasswordError.setHeaderText("The two passwords must be identical.");
		alertUsernamePasswordError.setContentText("Correct the passwords and try again.");
       // Set up the account creation and login
       setupButtonUI(button_UserSetup, "Dialog", 18, 200, Pos.CENTER, 475, 180);
       button_UserSetup.setOnAction((event) -> {
			if (validatePasswordFinal(text_Password1.getText())) {
				if (text_Password1.getText().equals(text_Password2.getText())) {
					if (validateUsernameFinal(text_Username.getText())) {
						ControllerNewAccount.doCreateUser();
					} else {
						Alert usernameError = new Alert(AlertType.ERROR);
						usernameError.setTitle("Invalid Username");
						usernameError.setHeaderText("Username does not meet requirements");
						usernameError.setContentText("Please fix the username validation errors.");
						usernameError.showAndWait();
					}
				} else {
					alertUsernamePasswordError.showAndWait();
				}
			} else {
				Alert passwordError = new Alert(AlertType.ERROR);
				passwordError.setTitle("Invalid Password");
				passwordError.setHeaderText("Password does not meet requirements");
				passwordError.setContentText("Please fix the password validation errors.");
				passwordError.showAndWait();
			}
		});
		
       // Enable the user to quit the application
       setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
       button_Quit.setOnAction((event) -> {ControllerNewAccount.performQuit(); });
	}
	
	/*-********************************************************************************************
	Validation Methods
	
	*/
	
	private static void validateUsername(String username) {
		// Check if starts with alphabet
		boolean startsWithAlpha = !username.isEmpty() && Character.isLetter(username.charAt(0));
		updateValidationLabel(label_UsernameStartAlpha, startsWithAlpha, "Start with alphabet");
		
		if (username.isEmpty()) {
	        // Force invalid when empty
	        updateValidationLabel(label_UsernameAllowedChars, false, "Allowed: A-Z, a-z, 0-9, ., _, -");
	        updateValidationLabel(label_UsernameSpecialBetween, false, "Special chars only between alphanumeric");
	    }
		
		else {
		// Check allowed characters
		boolean allowedChars = username.matches("^[a-zA-Z0-9._-]*$");
		updateValidationLabel(label_UsernameAllowedChars, allowedChars, "Allowed: A-Z, a-z, 0-9, ., _, -");
		
		// Check special characters only between alphanumeric
		boolean specialBetween = true;
		if (username.length() > 1) {
			for (int i = 0; i < username.length(); i++) {
				char c = username.charAt(i);
				if (c == '.' || c == '_' || c == '-') {
					if (i == 0 || i == username.length() - 1 ||
						!Character.isLetterOrDigit(username.charAt(i-1)) ||
						!Character.isLetterOrDigit(username.charAt(i+1))) {
						specialBetween = false;
						break;
					}
				}
			}
		}
		updateValidationLabel(label_UsernameSpecialBetween, specialBetween, "Special chars only between alphanumeric");
		}
		
		// Check length
		boolean validLength = username.length() >= 4 && username.length() <= 16;
		updateValidationLabel(label_UsernameLength, validLength, "4-16 characters long");
	}
	
	private static void validatePassword(String password) {
		// Use the existing Model class for password validation
		PasswordEvaluationTestingAutomation.evaluatePassword(password);
		
		updateValidationLabel(label_UpperCase, PasswordEvaluationTestingAutomation.foundUpperCase, "At least one upper case letter");
		updateValidationLabel(label_LowerCase, PasswordEvaluationTestingAutomation.foundLowerCase, "At least one lower case letter");
		updateValidationLabel(label_NumericDigit, PasswordEvaluationTestingAutomation.foundNumericDigit, "At least one numeric digit");
		updateValidationLabel(label_SpecialChar, PasswordEvaluationTestingAutomation.foundSpecialChar, "At least one special character");
		updateValidationLabel(label_LongEnough, PasswordEvaluationTestingAutomation.foundLongEnough, "At least eight characters");
		updateValidationLabel(label_ShortEnough, PasswordEvaluationTestingAutomation.foundShortEnough, "Less than 32 characters");
	}
	
	private static void updateValidationLabel(Label label, boolean isValid, String baseText) {
		if (isValid) {
			label.setText(baseText + " - Satisfied");
			label.setTextFill(Color.GREEN);
		} else {
			label.setText(baseText + " - Not yet satisfied");
			label.setTextFill(Color.RED);
		}
	}
	
	private static void resetUsernameValidation() {
		updateValidationLabel(label_UsernameStartAlpha, false, "Should start with alphabet");
		updateValidationLabel(label_UsernameAllowedChars, false, "Allowed: A-Z, a-z, 0-9, ., _, -");
		updateValidationLabel(label_UsernameSpecialBetween, false, "Special chars only between alphanumeric");
		updateValidationLabel(label_UsernameLength, false, "4-16 characters long");
	}
	
	private static void resetPasswordValidation() {
		updateValidationLabel(label_UpperCase, false, "At least one upper case letter");
		updateValidationLabel(label_LowerCase, false, "At least one lower case letter");
		updateValidationLabel(label_NumericDigit, false, "At least one numeric digit");
		updateValidationLabel(label_SpecialChar, false, "At least one special character");
		updateValidationLabel(label_LongEnough, false, "At least eight characters");
		updateValidationLabel(label_ShortEnough, false, "Less than 32 characters");
	}
	
	protected static boolean validatePasswordFinal(String password) {
		String result = PasswordEvaluationTestingAutomation.evaluatePassword(password);
		return result.isEmpty();
	}
	
	protected static boolean validateUsernameFinal(String username) {
		String result = UserNameRecognizer.checkForValidUserName(username);
		return result.isEmpty();
	}
	
	/*-********************************************************************************************
	Helper methods to reduce code length
	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 *
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	
}

